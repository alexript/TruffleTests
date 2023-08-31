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
package net.napilnik.appstorage;

import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.napilnik.app.App;
import net.napilnik.app.AppModule;

/**
 * Симулятор хранилища.
 *
 * @author malyshev
 */
public class ModuleStorage {

    private static final Map<String, AppModule> store = new ConcurrentHashMap<>();

    public ModuleStorage() {

    }

    public AppModule save(AppModule module) {
        return store.put(module.getMnemo(), module);
    }

    public AppModule delete(String mnemo) {
        return store.remove(mnemo);
    }

    public Set<String> list() {
        return store.keySet();
    }

    public Set<String> listApps() {
        Set<String> mnemoSet = new HashSet<>();
        store.forEach((mnemo, module) -> {
            if (module.isApplication()) {
                mnemoSet.add(mnemo);
            }
        });
        return mnemoSet;
    }

    public AppModule read(String mnemo) {
        return store.get(mnemo);
    }

    public String readAsJson(String mnemo) {
        AppModule module = read(mnemo);
        if (module == null) {
            return null;
        }
        Gson g = new Gson();
        return g.toJson(module);
    }
}
