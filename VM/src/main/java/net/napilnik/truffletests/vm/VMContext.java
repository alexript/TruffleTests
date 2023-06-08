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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.Value;

/**
 *
 * @author malyshev
 */
public class VMContext extends VMEvaluator implements AutoCloseable {

    private final List<VMContext> childs;
    private final VMContext parent;

    protected static final VMContext GLOBALCONTEXT = constructGlobalContext();

    private static VMContext constructGlobalContext() {
        VMContext vmContext = new VMContext(VMLanguage.JS, null, false);
        GlobalBindings.bind(vmContext);
        try {
            VMStdLib.apply(vmContext);
        } catch (VMException ex) {
            Logger.getLogger(VMContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vmContext;
    }

    private VMContext(VMContext parent, boolean withInspection) {
        this(VMLanguage.JS, parent, withInspection);
    }

    private VMContext(VMLanguage lng, VMContext parent, boolean withInspection) {
        super(lng, buildContext(lng), withInspection);
        childs = new ArrayList<>();
        this.parent = parent;
    }

    private static Context buildContext(VMLanguage lng) {
        return Context.newBuilder(lng.toPolyglot())
                .allowHostAccess(HostAccessProvider.HOST_ACCESS)
                .allowCreateThread(true)
                .allowValueSharing(true)
                .allowExperimentalOptions(true)
                .allowInnerContextOptions(true)
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT)
                .hostClassLoader(ClassLoader.getSystemClassLoader())
                .build();
    }

    protected VMContext create(boolean withInspection) {
        return new VMContext(this, withInspection);
    }

    protected VMContext create(VMLanguage lng, boolean withInspection) {
        return new VMContext(lng, this, withInspection);
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
        synchronized (this.getContext()) {
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
            getContext().close();
        } catch (Exception ex) {
            throw new VMException(ex);
        }
        if (parent != null) {
            parent.childs.remove(this);
        }
    }

}
