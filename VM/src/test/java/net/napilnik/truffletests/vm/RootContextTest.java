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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author malyshev
 */
public class RootContextTest {

    @Test
    public void testSecondRoot() {
        VMContext seconRoot = VM.root("Second");
        Assertions.assertFalse(VMRootContextsContainer.GLOBALCONTEXT == seconRoot);
    }

    @Test
    public void testRootReusage() {
        String rootName = "Reusage";
        VMContext root1 = VM.root(rootName);
        VMContext root2 = VM.root(rootName);
        Assertions.assertTrue(root1 == root2);
    }
}
