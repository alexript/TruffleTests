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

import java.util.HashMap;
import java.util.Map;
import net.napilnik.app.App;
import net.napilnik.truffletests.vm.Evaluator;

/**
 *
 * @author malyshev
 */
public class ExecutorsPool {

    private static final Map<String, AppExecutor> pool = new HashMap<>();

    public static Evaluator get(App app) {
        String mnemo = app.getMnemo();
        if (!pool.containsKey(mnemo)) {
            pool.put(mnemo, new AppExecutor(app));
        }
        return pool.get(mnemo);
    }
}
