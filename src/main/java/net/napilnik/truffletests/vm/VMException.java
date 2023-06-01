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
public class VMException extends Exception {

    private static final long serialVersionUID = -3851696387956091614L;

    private int lineNumber;
    private int columnNumber;
    private String method;
    private String line;
    private String detailMessage;

    public VMException(Throwable t) {
        super(t);
    }

    public VMException(String msg) {
        super(msg);
    }

    public VMException(String method, int lineNumber, int columnNumber, String line, String message, String detailMessage) {
        super(message);
        this.method = method;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.line = line;
        this.detailMessage = detailMessage;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public String getMethod() {
        return method;
    }

}
