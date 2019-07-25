/*
 * Copyright 2013 the original author or authors.
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

package javasnack.cli;

public class CliDemo1 {
    
    /*
     * mvn exec:java \
     *     -Dexec.mainClass=javasnack.cli.CliDemo1 \
     *     -Dexec.classpathScope=test \
     *     -Dexec.args=arg1,arg2,arg3 \
     *     -Dexec.args=arg4,arg5,arg6 \
     *     -Dexec.arguments=123,456 \
     *     -DCliDemo1.prop=abc
     * ->
     * <pre>
     * args.length = [1]
     * arg4,arg5,arg6
     * -DCliDemo1.prop=[abc]
     * </pre>
     */
    
    public static void main(String[] args) {
        System.out.println("args.length = [" + args.length + "]");
        for (String arg : args) {
            System.out.println(arg);
        }
        System.out.println("-DCliDemo1.prop=["
                + System.getProperty("CliDemo1.prop", "default") + "]");
    }
}
