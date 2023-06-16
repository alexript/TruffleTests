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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.napilnik.truffletests.vm.javabridge.BindObjectEvent;

/**
 *
 * @author malyshev
 */
public final class ReBinder {

    private final List<VMContext> childs;

    public ReBinder(VMContext child) {
        this.childs = new ArrayList<>(1);
        this.childs.add(child);
    }

    public ReBinder(List<VMContext> childs) {
        this.childs = childs;
    }

    public void rebind(BindObjectEvent bindObjectEvent) {
        if (childs != null && !childs.isEmpty()) {
            Map<String, Object> boundObjects = bindObjectEvent.getBindings();
            if (!boundObjects.isEmpty()) {
                for (VMContext ctx : childs) {
                    ctx.bindObjects(boundObjects);
                }
            }
        }
    }
}
