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
package net.napilnik.truffletests.objects;

import net.napilnik.truffletests.vm.VM;
import net.napilnik.truffletests.vm.VMContext;
import net.napilnik.truffletests.vm.VMException;
import net.napilnik.truffletests.vm.VMScript;
import net.napilnik.truffletests.vm.nesting.Nesting;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author malyshev
 */
public class PairTest {

    @Test
    public void testPair() {
        try {
            VMScript script = new VMScript("testPair.js", "function getAnswer() {return new Pair('answer', 42);}");
            VMContext contextNone = VM.context(Nesting.None);
            try (contextNone) {
                contextNone.eval(script);
                Pair result = contextNone.eval("getAnswer", Pair.class);
                assertNotNull(result);
                assertEquals(42, result.tail()[0]);
            }
            VMContext contextNaive = VM.context(Nesting.Naive);
            try (contextNaive) {
                contextNaive.eval(script);
                Pair result = contextNaive.eval("getAnswer", Pair.class);
                assertNotNull(result);
                assertEquals(42, result.tail()[0]);
            }
            VMContext contextCache = VM.context(Nesting.Cache);
            try (contextCache) {
                contextCache.eval(script);
                Pair result = contextCache.eval("getAnswer", Pair.class);
                assertNotNull(result);
                assertEquals(42, result.tail()[0]);
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

}
