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
 *
 * @author malyshev
 */
public interface Evaluator extends AutoCloseable {

    void eval(String functionName) throws VMException;

    <T> T eval(String functionName, Class<T> targetType) throws VMException;

    void eval(String functionName, Object... objArray) throws VMException;

    <T> T eval(String functionName, Class<T> targetType, Object... objArray) throws VMException;

    <T> T eval(String objectName, String fieldName, Class<T> targetType, Object... objArray) throws VMException;

    void eval(VMScript script) throws VMException;

    <T> T eval(VMScript script, Class<T> targetType) throws VMException;

    boolean hasFunction(String functionName);

}
