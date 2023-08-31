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

import java.io.InputStream;
import java.util.Date;
import org.graalvm.polyglot.Value;

/**
 * Поддержка стандартной библиотеки.
 *
 * @author malyshev
 */
public class VMStdLib extends VMScript {

    private static final String STDLIBJS = "stdlib.js";
    private static final String STD_LIB_OBJECTNAME = "StdLib";

    public static void apply(VMContext context) throws VMException {
        VMScript stdlib = new VMStdLib();
        context.eval(stdlib);
    }

    private static InputStream getIS() throws VMException {
        InputStream is = VMStdLib.class.getResourceAsStream(STDLIBJS);
        if (is == null) {
            throw new VMException(STDLIBJS + " not found");
        }
        return is;
    }

    private VMStdLib() throws VMException {
        super(STDLIBJS, getIS());
    }

    public static Value javaDateToJSDate(VMContext c, Date d) throws VMException {
        return c.eval(STD_LIB_OBJECTNAME, "__getJsDate", Value.class, d);
    }

}
