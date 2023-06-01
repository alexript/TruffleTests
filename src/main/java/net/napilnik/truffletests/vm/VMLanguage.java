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
public enum VMLanguage {
    JS("js", new String[]{"js", "jsx", "javascript"});

    private final String polyglotName;
    private final String[] suffixes;

    private VMLanguage(String polyglotLanguageName, String[] filenameSuffixes) {
        this.polyglotName = polyglotLanguageName;
        this.suffixes = filenameSuffixes;
    }

    public String toPolyglot() {
        return polyglotName;
    }

    public static VMLanguage findByFilenameSuffix(String suffix) {
        for (VMLanguage lng : values()) {
            for (String sfx : lng.suffixes) {
                if (sfx.equalsIgnoreCase(suffix)) {
                    return lng;
                }
            }
        }
        return JS;
    }
}
