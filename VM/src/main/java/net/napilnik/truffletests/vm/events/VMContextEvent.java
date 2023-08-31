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
package net.napilnik.truffletests.vm.events;

import java.util.EventObject;

/**
 * Событие, возникающее в контексте на каком-то источнике внутри конеткста.
 *
 * @param <SOURCE> Источник события.
 * @author malyshev
 */
public class VMContextEvent<SOURCE> extends EventObject {

    private static final long serialVersionUID = 7499445933683474877L;
    private final VMContextEventType type;

    public VMContextEvent(SOURCE source, VMContextEventType eventType) {
        super(source);
        this.type = eventType;
    }

    /**
     * Получить тип события.
     *
     * @return тип события
     */
    public final VMContextEventType getType() {
        return type;
    }

    /**
     * Получить источник события.
     *
     * @return источник события.
     */
    @Override
    public final SOURCE getSource() {
        return (SOURCE) super.getSource();
    }

}
