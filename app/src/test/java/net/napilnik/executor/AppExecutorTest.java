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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author malyshev
 */
public class AppExecutorTest {

    /**
     * Проверяем что скрипт приложения интерпретировался и объявленную в нем
     * функцию можно использовать.
     */
    @Test
    public void testAppObject() {
        String appMnemo = "appobjtest";
        App app = new App(appMnemo, "AppObject Test");
        app.addScript(AppModule.SCRIPT_TYPE.Shared, "testAppObject.js", "function getAppMnemo() { return Application.mnemo; }");
        try (AppExecutor executor = new AppExecutor(app)) {
            String result = executor.eval("getAppMnemo", String.class);
            assertEquals(appMnemo, result);
        } catch (Exception ex) {
            fail(ex);
        }
    }
}
