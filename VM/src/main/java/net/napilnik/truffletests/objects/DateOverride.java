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

import com.google.gson.internal.bind.util.ISO8601Utils;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import net.napilnik.truffletests.vm.VMAccess;
import net.napilnik.truffletests.vm.VMContextInjection;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

/**
 *
 * @author malyshev
 */
@VMContextInjection(contextObjectName = "Date")
public class DateOverride extends Date implements ProxyObject {

    private static final long serialVersionUID = -3400411885843649559L;

    private static final Set<String> PROTOTYPE_FUNCTIONS = new HashSet<>(Arrays.asList(new String[]{
        "getTime",
        "toISOString",
        "toJSON",
        "toString"}));

    @VMAccess
    public DateOverride() {
        super();
    }

    @VMAccess
    public DateOverride(Date date) {
        super(date.getTime());
    }

    @VMAccess
    public DateOverride(long epoch) {
        super(epoch);
    }

    @Override
    public Object getMember(String key) {
        switch (key) {
            case "getTime":
                return (ProxyExecutable) arguments -> getTime();
            case "toJSON":
            case "toISOString":
                return (ProxyExecutable) arguments -> ISO8601Utils.format(this, true);
            case "toString":
                // Currently defaulting to Date.toString, but could improve
                return (ProxyExecutable) arguments -> toString();
            default:
                throw new UnsupportedOperationException("This date does not support: " + key);
        }
    }

    @Override
    public Object getMemberKeys() {
        return PROTOTYPE_FUNCTIONS.toArray();
    }

    @Override
    public boolean hasMember(String key) {
        return PROTOTYPE_FUNCTIONS.contains(key);
    }

    @Override
    public void putMember(String key, Value value) {
        throw new UnsupportedOperationException("This date does not support adding new properties/functions.");
    }
}
