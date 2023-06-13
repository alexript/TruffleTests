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

/**
 *
 * @author malyshev
 */
public class VM {

    private static boolean inspectedEvaluation = true;

    public static void setInspectableEvaluation(boolean doInspectation) {
        inspectedEvaluation = doInspectation;
    }

    public static boolean isInspectableEvaluation() {
        return inspectedEvaluation;
    }

    public static VMContext context(Nesting nestingMode) {
        return context((String) null, nestingMode);
    }

    public static VMContext context(String contextName, Nesting nestingMode) {
        return context(contextName, nestingMode, inspectedEvaluation);
    }

    protected static VMContext context(String contextName, Nesting nestingMode, boolean withInspection) {
        return context(contextName, VMContext.GLOBALCONTEXT, nestingMode, withInspection);
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

    private static void prepareInstance(VMContext instance) {

    }

}
