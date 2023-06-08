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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author malyshev
 */
public class JSDateTest {

    @Test
    public void testJsDate() {
        try {
            VMScript script = new VMScript("jsDateTest.js", "function getDate() {return new Date();}");
            VMContext context = VM.context();
            try (context) {
                context.eval(script);
                Value result = context.eval("getDate", Value.class);
                assertNotNull(result);
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

    @Test
    public void testJsDateObject() {
        try {
            Date now = new Date();
            VMScript script = new VMScript("testJsDateObject.js", "function getDate() {return new Date();}");
            VMContext context = VM.context();
            try (context) {

                context.eval(script);
                Value result = context.eval("getDate", Value.class);
                LocalDate asDate = result.asDate();
                LocalTime asTime = result.asTime();
                assertNotNull(asDate);
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }

    private static VMScript stdlib;

    static {
        try {
            stdlib = new VMScript("stdlib.js", """
                                                           function __getJsDate(javaDate) {
                                                               let d= new Date(javaDate.getTime());
                                                               return d;
                                                           }
                                                           """);
        } catch (VMException ex) {
            Logger.getLogger(JSDateTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testJavaToJsDateObject() {
        try {
            Date now = new Date();
            class StdContextHolder {

                public VMContext stdContext;
            }

            class StdContextCreator implements Runnable {

                private final StdContextHolder target;

                public StdContextCreator(StdContextHolder target) {
                    this.target = target;
                }

                @Override

                public void run() {
                    target.stdContext = VM.context();
                }

            }
            StdContextHolder stdContextHolder = new StdContextHolder();
            Thread t = new Thread(new StdContextCreator(stdContextHolder));
            t.start();
            t.join();
            stdContextHolder.stdContext.eval(stdlib);

            class Result {

                public Date expect;
                public Date result;
                public Value value;
            }

            Result r = new Result();
            r.expect = new Date(now.getYear(), now.getMonth(), now.getDate(), now.getHours(), now.getMinutes(), now.getSeconds());

            class GetDateTest implements Runnable {

                private final Result r;

                public GetDateTest(Result result) {
                    this.r = result;
                }

                @Override
                public void run() {
                    try {
                        VMScript script = new VMScript("testJavaToJsDateObject.js", "function getDate(jsDate) {return jsDate;}");
                        VMContext context = VM.context();
                        try (context) {
                            Value jsDate = stdContextHolder.stdContext.eval("__getJsDate", Value.class, now);
                            context.eval(script);
                            Value result = context.eval("getDate", Value.class, jsDate);
                            LocalDate asDate = result.asDate();
                            LocalTime asTime = result.asTime();
                            r.value = result;
                            r.result = new Date(asDate.getYear() - 1900, asDate.getMonthValue() - 1, asDate.getDayOfMonth(), asTime.getHour(), asTime.getMinute(), asTime.getSecond());
                        }
                    } catch (VMException ex) {
                        fail(ex);
                    }
                }

            }
            t = new Thread(new GetDateTest(r));
            t.start();
            t.join();
            stdContextHolder.stdContext.close();
            Assertions.assertEquals(r.expect, r.result);

            class JSDateConsumer implements Runnable {

                private final Result r;

                public JSDateConsumer(Result r) {
                    this.r = r;
                }

                @Override
                public void run() {
                    try {
                        VMScript script = new VMScript("testValueReuse.js", "function getDate(jsDate) {return jsDate;}");
                        VMContext context = VM.context();
                        try (context) {
                            context.eval(script);
                            Value result = context.eval("getDate", Value.class, r.result);
                            LocalDate asDate = result.asDate();
                            LocalTime asTime = result.asTime();
                            r.result = new Date(Date.UTC(asDate.getYear() - 1900, asDate.getMonthValue() - 1, asDate.getDayOfMonth(), asTime.getHour(), asTime.getMinute(), asTime.getSecond()));
                        }
                    } catch (VMException ex) {
                        fail(ex);
                    }
                }
            }

            t = new Thread(new JSDateConsumer(r));
            t.start();
            t.join();
            Assertions.assertEquals(r.expect, r.result);

        } catch (VMException | InterruptedException ex) {
            fail(ex);
        }

    }

    @VMContextInjection(contextObjectName = "Date")
    public static class DateOverride {

        @VMAccess
        public DateOverride() {

        }
    }

    @Test
    public void testDateOverride() {

        try {
            VMScript script = new VMScript("testDateOverride.js", "function getDate() {return new Date();}");
            VMContext context = VM.context();
            try (context) {
                context.addClass(DateOverride.class);
                context.eval(script);
                DateOverride result = context.eval("getDate", DateOverride.class);
                assertNotNull(result);
            }
        } catch (VMException ex) {
            fail(ex);
        }
    }
}