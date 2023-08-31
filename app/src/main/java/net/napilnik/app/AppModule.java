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

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static net.napilnik.app.App.ROOT;

/**
 *
 * @author malyshev
 */
public class AppModule implements Serializable {

    private static final long serialVersionUID = 2733064793313755146L;
    private static final String ROOTMNEMO = "__ROOT__";

    public static final AppModule ROOT = new AppModule(null, ROOTMNEMO, ROOTMNEMO);

    @SerializedName("mnemo")
    private String mnemo;

    @SerializedName("title")
    private String title;

    @SerializedName("super")
    private String superModuleMnemo;

    @SerializedName("deps")
    private HashSet<String> dependencies;

    @SerializedName("sharedScripts")
    private ModuleScripts sharedScripts;

    @SerializedName("serverScripts")
    private ModuleScripts serverScripts;

    @SerializedName("clientScripts")
    private ModuleScripts clientScripts;

    public static enum SCRIPT_TYPE {
        Shared, Server, Client
    };

    public AppModule(String mnemo, String title) {
        this(ROOT, mnemo, title);
    }

    protected AppModule(AppModule superModule, String mnemo, String title) {
        this.superModuleMnemo = (superModule == null && ROOTMNEMO.equals(mnemo)) ? ROOTMNEMO : (superModule == null || superModule.isApplication()) ? ROOT.mnemo : superModule.mnemo;
        this.dependencies = new HashSet<>();
        this.title = title;
        this.mnemo = mnemo;
        sharedScripts = new ModuleScripts();
        serverScripts = new ModuleScripts();
        clientScripts = new ModuleScripts();
    }

    @SerializedName("isapp")
    public boolean isApplication() {
        return false;
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

    public String getSuper() {
        return superModuleMnemo;
    }

    public void setSuper(AppModule superModule) {
        this.superModuleMnemo = (superModule == null || superModule.isApplication()) ? ROOT.mnemo : superModule.mnemo;
    }

    public Set<String> getDependencies() {
        return new HashSet<>(dependencies);
    }

    public void addDependency(AppModule dep) {
        if (!dep.isApplication()) {
            dependencies.add(dep.mnemo);
        }
    }

    public void removeDependency(AppModule dep) {
        dependencies.remove(dep.mnemo);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof AppModule module) {
            return mnemo.equals(module.mnemo);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.mnemo);
        return hash;
    }

    public ModuleScripts getSharedScripts() {
        return sharedScripts;
    }

    public void setSharedScripts(ModuleScripts sharedScripts) {
        this.sharedScripts = sharedScripts;
    }

    public ModuleScripts getServerScripts() {
        return serverScripts;
    }

    public void setServerScripts(ModuleScripts serverScripts) {
        this.serverScripts = serverScripts;
    }

    public ModuleScripts getClientScripts() {
        return clientScripts;
    }

    public void setClientScripts(ModuleScripts clientScripts) {
        this.clientScripts = clientScripts;
    }

    public void addScript(SCRIPT_TYPE type, String scriptName, String scriptBody) {
        switch (type) {
            case Client ->
                getClientScripts().addScript(scriptName, scriptBody);
            case Server ->
                getServerScripts().addScript(scriptName, scriptBody);
            default ->
                getSharedScripts().addScript(scriptName, scriptBody);
        }
    }

    public Map<String, String> getScripts(SCRIPT_TYPE type) {
        return switch (type) {
            case Client ->
                getClientScripts().getScripts();
            case Server ->
                getServerScripts().getScripts();
            default ->
                getSharedScripts().getScripts();
        };
    }
}
