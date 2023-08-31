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
package net.napilnik.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.napilnik.app.App;
import net.napilnik.truffletests.vm.Evaluator;
import net.napilnik.truffletests.vm.nesting.Nesting;

/**
 * Пул корневых контекстов приложений. Служит их кэшем. Причем, я не предлагаю
 * программисту создавать пул. Я даю уже готовые. Изолированные друг от друга.
 *
 * @author malyshev
 */
public class ExecutorsPool {

    private final Nesting userContextNestingMode;

    /**
     * Пул, на котором контекст вызова создается Naive нестингом.
     */
    public static final ExecutorsPool NAIVE = new ExecutorsPool(Nesting.Naive);

    /**
     * Пул, на котором контекст вызова создается Cache нестингом.
     */
    public static final ExecutorsPool CAHCE = new ExecutorsPool(Nesting.Cache);

    /**
     * Пул, на котором контекст вызова создается None нестингом.
     */
    public static final ExecutorsPool NONE = new ExecutorsPool(Nesting.None);

    private ExecutorsPool(Nesting userContextNestingMode) {
        this.userContextNestingMode = userContextNestingMode;
    }

    private final Map<String, AppExecutor> pool = new ConcurrentHashMap<>();

    /**
     * Получить исполнитель для приложения.
     *
     * @param app приложение
     * @return исполнитель
     */
    public Evaluator get(App app) {
        String mnemo = app.getMnemo();

        if (!pool.containsKey(mnemo)) {
            pool.put(mnemo, new AppExecutor(app, userContextNestingMode));
        }
        Evaluator ev = pool.get(mnemo);
        return ev;
    }
}
