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

/**
 * Выполнятор.
 *
 * @author malyshev
 */
public interface Evaluator extends AutoCloseable {

    /**
     * Выполнить функцию без аргументов и результата.
     *
     * @param functionName название функциии
     * @throws VMException
     */
    void eval(String functionName) throws VMException;

    /**
     * Выполнить функцию без аргументов и получить результат.
     *
     * @param <T> тип результата
     * @param functionName название функции
     * @param targetType класс типа результата
     * @return результат указанного типа
     * @throws VMException
     */
    <T> T eval(String functionName, Class<T> targetType) throws VMException;

    /**
     * Выполнить функцию с аргументами и без результата.
     *
     * @param functionName название функции
     * @param objArray массив аргументов
     * @throws VMException
     */
    void eval(String functionName, Object... objArray) throws VMException;

    /**
     * Выполнить функцию с аргументами и получить результат.
     *
     * @param <T> тип результата
     * @param functionName название функции
     * @param targetType класс типа результата
     * @param objArray массив аргументов
     * @return результат указанного типа
     * @throws VMException
     */
    <T> T eval(String functionName, Class<T> targetType, Object... objArray) throws VMException;

    /**
     * Выполнить функцию объекта с аргументами и получить результат.
     *
     * @param <T> тип результата
     * @param objectName название глобального объекта
     * @param functionName название функции
     * @param targetType класс типа результата
     * @param objArray массив аргументов
     * @return результат указанного типа
     * @throws VMException
     */
    <T> T eval(String objectName, String functionName, Class<T> targetType, Object... objArray) throws VMException;

    /**
     * Выполнить скрипт без результата.
     *
     * @param script тело скрипта.
     * @throws VMException
     */
    void eval(VMScript script) throws VMException;

    /**
     * Выполнить скрипт и получить результат указанного типа.
     *
     * @param <T> тип результата
     * @param script тело скрипта.
     * @param targetType класс типа результата
     * @return результат указанного типа
     * @throws VMException
     */
    <T> T eval(VMScript script, Class<T> targetType) throws VMException;

    /**
     * Проверить существование функции.
     *
     * @param functionName название функции
     * @return состояние существования функции.
     */
    boolean hasFunction(String functionName);

}
