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
import net.napilnik.truffletests.vm.VMException;
import net.napilnik.truffletests.vm.events.VMContextEvent;
import net.napilnik.truffletests.vm.events.VMContextEventType;
import net.napilnik.truffletests.vm.events.VMContextListener;
import net.napilnik.truffletests.vm.nesting.Nesting;

/**
 *
 * @author malyshev
 */
public class BridgeListener implements VMContextListener {

    @Override
    public void onEvent(VMContextEvent event) throws VMException {
        if (event.getType() == VMContextEventType.Bridging && event instanceof BridgeEvent bridgeEvent) {
            onBridge(bridgeEvent.getSource(), bridgeEvent.getNesting());
        }
    }

    /**
     * Отреагировать на добавление класса или объекта.
     *
     * @param source контекст, на который добавлен класс или объект.
     * @param nesting стратегия вкладывания.
     * @throws VMException
     */
    private void onBridge(VMContext source, Nesting nesting) throws VMException {
        switch (nesting) {
            case None ->
                GlobalBindings.bind(source);
            case Naive -> {
            }
            case Cache -> {
            }
            default -> {
            }
        }
    }

}
