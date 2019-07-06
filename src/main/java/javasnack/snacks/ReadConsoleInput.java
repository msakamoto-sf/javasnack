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

package javasnack.snacks;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;

public class ReadConsoleInput implements Runnable {
    @Override
    public void run() {

        // Traditional STDIN Read
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in));
            System.out.print("prompot:>");
            String input = br.readLine().trim();
            System.out.println("Input = [" + input + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // JDK 1.6 : java.io.Console & System.console()
        Console console = System.console();
        if (null == console) {
            // NOTE: return null on Eclipse Console view :( 
            System.out
                    .println("'System.console()' returns null, Console is disabled.");
        } else {
            console.printf("Console Output Example %s%n", "abcdefg");
            String input = console.readLine("[%s %s]>", "abc", "def");
            console.printf("Input = [%s]%n", input);
            char[] password = console.readPassword("input dummy password>");
            console.printf("Input Password = [%s]%n", new String(password));
        }

        System.out.println("(END)");
    }

}
