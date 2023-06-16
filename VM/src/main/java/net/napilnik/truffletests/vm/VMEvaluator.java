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

import net.napilnik.truffletests.vm.inspection.VMInspectionListener;
import net.napilnik.truffletests.vm.inspection.VMInspector;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

/**
 *
 * @author malyshev
 */
abstract class VMEvaluator extends PolyglotContextProvider implements Evaluator {

    private final Context ctx;
    private final VMLanguage lng;
    private final boolean useInspector;

    public VMEvaluator(VMLanguage lng, Context ctx, boolean withInspection) {
        this.ctx = ctx;
        this.lng = lng;
        this.useInspector = withInspection;
    }

    @Override
    public final Context getPolyglotContext() {
        return ctx;
    }

    protected Value getBindings() {
        synchronized (ctx) {
            return ctx.getBindings(lng.toPolyglot());
        }
    }

    private void processScriptException(Exception x, VMInspectionListener enterListener, String functionName) throws VMException {
        throw new VMException(functionName, enterListener.getLineNumber(), enterListener.getColumnNumber(), enterListener.getLine(), x.getMessage(), "");
    }

    @Override
    public void eval(String functionName) throws VMException {
        eval(functionName, new Object[0]);
    }

    @Override
    public <T> T eval(String functionName, Class<T> targetType) throws VMException {
        return eval(functionName, targetType, new Object[0]);
    }

    @Override
    public void eval(String functionName, Object... objArray) throws VMException {
        eval(functionName, Void.class, objArray);
    }

    @Override
    public <T> T eval(String functionName, Class<T> targetType, Object... objArray) throws VMException {
        if (!useInspector) {
            return evalImpl(functionName, targetType, objArray);
        }
        VMInspectionListener enterListener = VMInspector.createInspectionListener("On enter");
        VMInspectionListener returnListener = VMInspector.createInspectionListener("On return");
        VMInspector inspector = new VMInspector(this, enterListener, returnListener);
        try (inspector) {
            return evalImpl(functionName, targetType, objArray);
        } catch (VMException next) {
            throw next;
        } catch (Exception ex) {
            processScriptException(ex, enterListener, functionName);
        }
        return null;

    }

    @Override
    public <T> T eval(String objectName, String fieldName, Class<T> targetType, Object... objArray) throws VMException {
        if (!useInspector) {
            return evalImpl(objectName, fieldName, targetType, objArray);
        }
        VMInspectionListener enterListener = VMInspector.createInspectionListener("On enter");
        VMInspectionListener returnListener = VMInspector.createInspectionListener("On return");
        VMInspector inspector = new VMInspector(this, enterListener, returnListener);
        try (inspector) {
            return evalImpl(objectName, fieldName, targetType, objArray);
        } catch (VMException next) {
            throw next;
        } catch (Exception ex) {
            processScriptException(ex, enterListener, "%s::%s".formatted(objectName, fieldName));
        }
        return null;

    }

    @Override
    public void eval(VMScript script) throws VMException {
        if (!useInspector) {
            evalImpl(script);
            return;
        }
        VMInspectionListener enterListener = VMInspector.createInspectionListener("On enter");
        VMInspectionListener returnListener = VMInspector.createInspectionListener("On return");
        VMInspector inspector = new VMInspector(this, enterListener, returnListener);
        try (inspector) {
            evalImpl(script);
        } catch (VMException next) {
            throw next;
        } catch (Exception ex) {
            processScriptException(ex, enterListener, script.getSource().getName());
        }
    }

    @Override
    public <T> T eval(VMScript script, Class<T> targetType) throws VMException {
        if (!useInspector) {
            return evalImpl(script, targetType);
        }
        VMInspectionListener enterListener = VMInspector.createInspectionListener("On enter");
        VMInspectionListener returnListener = VMInspector.createInspectionListener("On return");
        VMInspector inspector = new VMInspector(this, enterListener, returnListener);
        try (inspector) {
            return evalImpl(script, targetType);
        } catch (VMException next) {
            throw next;
        } catch (Exception ex) {
            processScriptException(ex, enterListener, script.getSource().getName());
        }
        return null;
    }

    private void evalImpl(VMScript script) {
        synchronized (ctx) {
            try {
                ctx.enter();
                ctx.eval(script.getSource());
            } finally {
                ctx.leave();
            }
        }
    }

    private <T> T evalImpl(VMScript script, Class<T> targetType) {
        synchronized (ctx) {
            try {
                ctx.enter();
                Value value = ctx.eval(script.getSource());
                T result = value.as(targetType);
                return result;
            } finally {
                ctx.leave();
            }
        }
    }

    @Override
    public boolean hasFunction(String functionName) {
        boolean result = false;
        synchronized (ctx) {
            try {
                ctx.enter();
                Value function = this.getBindings().getMember(functionName);
                if (function != null && function.canExecute()) {
                    result = true;
                }

            } finally {
                ctx.leave();
            }
        }
        return result;
    }

    private <T> T evalImpl(String functionName, Class<T> targetType, Object... objArray) throws VMException {
        synchronized (ctx) {
            try {
                Value result = null;
                ctx.enter();
                Value function;

                try {
                    function = this.getBindings().getMember(functionName);
                } catch (PolyglotException ex) {
                    throw new VMException(ex);
                }

                if (function == null) {
                    throw new VMNoFunctionException("Function '" + functionName + "' not found");
                }
                if (!function.canExecute()) {
                    throw new VMNoFunctionException(functionName + " is not a function");
                }
                if (objArray == null) {
                    objArray = new Object[0];
                }

                if (targetType == null || Void.class.isInstance(targetType)) {
                    function.executeVoid(objArray);
                } else {
                    result = function.execute(objArray);
                }
                if (result == null) {
                    return null;
                }
                return result.as(targetType);

            } finally {
                ctx.leave();
            }
        }
    }

    private <T> T evalImpl(String objectName, String fieldName, Class<T> targetType, Object... objArray) throws VMException {
        synchronized (ctx) {
            try {
                Value result = null;
                ctx.enter();
                Value function = null;

                try {
                    Value member = this.getBindings().getMember(objectName);
                    if (member != null) {
                        function = member.getMember(fieldName);
                    }
                } catch (PolyglotException ex) {
                    throw new VMException(ex);
                }

                if (function == null) {
                    throw new VMNoFunctionException("Function '" + fieldName + "' not found");
                }
                if (!function.canExecute()) {
                    throw new VMNoFunctionException(fieldName + " is not a function");
                }
                if (objArray == null) {
                    objArray = new Object[0];
                }

                if (targetType == null || Void.class.isInstance(targetType)) {
                    function.executeVoid(objArray);
                } else {
                    result = function.execute(objArray);
                }
                if (result == null) {
                    return null;
                }
                return result.as(targetType);

            } finally {
                ctx.leave();
            }
        }
    }
}
