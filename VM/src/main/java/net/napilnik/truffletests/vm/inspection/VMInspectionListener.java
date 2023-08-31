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
package net.napilnik.truffletests.vm.inspection;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.management.ExecutionEvent;

/**
 * Конкретный слушатель событий инспектора.
 *
 * @author malyshev
 */
public class VMInspectionListener extends VMInspector.Listener {

    private final String prefix;
    private int lineNumber;
    private int columnNumber;
    private String line;
    PolyglotException exception;

    public VMInspectionListener(String prefixMsg) {
        this.prefix = prefixMsg;
    }

    @Override
    public void accept(ExecutionEvent evt) {
        lineNumber = evt.getLocation().getStartLine();
        columnNumber = evt.getLocation().getEndColumn();
        line = evt.getLocation().getCharacters().toString();

        PolyglotException r = evt.getException();
        if (r != null) {
            exception = r;
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public String getLine() {
        return line;
    }

    public PolyglotException getException() {
        return exception;
    }

}
