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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Optional;
import org.graalvm.polyglot.Source;

/**
 *
 * @author malyshev
 */
public class VMScript {

    private final Source source;
    private final VMLanguage lng;

    public VMScript(File file) throws VMException {
        this(file.getName(), readFile(file));
    }

    public VMScript(String filename, InputStream is) throws VMException {
        this(filename, readInputStream(is));
    }

    public VMScript(String scriptFileName, String scriptFileBody) throws VMException {
        this.lng = getLanguageFor(scriptFileName);
        try {
            source = Source.newBuilder(lng.toPolyglot(), scriptFileBody, scriptFileName)
                    .cached(true)
                    .build();
        } catch (IOException ex) {
            throw new VMException(ex);
        }
    }

    private static String readInputStream(InputStream is) throws VMException {
        String fileBody = "";

        try (Reader reader = new InputStreamReader(is, "UTF-8")) {
            BufferedReader breader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = breader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            fileBody = builder.toString();
        } catch (IOException ex) {
            throw new VMException(ex);
        }
        return fileBody;
    }

    private static String readFile(File file) throws VMException {
        String fileBody = "";
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            fileBody = new String(bytes, "UTF-8");
        } catch (IOException ex) {
            throw new VMException(ex);
        }
        return fileBody;
    }

    private static VMLanguage getLanguageFor(String fileName) {
        Optional<String> sfx = Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
        return VMLanguage.findByFilenameSuffix(sfx.get());
    }

    public Source getSource() {
        return source;
    }

}
