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

import net.napilnik.truffletests.vm.javabridge.HostAccessProvider;
import net.napilnik.truffletests.vm.nesting.Nesting;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.truffletests.vm.javabridge.BindObjectEvent;
import net.napilnik.truffletests.vm.javabridge.BindObjectListener;
import net.napilnik.truffletests.vm.javabridge.BridgeEvent;
import net.napilnik.truffletests.vm.javabridge.BridgeListener;
import net.napilnik.truffletests.vm.javabridge.HostAccessEvent;
import net.napilnik.truffletests.vm.javabridge.HostAccessListener;
import net.napilnik.truffletests.vm.nesting.VMContextNestingEvent;
import net.napilnik.truffletests.vm.nesting.VMContextNestingListener;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

/**
 *
 * @author malyshev
 */
public class VMContext extends VMEvaluator implements AutoCloseable {

    private final List<VMContext> childs;
    private final VMContext parent;
    private final String contextName;
    private final Nesting nesting;

    private static final VMRootContextsContainer ROOTS = new VMRootContextsContainer();

    protected static VMContext constructRootContext(String contextName) {

        return constructRootContext(contextName, false);
    }

    protected static VMContext constructRootContext(String contextName, boolean isGlobal) {
        VMContext vmContext;
        if (isGlobal) {
            vmContext = constructNewRootContext(contextName);
        } else {
            if (ROOTS.contains(contextName)) {
                vmContext = ROOTS.get(contextName);
            } else {
                vmContext = constructNewRootContext(contextName);
                ROOTS.store(contextName, vmContext);
            }

        }
        return vmContext;
    }

