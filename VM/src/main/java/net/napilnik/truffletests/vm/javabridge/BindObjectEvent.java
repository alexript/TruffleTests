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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.napilnik.truffletests.vm.ReBinder;
import net.napilnik.truffletests.vm.VMContext;
import net.napilnik.truffletests.vm.events.VMContextEvent;
import net.napilnik.truffletests.vm.events.VMContextEventType;
import net.napilnik.truffletests.vm.nesting.Nesting;

/**
 *
 * @author malyshev
 */
public class BindObjectEvent extends VMContextEvent<VMContext> {

    private static final long serialVersionUID = -5128246791062201145L;
    private final Nesting nesting;
    private final Map<String, Object> bindings;
    private final ReBinder rebinder;

    public BindObjectEvent(VMContext source, Nesting nesting, ReBinder rebinder, String identificator, Object javaObject) {
        super(source, VMContextEventType.BindObject);
        this.nesting = nesting;
        this.rebinder = rebinder;
        bindings = new HashMap<>();
        bindings.put(identificator, javaObject);
    }

    public BindObjectEvent(VMContext source, Nesting nesting, ReBinder rebinder, Map<String, Object> javaObjects) {
        super(source, VMContextEventType.BindObject);
        this.nesting = nesting;
        this.rebinder = rebinder;
        bindings = new HashMap<>(javaObjects);
    }

    public final Nesting getNesting() {
        return nesting;
    }

    public final ReBinder getRebinder() {
        return rebinder;
    }

    public final Map<String, Object> getBindings() {
        return bindings;
    }
}
