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
import net.napilnik.truffletests.vm.nesting.Nesting;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author malyshev
 */
public class NestingPerformanceTest {

    private static VMScript testScript;

    @BeforeAll
    public static void warmUp() throws VMException {
        long iterations = 10000;
        StringBuilder sb = new StringBuilder();
        for (long i = 0; i < iterations; i++) {
            String fooName = "foo" + Long.toString(i);

            String foo = "function %s(){return;}".formatted(fooName);
            sb.append(foo).append('\n');

        }
        testScript = new VMScript("foo.js", sb.toString());
        try (VMContext context = VM.context("WarmUpContext", Nesting.None)) {
            context.eval(testScript);
        }
    }

    private static void executeNesting(String testName, Nesting outerNesting, Nesting innerNesting) throws VMException {
        VMContext root = VM.root(testName);
        Date now = new Date();
        System.out.println("-ts- Test: %1$s --------- <start: %2$tH:%2$tM:%2$tS> --------".formatted(testName, now));
        try (VMContext context = VM.context("OuterContext", root, outerNesting)) {
            context.eval(testScript);

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
    public void testNoneNonePerfomance() {
        try {
            executeNesting("testNoneNonePerfomance", Nesting.None, Nesting.None);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }

    @Test
    public void testNoneNaivePerfomance() {
        try {
            executeNesting("testNoneNaivePerfomance", Nesting.None, Nesting.Naive);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }

    @Test
    public void testNoneCachePerfomance() {
        try {
            executeNesting("testNoneCachePerfomance", Nesting.None, Nesting.Cache);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }

    @Test
    public void testNaiveNonePerfomance() {
        try {
            executeNesting("testNaiveNonePerfomance", Nesting.Naive, Nesting.None);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }

    @Test
    public void testNaiveNaivePerfomance() {
        try {
            executeNesting("testNaiveNaivePerfomance", Nesting.Naive, Nesting.Naive);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }

    @Test
    public void testNaiveCachePerfomance() {
        try {
            executeNesting("testNaiveCachePerfomance", Nesting.Naive, Nesting.Cache);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }

    @Test
    public void testCacheNonePerfomance() {
        try {
            executeNesting("testCacheNonePerfomance", Nesting.Cache, Nesting.None);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }

    @Test
    public void testCacheNaivePerfomance() {
        try {
            executeNesting("testCacheNaivePerfomance", Nesting.Cache, Nesting.Naive);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }

    @Test
    public void testCacheCachePerfomance() {
        try {
            executeNesting("testCacheCachePerfomance", Nesting.Cache, Nesting.Cache);
        } catch (VMException ex) {
            fail(ex);
        }
        assertTrue(true);
    }
}
