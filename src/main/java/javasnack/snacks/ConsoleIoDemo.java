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

import java.io.Console;
import java.io.PrintWriter;
import java.util.Objects;

import javasnack.RunnableSnack;

public class ConsoleIoDemo implements RunnableSnack {
    @Override
    public void run(final String... args) {
        // JDK 1.6 : java.io.Console & System.console()
        Console console = System.console();
        if (Objects.isNull(console)) {
            System.out.println("'System.console()' returns null, Console is disabled.");
            return;
        }

        console.printf("Console.printf() : %s, %d%n", "hello", 100);
        console.format("Console.format() : %s, %d%n", "world", 200);
        PrintWriter pw = console.writer();
        pw.printf("Console.writer().printf() : %s, %d%n", "HELLO", 300);
        pw.format("Console.writer().format() : %s, %d%n", "WORLD", 400);
        pw.flush();
        console.printf("enter some text>");
        String s = console.readLine();
        console.printf("input text=[%s]%n", s);
        s = console.readLine("enter %s text(2)>", "some");
        console.printf("input text(2)=[%s]%n", s);

        console.printf("enter dummy password>");
        char[] password = console.readPassword();
        console.printf("Input Password = [%s]%n", new String(password));
        password = console.readPassword("enter dummy password(%d)>", 2);
        console.printf("Input Password(2) = [%s]%n", new String(password));

        System.out.println("(END)");
    }
}
