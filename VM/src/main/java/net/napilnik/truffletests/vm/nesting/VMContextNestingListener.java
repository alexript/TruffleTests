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

import net.napilnik.truffletests.vm.VMLanguage;
import net.napilnik.truffletests.vm.events.VMContextEvent;
import net.napilnik.truffletests.vm.events.VMContextEventType;
import net.napilnik.truffletests.vm.events.VMContextListener;
import org.graalvm.polyglot.Context;

/**
 *
 * @author malyshev
 */
public interface VMContextNestingListener extends VMContextListener {

    @Override
    public default void onEvent(VMContextEvent event) {
        VMContextEventType type = event.getType();
        if (type == VMContextEventType.Nesting && event instanceof VMContextNestingEvent nestingEvent) {
            if (nestingEvent.getNesting() == getListenerNesting()) {
                VMLanguage language = nestingEvent.getLanguage();
                Context ctx = nestingEvent.getSource();
                Context parentContext = nestingEvent.getParentPolyglotContext();
                onNesting(language, parentContext, ctx);
            }
        }
    }

    Nesting getListenerNesting();

    void onNesting(VMLanguage language, Context parentContext, Context ctx);

}
