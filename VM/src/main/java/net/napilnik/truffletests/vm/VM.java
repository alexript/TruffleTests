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

import net.napilnik.truffletests.vm.nesting.Nesting;
import net.napilnik.truffletests.vm.events.VMContextEvent;
import net.napilnik.truffletests.vm.events.VMContextEventEmitter;
import net.napilnik.truffletests.vm.events.VMContextEventType;
import net.napilnik.truffletests.vm.events.VMContextListener;
import net.napilnik.truffletests.vm.javabridge.BridgeListener;

/**
 *
 * @author malyshev
 */
public class VM {

    private static boolean inspectedEvaluation = true;

    private static final VMContextEventEmitter contextEventEmitter = createContextEventEmitter();

    private static VMContextEventEmitter createContextEventEmitter() {
        VMContextEventEmitter emitter = new VMContextEventEmitter();
        emitter.addContextListener(new BridgeListener());
        for (Nesting n : Nesting.values()) {
            emitter.addContextListener(n.createNestingListener());
        }
        return emitter;
    }

    protected static VMContextEventEmitter getContextEventEmitter() {
        return contextEventEmitter;
    }

    public static void setInspectableEvaluation(boolean doInspectation) {
        inspectedEvaluation = doInspectation;
    }

    public static boolean isInspectableEvaluation() {
        return inspectedEvaluation;
    }

    public static VMContext root(String newRootName) {
        return VMContext.constructRootContext(newRootName);
    }

    public static VMContext context() {
        return context(Nesting.None);
    }

    public static VMContext context(Nesting nestingMode) {
        return context((String) null, nestingMode);
    }

    public static VMContext context(String contextName, Nesting nestingMode) {
        return context(contextName, nestingMode, inspectedEvaluation);
    }

    protected static VMContext context(String contextName, Nesting nestingMode, boolean withInspection) {
        return context(contextName, VMRootContextsContainer.GLOBALCONTEXT, nestingMode, withInspection);
    }

    public static VMContext context(VMContext parentContext, Nesting nestingMode) {
        return context((String) null, parentContext, nestingMode);
    }

    public static VMContext context(String contextName, VMContext parentContext, Nesting nestingMode) {
        return context(contextName, parentContext, nestingMode, inspectedEvaluation);
    }

    protected static VMContext context(String contextName, VMContext parentContext, Nesting nestingMode, boolean withInspection) {
        VMContext instance = parentContext.create(contextName, nestingMode, withInspection);
        prepareInstance(instance);
        return instance;
    }

    protected static void prepareInstance(VMContext instance) {
        VMContextEvent<VMContext> event = new VMContextEvent<>(instance, VMContextEventType.ContextPrepared);
        getContextEventEmitter().emitEvent(event);
    }

    protected static void clearContextListeners() {
        getContextEventEmitter().clearContextListeners();
    }

    protected static void addContextListener(VMContextListener listener) {
        getContextEventEmitter().addContextListener(listener);
    }

    protected static void removeContextListener(VMContextListener listener) {
        getContextEventEmitter().removeContextListener(listener);
    }
}
