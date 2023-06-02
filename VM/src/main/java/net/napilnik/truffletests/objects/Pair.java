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

import java.util.Arrays;
import net.napilnik.truffletests.vm.VMAccess;
import net.napilnik.truffletests.vm.VMContextInjection;

/**
 *
 * @author malyshev
 */
@VMContextInjection(contextObjectName = "Pair")
public class Pair {

    private Object head;
    private Object[] tail;

    public static class PairException extends RuntimeException {

        private static final long serialVersionUID = -1757262664431112641L;

        public PairException(String msg) {
            super(msg);
        }
    }

    @VMAccess
    public Pair(Object[] objects) {
        if (objects == null || objects.length < 1) {
            throw new PairException("Invalid arguments size");
        }
        switch (objects.length) {
            case 1 -> set(objects[0], null);
            case 2 -> set(objects[0], objects[1]);
            default -> {
                Object[] oTail = Arrays.copyOfRange(objects, 1, objects.length);
                this.head = objects[0];
                this.tail = oTail;
            }
        }
    }

    @VMAccess
    public Pair(Object head, Object tail) {
        set(head, tail);
    }

    @VMAccess
    public Pair(Object head, Object[] tail) {
        if (tail == null) {
            set(head, null);
        } else if (tail.length == 1) {
            set(head, tail[0]);
        } else {
            this.head = head;
            this.tail = tail;
        }
    }

    private void set(Object head, Object tail) {
        this.head = head;
        this.tail = new Object[]{tail};
    }

    @VMAccess
    public Object head() {
        return head;
    }

    @VMAccess
    public Object[] tail() {
        return tail;
    }
}
