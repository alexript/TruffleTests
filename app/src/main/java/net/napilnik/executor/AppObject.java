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
import net.napilnik.truffletests.vm.annotations.VMAccess;
import net.napilnik.truffletests.vm.annotations.VMObject;

/**
 * Глобальный объект Application в контексте Javascript
 *
 * @author malyshev
 */
@VMObject(value = "Application")
public class AppObject {

    /**
     * Мнемоника приложения.
     */
    @VMAccess
    public final String mnemo;

    AppObject(App app) {
        mnemo = app.getMnemo();
    }
}
