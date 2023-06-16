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

import net.napilnik.truffletests.vm.nesting.Nesting;
import net.napilnik.truffletests.objects.Pair;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author malyshev
 */
public class ContextNestingTest {

    @Test
    public void testNaiveNestingObjects() {
        try {
            VMScript script = new VMScript("testNaiveNestingObjects.js",
                    """
                        function getAnswer() {
                             return 41;
                        }

                        function getAnswerPair() {
                            return new Pair('answer', 41);
                        }
                    """);

            VMScript scriptNested = new VMScript("testNaiveNestingObjectsNested.js",
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
    public void testCacheNestingObjects() {
        try {
            VMScript script = new VMScript("testCacheNestingObjects.js",
                    """
                        function getAnswer() {
                             return 41;
                        }

                        function getAnswerPair() {
                            return new Pair('answer', 41);
                        }
                    """);

            VMScript scriptNested = new VMScript("testCacheNestingObjectsNested.js",
                    """
                        function getAnswer() {
                             return 42;
                        }

                        function getAnswerPair() {
                            return new Pair('answer', 42);
                        }
                    """);

            try (VMContext context = VM.context("OuterContext", Nesting.Cache)) {
                context.eval(script);

                Integer result = context.eval("getAnswer", Integer.class);
                Assertions.assertEquals(41, result);

                Pair pair = context.eval("getAnswerPair", Pair.class);
                Assertions.assertEquals(41, pair.tail()[0]);

                context.addObject("OuterObject", new Object());

                try (VMContext nestedContext = VM.context("InnerContext", context, Nesting.Cache)) {

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
    public void testCacheNestingFunctions() {
        try {
            VMScript script = new VMScript("testCacheNestingFunctions.js",
                    """
                        function getTheAnswer() {
                             return 42;
                        }
                    """);

            VMScript scriptNested = new VMScript("testCacheNestingFunctionsInner.js",
                    """
                        function getAnswer() {
                             return getTheAnswer();
                        }
                    """);

            try (VMContext context = VM.context("OuterContext", Nesting.None)) {
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
    public void testCacheNestingConstants() {
        try {
            VMScript script = new VMScript("testCacheNestingConstants.js",
                    """
                        const answer = 42;
                    """);

            VMScript scriptNested = new VMScript("testCacheNestingConstantsInner.js",
                    """
                        function getAnswer() {
                             return answer;
                        }
                    """);

            try (VMContext context = VM.context("OuterContext", Nesting.None)) {
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
    public void testContextAsScope() {
        try {
            VMScript script = new VMScript("testContextAsScope.js",
                    """
                        var answer = 42;
                    """);

            VMScript scriptNested = new VMScript("testContextAsScope2.js",
                    """
                        function getAnswer() {
                            answer = answer+1;
                            return answer;
                        }
                    """);

            try (VMContext context = VM.context("OuterContext", Nesting.None)) {
                context.eval(script);
                try (VMContext nestedContext = VM.context("InnerContext", context, Nesting.Cache)) {
                    nestedContext.eval(scriptNested);

                    Integer resultNested = nestedContext.eval("getAnswer", Integer.class);
                    Assertions.assertEquals(43, resultNested);

                    resultNested = nestedContext.eval("getAnswer", Integer.class);
                    Assertions.assertEquals(44, resultNested);
                }

                try (VMContext nestedContext = VM.context("InnerContext", context, Nesting.Cache)) {
                    nestedContext.eval(scriptNested);
                    Integer resultNested = nestedContext.eval("getAnswer", Integer.class);
                    Assertions.assertEquals(43, resultNested);

                    context.eval(new VMScript("directaccess.js", """
                        function getOuterAnswer() {
                            return answer;
                        }
                    """));
                    Integer result = context.eval("getOuterAnswer", Integer.class);
                    Assertions.assertEquals(42, result);
                }

                context.eval(new VMScript("directaccess2.js", """
                        function getOuterAnswer2() {
                            return answer;
                        }
                """));
                Integer result = context.eval("getOuterAnswer2", Integer.class);
                Assertions.assertEquals(42, result);
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

}
