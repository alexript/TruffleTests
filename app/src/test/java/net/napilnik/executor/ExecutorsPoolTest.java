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
package net.napilnik.executor;

import net.napilnik.app.App;
import net.napilnik.truffletests.vm.Evaluator;
import net.napilnik.truffletests.vm.VMException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author malyshev
 */
public class ExecutorsPoolTest {

    public ExecutorsPoolTest() {
    }

    @Test
    public void testGet() {
        App app = new App("pooltest", "ExecutorsPool test");
        app.addScript("summ.js", "function incOne(i) { return i+1; }");
        int result = 0;
        int total = 100;
        for (int i = 0; i < total; i++) {
            try {
                Evaluator evaluator = ExecutorsPool.get(app);
                result = evaluator.eval("incOne", Integer.class, result);
            } catch (VMException ex) {
                System.out.println("result: %d, step: %d".formatted(result, i));
                fail(ex);
            }
        }
        assertEquals(total, result);
    }

}
