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
package net.napilnik.truffletests.vm.inspection;

import java.util.function.Consumer;
import net.napilnik.truffletests.vm.PolyglotContextProvider;
import net.napilnik.truffletests.vm.VMContext;
import org.graalvm.polyglot.management.ExecutionEvent;
import org.graalvm.polyglot.management.ExecutionListener;

/**
 *
 * @author malyshev
 */
public class VMInspector implements AutoCloseable {

    private final ExecutionListener listener;

    public static abstract class Listener implements Consumer<ExecutionEvent> {

    }

    public VMInspector(PolyglotContextProvider contextProvider, Listener enterListener, Listener returnListener) {
        ExecutionListener.Builder builder = ExecutionListener.newBuilder();
        if (enterListener != null) {
            builder.onEnter(enterListener);
        }
        if (returnListener != null) {
            builder.onReturn(returnListener);
        }

        builder.statements(true);
        builder.collectExceptions(true);
        listener = builder.attach(contextProvider.getPolyglotContext().getEngine());
    }

    @Override
    public void close() throws Exception {
        listener.close();
    }

    public static VMInspectionListener createInspectionListener(String prefixMsg) {
        return new VMInspectionListener(prefixMsg);
    }

    public static class EnterInspector extends VMInspector {

        public EnterInspector(VMContext ctx) {
            this(ctx, createInspectionListener("On enter"));
        }

        public EnterInspector(VMContext ctx, Listener enterListener) {
            super(ctx, enterListener, null);
        }
    }

    public static class ReturnInspector extends VMInspector {

        public ReturnInspector(VMContext ctx) {
            this(ctx, createInspectionListener("On return"));
        }

        public ReturnInspector(VMContext ctx, Listener returnListener) {
            super(ctx, null, returnListener);
        }
    }

    public static class ScoperInspector extends VMInspector {

        public ScoperInspector(VMContext ctx) {
            this(ctx, createInspectionListener("On enter"), createInspectionListener("On return"));
        }

        public ScoperInspector(VMContext ctx, Listener enterListener, Listener returnListener) {
            super(ctx, enterListener, returnListener);
        }
    }

}
