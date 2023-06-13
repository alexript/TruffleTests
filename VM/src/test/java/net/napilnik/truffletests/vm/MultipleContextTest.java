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

import java.io.IOException;
import java.util.Set;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author malyshev
 */
public class MultipleContextTest {

    @Test
    public void testMultipleContext() {
        try (Engine engine = Engine.create()) {

            Source source = Source.newBuilder("js", "function getAnswer(){return 21 + 21;}", "testMultipleContext.js")
                    .cached(true)
                    .build();

            try (Context context = Context.newBuilder()
                    .engine(engine)
//                    .allowInnerContextOptions(true)
//                    .allowValueSharing(true)
                    .build()) {
                context.eval(source);
                context.getBindings("js").getMemberKeys();
                int v = context.getBindings("js").getMember("getAnswer").execute().asInt();
                assertEquals(42, v);

                try (Context contextInner = Context.newBuilder()
                        .engine(engine)
//                        .allowInnerContextOptions(true)
//                        .allowValueSharing(true)
                        .build()) {

                    Set<Source> cachedSources = engine.getCachedSources();
                    for (Source s : cachedSources) {
                        contextInner.eval(s);
                    }
                    contextInner.getBindings("js").getMemberKeys();
                    int vInner = contextInner.getBindings("js").getMember("getAnswer").execute().asInt();
                    assertEquals(42, vInner);
                }
            }

        } catch (IOException ex) {
            fail(ex);
        }
    }
}
