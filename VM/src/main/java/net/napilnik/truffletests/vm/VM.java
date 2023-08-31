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
 * Виртуальная машина. Служит для получения контекстов выполнения.
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

    /**
     * Вкл/выкл инспектора.
     *
     * @param doInspectation вкл/выкл
     */
    public static void setInspectableEvaluation(boolean doInspectation) {
        inspectedEvaluation = doInspectation;
    }

    /**
     * Текущий режим инспектора.
     *
     * @return вкл/выкл
     */
    public static boolean isInspectableEvaluation() {
        return inspectedEvaluation;
    }

    /**
     * Получить корневой контекст выполнения по имени. Если его нет, то он будет
     * создан.
     *
     * @param newRootName название корневого контекста.
     * @return корневой контекст
     */
    public static VMContext root(String newRootName) {
        return VMContext.constructRootContext(newRootName);
    }

    /**
     * Создать новый контекст выполнения по стратегии None.
     *
     * @return контекст выполнения
     */
    public static VMContext context() {
        return context(Nesting.None);
    }

    /**
     * Создать новый контекст выполнения по указаной стратегии.
     *
     * @param nestingMode стратегия вложения
     * @return контекст выполнения
     */
    public static VMContext context(Nesting nestingMode) {
        return context((String) null, nestingMode);
    }

    /**
     * Создать новый поименованный контекст выполнения по указанной стратегии.
     *
     * @param contextName название контекста
     * @param nestingMode стратегия вложения
     * @return контекст выполнения
     */
    public static VMContext context(String contextName, Nesting nestingMode) {
        return context(contextName, nestingMode, inspectedEvaluation);
    }

    /**
     * Создать новый поименованный контекст выполнения по указанной стратегии с
     * указанным режимом инспектора.
     *
     * @param contextName название контекста
     * @param nestingMode стратегия вложения
     * @param withInspection вкл/выкл
     * @return контекст выполнения
     */
    protected static VMContext context(String contextName, Nesting nestingMode, boolean withInspection) {
        return context(contextName, VMRootContextsContainer.GLOBALCONTEXT, nestingMode, withInspection);
    }

    /**
     * Создать новый контекст выполнения на основании родительского.
     *
     * @param parentContext родительский контекст выполнения
     * @param nestingMode стратегия вложения
     * @return контекст выполнения
     */
    public static VMContext context(VMContext parentContext, Nesting nestingMode) {
        return context((String) null, parentContext, nestingMode);
    }

    /**
     * Создать новый поименованный контекст выполнения на основании
     * родительского.
     *
     * @param contextName название контекста
     * @param parentContext родительский контекст выполнения
     * @param nestingMode стратегия вложения
     * @return контекст выполнения
     */
    public static VMContext context(String contextName, VMContext parentContext, Nesting nestingMode) {
        return context(contextName, parentContext, nestingMode, inspectedEvaluation);
    }

    /**
     * Создать новый поименованный контекст выполнения на основании
     * родительского.
     *
     * @param contextName название контекста
     * @param parentContext родительский контекст выполнения
     * @param nestingMode стратегия вложения
     * @param withInspection вкл/выкл
     * @return контекст выполнения
     */
    protected static VMContext context(String contextName, VMContext parentContext, Nesting nestingMode, boolean withInspection) {
        VMContext instance = parentContext.create(contextName, nestingMode, withInspection);
        prepareInstance(instance);
        return instance;
    }

    /**
     * Уведомить о создании нового контекста.
     *
     * @param instance созданный контекст
     */
    protected static void prepareInstance(VMContext instance) {
        VMContextEvent<VMContext> event = new VMContextEvent<>(instance, VMContextEventType.ContextPrepared);
        getContextEventEmitter().emitEvent(event);
    }

    /**
     * Забыть всех слушателей событий.
     */
    protected static void clearContextListeners() {
        getContextEventEmitter().clearContextListeners();
    }

    /**
     * Добавить слушателя событий.
     *
     * @param listener
     */
    protected static void addContextListener(VMContextListener listener) {
        getContextEventEmitter().addContextListener(listener);
    }

    /**
     * Забыть конкретного слушателя событий.
     *
     * @param listener слушатель
     */
    protected static void removeContextListener(VMContextListener listener) {
        getContextEventEmitter().removeContextListener(listener);
    }
}
