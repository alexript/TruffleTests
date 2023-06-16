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
package net.napilnik.app;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author malyshev
 */
public class App implements Serializable {

    private static final long serialVersionUID = 9007671469574877794L;
    public static final App ROOT = new App(null, "__ROOT__", "__ROOT__");

    private String mnemo;
    private String title;
    private App superApp;
    private Set<App> dependencies;
    private Map<String, String> appScripts;

    public App(String mnemo, String title) {
        this(ROOT, mnemo, title);
    }

    private App(App superApp, String mnemo, String title) {
        this.appScripts = new HashMap<>();
        this.superApp = superApp;
        this.dependencies = new HashSet<>();
        this.title = title;
        this.mnemo = mnemo;
    }

    public String getMnemo() {
        return mnemo;
    }

    public void setMnemo(String mnemo) {
        this.mnemo = mnemo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public App getSuper() {
        return superApp;
    }

    public void setSuper(App superApp) {
        this.superApp = superApp;
    }

    public Set<App> getDependencies() {
        return new HashSet<>(dependencies);
    }

    public void addDependency(App dep) {
        dependencies.add(dep);
    }

    public void removeDependency(App dep) {
        dependencies.remove(dep);
    }

    public Map<String, String> getScripts() {
        return appScripts;
    }

    public void addScript(String scriptName, String scriptBody) {
        appScripts.put(scriptName, scriptBody);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof App app) {
            return mnemo.equals(app.mnemo);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.mnemo);
        return hash;
    }

}
