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
package net.napilnik.truffletests.vm.javabridge;

import net.napilnik.truffletests.vm.VMContext;
import net.napilnik.truffletests.vm.events.VMContextEvent;
import net.napilnik.truffletests.vm.events.VMContextEventType;
import net.napilnik.truffletests.vm.nesting.Nesting;

/**
 *
 * @author malyshev
 */
public class BridgeEvent extends VMContextEvent<VMContext> {

    private static final long serialVersionUID = -5503844283027739257L;
    private final Nesting nesting;

    public BridgeEvent(VMContext source, Nesting nesting) {
        super(source, VMContextEventType.Bridging);
        this.nesting = nesting;
    }

    /**
     * Получить стратегию вкладывания.
     *
     * @return стратегия
     */
    public final Nesting getNesting() {
        return nesting;
    }

}
