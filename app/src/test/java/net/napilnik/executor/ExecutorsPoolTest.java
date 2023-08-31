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
import net.napilnik.app.AppModule;
import net.napilnik.truffletests.vm.Evaluator;
import net.napilnik.truffletests.vm.VMException;
import net.napilnik.truffletests.vm.VMNoFunctionException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author malyshev
 */
public class ExecutorsPoolTest {

    private static int usecaseFor(int total, ExecutorsPool pool) throws VMException {
        App app = new App("pooltest", "ExecutorsPool test");
        app.addScript(AppModule.SCRIPT_TYPE.Shared, "summ.js", "function incOne(i) { return i+1; }");
        int result = 0;
        for (int i = 0; i < total; i++) {
            try {
                Evaluator evaluator = pool.get(app);
                result = evaluator.eval("incOne", Integer.class, result);
            } catch (VMException ex) {
                System.out.println("result: %d, step: %d".formatted(result, i));
                throw ex;
            }
        }
        return result;
    }

    @Test
    public void testNaiveGet() {
        System.out.println("testNaiveGet");
        try {
            int total = 100;
            int result = usecaseFor(total, ExecutorsPool.NAIVE);
            assertEquals(total, result);
        } catch (VMException ex) {
            fail(ex);
        }
    }

    @Test
    public void testNoneGet() {
        System.out.println("testNoneGet");
        VMNoFunctionException ex = assertThrows(VMNoFunctionException.class, () -> {
            int total = 100;
            int result = usecaseFor(total, ExecutorsPool.NONE);
            assertNotEquals(total, result);
        });
        assertEquals("incOne", ex.getFunctionName());
    }

    @Test
    public void testCacheGet() {
        System.out.println("testCacheGet");
        try {
            int total = 100;
            int result = usecaseFor(total, ExecutorsPool.CAHCE);
            assertEquals(total, result);
        } catch (VMException ex) {
            fail(ex);
        }
    }

}
