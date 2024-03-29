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

import org.graalvm.polyglot.Context;

/**
 * Предоставлятор полиглот-контекста. Я не знаю, почему я сделал его абстрактным
 * классом, а не интерфейсом.
 *
 * @author malyshev
 */
public abstract class PolyglotContextProvider {

    protected PolyglotContextProvider() {

    }

    abstract protected String getName();

    abstract public Context getPolyglotContext();

    @Override
    public String toString() {
        return "PolyglotContextProvider: %s".formatted(getName());
    }

}
