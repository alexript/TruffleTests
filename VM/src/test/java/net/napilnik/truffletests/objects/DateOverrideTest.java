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
package net.napilnik.truffletests.objects;

import net.napilnik.truffletests.vm.nesting.Nesting;
import java.util.Date;
import net.napilnik.truffletests.vm.VM;
import net.napilnik.truffletests.vm.VMContext;
import net.napilnik.truffletests.vm.VMException;
import net.napilnik.truffletests.vm.VMScript;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import net.napilnik.truffletests.vm.annotations.VMAccess;
import net.napilnik.truffletests.vm.annotations.VMClass;

/**
 *
 * @author malyshev
 */
public class DateOverrideTest {

    @Test
    public void testDate() {
        try {
            VMScript script = new VMScript("testDate.js", "function getDate() {return new Date();}");
            VMContext contextNone = VM.context(Nesting.None);
            try (contextNone) {
                contextNone.eval(script);
                Date result = contextNone.eval("getDate", Date.class);
                assertNotNull(result);
            }
            VMContext contextNaive = VM.context(Nesting.Naive);
            try (contextNaive) {
                contextNaive.eval(script);
                Date result = contextNaive.eval("getDate", Date.class);
                assertNotNull(result);
            }
            VMContext contextCache = VM.context(Nesting.Cache);
            try (contextCache) {
                contextCache.eval(script);
                Date result = contextCache.eval("getDate", Date.class);
                assertNotNull(result);
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

    @Test
    public void testDateOverride() {

        try {
            VMScript script = new VMScript("testDateOverride.js", "function getDate() {return new Date();}");
            VMScript scriptJson = new VMScript("getDateAsJson.js", "function getDateAsJson() {let d= new Date(); return d.toJSON();}");
            VMContext context = VM.context(Nesting.Naive);
            try (context) {

                context.eval(script);

                Date typeResult = context.eval("getDate", Date.class);
                assertNotNull(typeResult);

                context.eval(scriptJson);
                String json = context.eval("getDateAsJson", String.class);
                System.out.println(json);

            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

    @VMClass(value = "Date")
    public static class DateOverride2 {

        @VMAccess
        public DateOverride2() {

        }
    }

    @Test
    public void testDateOverride2() {

        try {
            VMScript script = new VMScript("testDateOverride2.js", "function getDate() {return new Date();}");
            VMContext context = VM.context(Nesting.None);
            try (context) {
                context.addClass(DateOverride2.class);
                context.eval(script);
                DateOverride2 result = context.eval("getDate", DateOverride2.class);
                assertNotNull(result);
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }
}
