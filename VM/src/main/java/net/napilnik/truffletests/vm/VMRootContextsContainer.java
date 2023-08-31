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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.napilnik.truffletests.vm.javabridge.BindObjectListener;
import net.napilnik.truffletests.vm.javabridge.HostAccessListener;
import org.graalvm.polyglot.HostAccess;

/**
 *
 * @author malyshev
 */
class VMRootContextsContainer {

    protected static final String GLOBAL_ROOT_CONTEXT_NAME = "__GLOBAL__";
    protected static final VMContext GLOBALCONTEXT = createGlobal();

    private static VMContext createGlobal() {
        VM.addContextListener(new HostAccessListener() {
            @Override
            public void specify(HostAccess.Builder builder) {
                // пока ничего не делаем.
            }
        }); // To be sure
        VM.addContextListener(new BindObjectListener()); // To be sure

        return VMContext.constructRootContext(GLOBAL_ROOT_CONTEXT_NAME, true);
    }

    private final Map<String, VMContext> roots;

    public VMRootContextsContainer() {
        roots = new ConcurrentHashMap<>();
    }

    boolean contains(String contextName) {
        return roots.containsKey(contextName);
    }

    VMContext get(String contextName) {
        return roots.get(contextName);
    }

    void store(String contextName, VMContext vmContext) {
        roots.put(contextName, vmContext);
    }

}
