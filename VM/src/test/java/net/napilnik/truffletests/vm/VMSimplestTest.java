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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author malyshev
 */
public class VMSimplestTest {

    @Test
    public void testSimplestUse() {
        try {
            VMScript script = new VMScript("testSimplestUse.js", "let answer = 42;");
            VMContext context = VM.context(Nesting.None);
            try (context) {
                context.eval(script);
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

}
