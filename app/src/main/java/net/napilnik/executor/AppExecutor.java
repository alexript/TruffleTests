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
package net.napilnik.executor;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.app.App;
import net.napilnik.app.AppModule;
import net.napilnik.truffletests.vm.Evaluator;
import net.napilnik.truffletests.vm.VM;
import net.napilnik.truffletests.vm.VMContext;
import net.napilnik.truffletests.vm.VMException;
import net.napilnik.truffletests.vm.VMScript;
import net.napilnik.truffletests.vm.nesting.Nesting;

/**
 * Хитрожопый контейнер контекста для приложения. Смысл в том, что для
 * приложения создается корневой контекст без нестинга. А под каждый вызов на
 * исполнение создается дочерний контекст с кэширующим нестингом.
 *
 * @author malyshev
 */
class AppExecutor implements Evaluator {

    private final App app;
    private final VMContext appContext;
    private final Nesting userContextNestingMode;

    public AppExecutor(App app) {
        this(app, Nesting.Cache);
    }

    public AppExecutor(App app, Nesting userContextNestingMode) {
        this.app = app;
        this.appContext = VM.root(app.getMnemo());
        this.userContextNestingMode = userContextNestingMode;
        initAppContext();
    }

    private VMContext getUserContext() {
        return VM.context(appContext, userContextNestingMode);
    }

    private void initAppContext() {
        try {
            appContext.addObject(new AppObject(app));

            Map<String, String> sharedScripts = app.getScripts(AppModule.SCRIPT_TYPE.Shared);
            Map<String, String> serverScripts = app.getScripts(AppModule.SCRIPT_TYPE.Server);
            for (Map.Entry<String, String> entry : sharedScripts.entrySet()) {
                try {
                    VMScript s = new VMScript(entry.getKey(), entry.getValue());
                    appContext.eval(s);
                } catch (VMException ex) { // Отдельный catch на каждую итерацию, чтоб пропустить бяку, а не остановиться после первой же бяки.
                    Logger.getLogger(AppExecutor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            for (Map.Entry<String, String> entry : serverScripts.entrySet()) {
                try {
                    VMScript s = new VMScript(entry.getKey(), entry.getValue());
                    appContext.eval(s);
                } catch (VMException ex) { // Отдельный catch на каждую итерацию, чтоб пропустить бяку, а не остановиться после первой же бяки.
                    Logger.getLogger(AppExecutor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (VMException ex) {
            Logger.getLogger(AppExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void eval(String functionName) throws VMException {
        try (VMContext ctx = getUserContext()) {
            ctx.eval(functionName);
        }
    }

    @Override
    public <T> T eval(String functionName, Class<T> targetType) throws VMException {
        try (VMContext ctx = getUserContext()) {
            return ctx.eval(functionName, targetType);
        }
    }

    @Override
    public void eval(String functionName, Object... objArray) throws VMException {
        try (VMContext ctx = getUserContext()) {
            ctx.eval(functionName, objArray);
        }
    }

    @Override
    public <T> T eval(String functionName, Class<T> targetType, Object... objArray) throws VMException {
        try (VMContext ctx = getUserContext()) {
            return ctx.eval(functionName, targetType, objArray);
        }
    }

    @Override
    public <T> T eval(String objectName, String fieldName, Class<T> targetType, Object... objArray) throws VMException {
        try (VMContext ctx = getUserContext()) {
            return ctx.eval(objectName, fieldName, targetType, objArray);
        }
    }

    @Override
    public void eval(VMScript script) throws VMException {
        try (VMContext ctx = getUserContext()) {
            ctx.eval(script);
        }
    }

    @Override
    public <T> T eval(VMScript script, Class<T> targetType) throws VMException {
        try (VMContext ctx = getUserContext()) {
            return ctx.eval(script, targetType);
        }
    }

    @Override
    public boolean hasFunction(String functionName) {
        return appContext.hasFunction(functionName);
    }

    @Override
    public void close() throws Exception {
        appContext.close();
    }

}
