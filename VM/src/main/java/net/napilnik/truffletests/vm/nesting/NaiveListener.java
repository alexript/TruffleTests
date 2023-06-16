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

import java.util.HashSet;
import java.util.Set;
import net.napilnik.truffletests.vm.VMLanguage;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 *
 * @author malyshev
 */
class NaiveListener implements VMContextNestingListener {

    @Override
    public void onNesting(VMLanguage language, Context parentContext, Context ctx) {

        Value parentBindings = parentContext.getBindings(language.toPolyglot());
        Value currentBindings = ctx.getBindings(language.toPolyglot());
        synchronized (currentBindings) {
            synchronized (parentBindings) {
                Set<String> parentMembers = new HashSet<>(parentBindings.getMemberKeys());
                parentMembers.stream().forEach(
                        id -> currentBindings.putMember(id, parentBindings.getMember(id))
                );
            }
        }

    }

    @Override
    public Nesting getListenerNesting() {
        return Nesting.Naive;
    }

}
