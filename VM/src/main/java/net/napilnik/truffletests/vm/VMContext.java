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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.truffletests.vm.javabridge.BridgeEvent;
import net.napilnik.truffletests.vm.javabridge.BridgeListener;
import net.napilnik.truffletests.vm.nesting.VMContextNestingEvent;
import net.napilnik.truffletests.vm.nesting.VMContextNestingListener;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.Value;

/**
 *
 * @author malyshev
 */
public class VMContext extends VMEvaluator implements AutoCloseable {

    private final List<VMContext> childs;
    private final VMContext parent;
    private final String contextName;
    protected static final VMContext GLOBALCONTEXT = constructGlobalContext();

    private static VMContext constructGlobalContext() {
        return constructGlobalContext("");
    }

    private static VMContext constructGlobalContext(String contextName) {
        VMContext vmContext = new VMContext("<RootContext" + contextName + ">", VMLanguage.JS, null, Nesting.None, false);
        BridgeEvent event = new BridgeEvent(vmContext, Nesting.None);
        VM.getContextEventEmitter().emitEvent(event, BridgeListener.class);

        try {
            VMStdLib.apply(vmContext);
        } catch (VMException ex) {
            Logger.getLogger(VMContext.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    }

    @Override
    public String getName() {
        return contextName;
    }

    private static Context buildContext(VMLanguage lng, PolyglotContextProvider parent, Nesting nestingMode) {
        Context.Builder builderTemplate = Context.newBuilder(lng.toPolyglot())
                .allowHostAccess(HostAccessProvider.HOST_ACCESS)
                .allowCreateThread(true)
                .allowValueSharing(true)
                .allowExperimentalOptions(true)
                .allowInnerContextOptions(true)
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .hostClassLoader(ClassLoader.getSystemClassLoader());

//        System.out.println("%s -> %s".formatted(((parent == null) ? "null" : parent.toString()), nestingMode.name()));
        if (parent == null) {
            Engine engine = Engine.newBuilder().build();
            builderTemplate.engine(engine);
        } else {
            if (parent == GLOBALCONTEXT) {
                Engine engine = Engine.newBuilder().build();
                builderTemplate.engine(engine);
            } else {
                if (nestingMode != Nesting.Cache) {
                    Engine engine = Engine.newBuilder().build();
                    builderTemplate.engine(engine);

                } else {
                    builderTemplate.engine(parent.getPolyglotContext().getEngine());
                }
            }
        }
        Context ctx = builderTemplate.build();
        if (parent != null) {
            VMContextNestingEvent event = new VMContextNestingEvent(lng, parent.getPolyglotContext(), ctx, nestingMode);
            VM.getContextEventEmitter().emitEvent(event, VMContextNestingListener.class);
        }
        return ctx;
    }

    protected VMContext create(String contextName, Nesting nestingMode, boolean withInspection) {
        VMContext ctx = new VMContext(contextName, this, nestingMode, withInspection);
        BridgeEvent event = new BridgeEvent(ctx, nestingMode);
        VM.getContextEventEmitter().emitEvent(event, BridgeListener.class);
        return ctx;
    }

    protected VMContext create(String contextName, VMLanguage lng, Nesting nestingMode, boolean withInspection) {
        VMContext ctx = new VMContext(contextName, lng, this, nestingMode, withInspection);
        BridgeEvent event = new BridgeEvent(ctx, nestingMode);
        VM.getContextEventEmitter().emitEvent(event, BridgeListener.class);
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

    public void addObject(String identificator, Object object) {
        synchronized (this.getPolyglotContext()) {
            Value bindings = this.getBindings();
            Object jsObject = prepareJSObject(object);
            bindings.putMember(identificator, jsObject);

            applyAccessors(identificator, jsObject);

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

}
