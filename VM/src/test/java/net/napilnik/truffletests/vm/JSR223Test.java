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

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author malyshev
 */
public class JSR223Test {

    @Test
    public void testRefManExample() {
        try {
            ScriptEngine eng = new ScriptEngineManager().getEngineByName("graal.js");
            Object fn = eng.eval("(function() { return this; })");
            Invocable inv = (Invocable) eng;
            Object result = inv.invokeMethod(fn, "call", fn);
            Assertions.assertNotNull(result);
        } catch (ScriptException | NoSuchMethodException ex) {
            fail(ex);
        }
    }
}
