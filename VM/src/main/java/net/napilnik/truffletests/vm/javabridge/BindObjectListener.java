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

import net.napilnik.truffletests.vm.events.VMContextEvent;
import net.napilnik.truffletests.vm.events.VMContextEventType;
import net.napilnik.truffletests.vm.events.VMContextListener;
import net.napilnik.truffletests.vm.nesting.Nesting;

/**
 * Реакция на добавление класса или объекта. Обеспечивает ребинд.
 *
 * @author malyshev
 */
public class BindObjectListener implements VMContextListener {

    @Override
    public void onEvent(VMContextEvent event) {
        if (event.getType() == VMContextEventType.BindObject && event instanceof BindObjectEvent bindEvent) {
            if (bindEvent.getNesting() == Nesting.Cache) {
                bindEvent.getRebinder().rebind(bindEvent);
            }
        }
    }

}