    private static VMContext constructNewRootContext(String contextName) {
        VMContext vmContext = new VMContext("<RootContext" + contextName + ">", VMLanguage.JS, null, Nesting.None, false);
        fireBridgeEvent(new BridgeEvent(vmContext, Nesting.None));

        try {
            VMStdLib.apply(vmContext);
        } catch (VMException ex) {
            Logger.getLogger(VMContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        VM.prepareInstance(vmContext);
        return vmContext;
    }

    private VMContext(String contextName, VMContext parent, Nesting nestingMode, boolean withInspection) {
        this(contextName, VMLanguage.JS, parent, nestingMode, withInspection);
    }

    private VMContext(String contextName, VMLanguage lng, VMContext parent, Nesting nestingMode, boolean withInspection) {
        super(lng, buildContext(lng, parent, nestingMode), withInspection);
        childs = new ArrayList<>();
        this.parent = parent;
        this.contextName = contextName;
        this.nesting = nestingMode;
    }

    @Override
    public String getName() {
        return contextName;
    }

    private static Context buildContext(VMLanguage lng, PolyglotContextProvider parent, Nesting nestingMode) {
        HostAccess.Builder hostAccessBuilder = HostAccessProvider.build();
        fireHostAccessEvent(new HostAccessEvent(hostAccessBuilder));

        Context.Builder builderTemplate = Context.newBuilder(lng.toPolyglot())
                .allowHostAccess(hostAccessBuilder.build())
                .allowCreateThread(true)
                .allowValueSharing(true)
                .allowExperimentalOptions(true)
                .allowInnerContextOptions(true)
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .hostClassLoader(VM.class.getClassLoader());

        Engine engine;
        if (parent == null) {
            engine = Engine.newBuilder().build();
        } else {
            if (nestingMode == Nesting.Cache) {
                engine = parent.getPolyglotContext().getEngine();
            } else {
                engine = Engine.newBuilder().build();
            }
        }
        builderTemplate.engine(engine);
        Context ctx = builderTemplate.build();
        if (parent != null) {
            fireContextNestingEvent(new VMContextNestingEvent(lng, parent.getPolyglotContext(), ctx, nestingMode));
        }
        return ctx;
    }

    protected VMContext create(String contextName, Nesting nestingMode, boolean withInspection) {
        VMContext ctx = new VMContext(contextName, this, nestingMode, withInspection);
        childs.add(ctx);

        fireBridgeEvent(new BridgeEvent(ctx, nestingMode));
        fireBindObjectEvent(new BindObjectEvent(this, nestingMode, new ReBinder(ctx), getBoundObjects()));

        return ctx;
    }

    protected VMContext create(String contextName, VMLanguage lng, Nesting nestingMode, boolean withInspection) {
        VMContext ctx = new VMContext(contextName, lng, this, nestingMode, withInspection);
        childs.add(ctx);

        fireBridgeEvent(new BridgeEvent(ctx, nestingMode));
        fireBindObjectEvent(new BindObjectEvent(this, nestingMode, new ReBinder(ctx), getBoundObjects()));

        return ctx;
    }

    public void addClass(Class someClass) {
        VMContextInjection annotation;
        try {
            annotation = (VMContextInjection) someClass.getAnnotation(VMContextInjection.class);
        } catch (NullPointerException ex) {
            annotation = null;
        }
        String identificator;
        if (annotation == null) {
            identificator = someClass.getName();
        } else {
            identificator = annotation.contextObjectName();
        }
        addObject(identificator, someClass);
    }

    public void addObject(Object object) {
        VMContextInjection annotation;
        try {
            annotation = object.getClass().getAnnotation(VMContextInjection.class);
        } catch (NullPointerException ex) {
            annotation = null;
        }
        String identificator;
        if (annotation == null) {
            identificator = object.getClass().getName();
        } else {
            identificator = annotation.contextObjectName();
        }
        addObject(identificator, object);
    }

    private final Map<String, Object> appliedObjects = new HashMap<>();

    public void addObject(String identificator, Object object) {
        Object jsObject = prepareJSObject(object);
        applyAccessors(identificator, jsObject);
        bindObject(identificator, jsObject);
    }

    private Map<String, Object> getBoundObjects() {
        return appliedObjects;
    }

    private void bindObject(String identificator, Object javaObject) {
        Value bindings = this.getBindings();
        bindings.putMember(identificator, javaObject);
        appliedObjects.put(identificator, javaObject);

        if (childs != null && !childs.isEmpty()) {
            fireBindObjectEvent(new BindObjectEvent(this, nesting, new ReBinder(childs), identificator, javaObject));
        }
    }

    protected void bindObjects(Map<String, Object> javaObjects) {
        Value bindings = this.getBindings();
        for (Map.Entry<String, Object> entry : javaObjects.entrySet()) {
            String identificator = entry.getKey();
            Object javaObject = entry.getValue();
            bindings.putMember(identificator, javaObject);
            appliedObjects.put(identificator, javaObject);
        }
        if (childs != null && !childs.isEmpty()) {
            fireBindObjectEvent(new BindObjectEvent(this, nesting, new ReBinder(childs), javaObjects));
        }
    }

    private Object prepareJSObject(Object object) {
        return object;
        /* TODO: uncomment for graalvm 21.3
        JSContext jsContext = JavaScriptLanguage.getJSContext(ctx);
        return JSProxyObject.create(jsContext.getRealm(), jsContext.getProxyFactory(), object, JSObjectPrototype.create(jsContext));
         */
    }

    private void applyAccessors(String identificator, Object object) throws SecurityException {

        Class<? extends Object> aClass = object.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
//            Accessor accessor = field.getAnnotation(Accessor.class);
//            if (accessor != null) {
//                String name = accessor.name();
//                String getter = accessor.getter();
//                String setter = accessor.setter();
//
//                try {
//
//                    ctx.enter();
//                    Value member = this.getBindings().getMember(identificator);
//                    Object setterBody = setter.isBlank() ? Undefined.instance : ctx.eval(JS, "x => this." + setter + "(x)");
//                    member.putMember(name, setterBody);

//                    StringBuilder sb = new StringBuilder();
//                    sb.append("Object.defineProperty(")
//                            .append(identificator)
//                            .append(", \"")
//                            .append(name)
//                            .append("\", {\n");
//
//                    if (setter.isBlank()) {
//                        sb.append("  set: undefined,\n");
//                    } else {
//                        sb.append("  set: function(value) {this.")
//                                .append(setter)
//                                .append("(value);},\n");
//                    }
//                    if (getter.isBlank()) {
//                        sb.append("  get: undefined,\n");
//                    } else {
//                        sb.append("  get: function() {return this.")
//                                .append(getter)
//                                .append("();},\n");
//
//                    }
//                    sb.append("});");
////                    ctx.eval(JS, sb.toString());
//                } finally {
//                    ctx.leave();
//                }
//            }
        }
    }

    @Override
    public void close() throws VMException {
        List<VMContext> tmp = new ArrayList<>(childs);
        for (VMContext ctx : tmp) {
            ctx.close();
        }

        try {
            getPolyglotContext().close();
        } catch (Exception ex) {
            throw new VMException(ex);
        }
        if (parent != null) {
            parent.childs.remove(this);
        }
    }

    private static void fireBridgeEvent(BridgeEvent event) {
        VM.getContextEventEmitter().emitEvent(event, BridgeListener.class);
    }

    private static void fireBindObjectEvent(BindObjectEvent event) {
        VM.getContextEventEmitter().emitEvent(event, BindObjectListener.class);
    }

    private static void fireContextNestingEvent(VMContextNestingEvent event) {
        VM.getContextEventEmitter().emitEvent(event, VMContextNestingListener.class);
    }

    private static void fireHostAccessEvent(HostAccessEvent event) {
        VM.getContextEventEmitter().emitEvent(event, HostAccessListener.class);
    }

}
