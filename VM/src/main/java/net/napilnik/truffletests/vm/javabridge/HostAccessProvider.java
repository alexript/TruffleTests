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
package net.napilnik.truffletests.vm.javabridge;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.napilnik.truffletests.vm.annotations.VMAccess;
import net.napilnik.truffletests.vm.annotations.VMClass;
import net.napilnik.truffletests.vm.annotations.VMObject;
import org.graalvm.polyglot.HostAccess;

/**
 * Предоставлятор полиглотного построителя прав доступа.
 *
 * @author malyshev
 */
public class HostAccessProvider {

    protected static final HostAccess DEFAULT_HOST_ACCESS = buildHostAccess().build();

    private static HostAccess.Builder buildHostAccess() {
        HostAccess.Builder builder = HostAccess.newBuilder()
                .allowPublicAccess(false)
                .allowAccessAnnotatedBy(VMAccess.class)
                .allowAccessAnnotatedBy(VMClass.class)
                .allowAccessAnnotatedBy(VMObject.class)
                .allowAllImplementations(true)
                .allowAllClassImplementations(true)
                .allowArrayAccess(true)
                .allowListAccess(true)
                .allowBufferAccess(true)
                .allowIterableAccess(true)
                .allowIteratorAccess(true)
                .allowMapAccess(true)
                .allowAccessInheritance(true);

        allowAll(builder, Object.class);
        allowAll(builder, Date.class);
        allowAll(builder, Collection.class);
        allowAll(builder, List.class);
        allowAll(builder, Map.class);
        allowAll(builder, Number.class);
        allowAll(builder, Double.class);
        allowAll(builder, Integer.class);
        allowAll(builder, BigDecimal.class);
        allowAll(builder, String.class);
        allowAll(builder, Character.class);
        allowAll(builder, Boolean.class);

        return builder;
    }

    private static void allowAll(HostAccess.Builder access, Class c) {
        allowAllFields(access, c);
        allowAllMethods(access, c);
    }

    private static void allowAllFields(HostAccess.Builder access, Class c) {
        if (c != null) {
            for (Field field : c.getFields()) {
                access.allowAccess(field);
            }
        }
    }

    private static void allowAllMethods(HostAccess.Builder access, Class c) {
        if (c != null) {
            for (Method method : c.getMethods()) {
                access.allowAccess(method);
            }
        }
    }

    /**
     * Создать полиглотного построителя прав доступа.
     *
     * @return полиглотный построитель прав доступа.
     */
    public static HostAccess.Builder build() {
        return buildHostAccess();
    }

}
