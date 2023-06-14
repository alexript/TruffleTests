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
import org.graalvm.polyglot.Context;

/**
 *
 * @author malyshev
 */
public class VMContextNestingEvent extends VMContextEvent<Context> {

    private static final long serialVersionUID = 5168362009305706363L;
    private final Nesting nesting;
    private final VMLanguage lng;
    private final Context parentContext;

    public VMContextNestingEvent(VMLanguage lng, Context parentCtx, Context source, Nesting nesting) {
        super(source, VMContextEventType.Nesting);
        this.lng = lng;
        this.parentContext = parentCtx;
        this.nesting = nesting;
    }

    public final Nesting getNesting() {
        return nesting;
    }

    public final VMLanguage getLanguage() {
        return lng;
    }

    public final Context getParentPolyglotContext() {
        return parentContext;
    }

}
