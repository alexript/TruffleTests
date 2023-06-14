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
package net.napilnik.truffletests.vm.events;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author malyshev
 */
public class VMContextEventEmitter implements VMEventEmitter<VMContextEvent, VMContextListener> {

    private final Set<VMContextListener> listeners;

    public VMContextEventEmitter() {
        listeners = new HashSet<>();
    }

    @Override
    public void clearContextListeners() {
        synchronized (listeners) {
            listeners.clear();
        }
    }

    @Override
    public void addContextListener(VMContextListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeContextListener(VMContextListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public void emitEvent(VMContextEvent event) {
        synchronized (listeners) {
            for (VMContextListener l : listeners) {
                l.onEvent(event);
            }
        }
    }

    @Override
    public void emitEvent(VMContextEvent event, Class<? extends VMContextListener> listenerClass) {
        synchronized (listeners) {
            for (VMContextListener l : listeners) {
                if (listenerClass.isAssignableFrom(l.getClass())) {
                    l.onEvent(event);
                }
            }
        }
    }
}
