/*
 * Copyright 2023 malyshev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.napilnik.truffletests.vm;

import java.util.Date;
import net.napilnik.truffletests.objects.Pair;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author malyshev
 */
public class ContextNestingTest {

    private static void executeNesting(String testName, Nesting outerNesting, Nesting innerNesting) throws VMException {
        Date now = new Date();
        System.out.println("-ts- Test: %1$s --------- <start: %2$tH:%2$tM:%2$tS> --------".formatted(testName, now));
        try (VMContext context = VM.context("OuterContext", outerNesting)) {

            long iterations = 10000;
            StringBuilder sb = new StringBuilder();
            for (long i = 0; i < iterations; i++) {
                String fooName = "foo" + Long.toString(i);

                String foo = "function %s(){return;}".formatted(fooName);
                sb.append(foo).append('\n');

            }
            context.eval(new VMScript("foo.js", sb.toString()));

            Date fullfill = new Date();
            System.out.println("-ts- Test: %1$s -------------- <fullfill: %2$tH:%2$tM:%2$tS, len: %3$dms> ---".formatted(testName, fullfill, fullfill.getTime() - now.getTime()));

            try (VMContext nestedContext = VM.context("NestedContext", context, innerNesting)) {

            }

            Date nested = new Date();
            System.out.println("-ts- Test: %1$s -------------- <nested: %2$tH:%2$tM:%2$tS, len: %3$dms> ---".formatted(testName, nested, nested.getTime() - fullfill.getTime()));

            System.out.println("-te- Test: %1$s --------- <end: %2$tH:%2$tM:%2$tS, len: %3$dms> --------\n".formatted(testName, now, nested.getTime() - now.getTime()));
        }
    }

    @Test
    public void testNaiveNesting() {
        try {
            VMScript script = new VMScript("testNaiveNesting.js",
                    """
                        function getAnswer() {
                             return 41;
                        }

                        function getAnswerPair() {
                            return new Pair('answer', 41);
                        }
                    """);

            VMScript scriptNested = new VMScript("testNaiveNestingNested.js",
                    """
                        function getAnswer() {
                             return 42;
                        }

                        function getAnswerPair() {
                            return new Pair('answer', 42);
                        }
                    """);

            try (VMContext context = VM.context("OuterContext", Nesting.Naive)) {
                context.eval(script);

                Integer result = context.eval("getAnswer", Integer.class);
                Assertions.assertEquals(41, result);

                Pair pair = context.eval("getAnswerPair", Pair.class);
                Assertions.assertEquals(41, pair.tail()[0]);

                context.addObject("OuterObject", new Object());

                try (VMContext nestedContext = VM.context("InnerContext", context, Nesting.Naive)) {

                    nestedContext.eval(scriptNested);

                    Integer resultNested = nestedContext.eval("getAnswer", Integer.class);
                    Assertions.assertEquals(42, resultNested);

                    Pair pairNested = nestedContext.eval("getAnswerPair", Pair.class);
                    Assertions.assertEquals(42, pairNested.tail()[0]);

                    nestedContext.addObject("InnerObject", new Object());
                }

            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

    @Test
    public void testCacheNesting() {
        try {
            VMScript script = new VMScript("testCacheNesting.js",
                    """
                        function getTheAnswer() {
                             return 42;
                        }
                    """);

            VMScript scriptNested = new VMScript("testCacheNestingInner.js",
                    """
                        function getAnswer() {
                             return getTheAnswer();
                        }
                    """);

            try (VMContext context = VM.context("OuterContext", Nesting.Naive)) {
                context.eval(script);
                try (VMContext nestedContext = VM.context("InnerContext", context, Nesting.Cache)) {
                    nestedContext.eval(scriptNested);

                    Integer resultNested = nestedContext.eval("getAnswer", Integer.class);
                    Assertions.assertEquals(42, resultNested);
                }
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

    @Test
    public void testCacheConstantsNesting() {
        try {
            VMScript script = new VMScript("testCacheConstantsNesting.js",
                    """
                        const answer = 42;
                    """);

            VMScript scriptNested = new VMScript("testCacheConstantsNestingInner.js",
                    """
                        function getAnswer() {
                             return answer;
                        }
                    """);

            try (VMContext context = VM.context("OuterContext", Nesting.Naive)) {
                context.eval(script);
                try (VMContext nestedContext = VM.context("InnerContext", context, Nesting.Cache)) {
                    nestedContext.eval(scriptNested);

                    Integer resultNested = nestedContext.eval("getAnswer", Integer.class);
                    Assertions.assertEquals(42, resultNested);
                }
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

    @Test
    public void testNaivePerfomance() {
        try {
            executeNesting("testNaivePerfomance", Nesting.None, Nesting.Naive);
        } catch (VMException ex) {
            fail(ex);
        }
    }

    @Test
    public void testCachePerfomance() {
        try {
            executeNesting("testCachePerfomance", Nesting.Cache, Nesting.Cache);
        } catch (VMException ex) {
            fail(ex);
        }
    }
}