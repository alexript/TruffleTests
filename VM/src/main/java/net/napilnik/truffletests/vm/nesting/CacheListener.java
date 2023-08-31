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
package net.napilnik.truffletests.vm.nesting;

import java.util.Set;
import net.napilnik.truffletests.vm.VMLanguage;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

/**
 * Вкладываем по стратегии Cache
 *
 * @author malyshev
 */
class CacheListener implements VMContextNestingListener {

    /**
     * Берем с Engine родителя все, что на нем есть закэшированного и выполняем
     * на контексте дочки. Если дочка создана с тем же самым экземпляром Engine,
     * то полиглот просто производит перерегистрацию и не занимается
     * интерпритацией и выполнением закэшированного. очевидно, что то, что не
     * закэшировано, в дочку так не попадет.
     *
     * @param language
     * @param parentContext
     * @param ctx
     */
    @Override
    public void onNesting(VMLanguage language, Context parentContext, Context ctx) {
        synchronized (parentContext) {
            Set<Source> cachedSources = parentContext.getEngine().getCachedSources();
            for (Source s : cachedSources) {
                ctx.eval(s);
            }
        }
    }

    @Override
    public Nesting getListenerNesting() {
        return Nesting.Cache;
    }

}
