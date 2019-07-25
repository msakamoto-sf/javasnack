/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javasnack.tool;

import java.io.File;
import java.io.IOException;

public class FileDirHelper {

    /* Create temporary directory (<= JDK 1.6) (from JDK 1.7, Files.createTempDirectory available)
     *
     * see:
     * http://stackoverflow.com/questions/617414/create-a-temporary-directory-in-java
     */
    /**
     * @return created temporary directory
     * @throws IOException if directory creation failed
     */
    public static File createTmpDir() throws IOException {
        File tmpFile = File.createTempFile("temp",
                Long.toString(System.nanoTime()));
        String fullPath = tmpFile.getAbsolutePath();
        tmpFile.delete();
        File tmpDir = new File(fullPath);
        if (!tmpDir.mkdir()) {
            throw new IOException("Failed to create tmpDir: " + fullPath);
        }
        return tmpDir;
    }
}
