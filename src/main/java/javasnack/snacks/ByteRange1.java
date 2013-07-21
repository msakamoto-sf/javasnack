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
package javasnack.snacks;

public class ByteRange1 implements Runnable {
    @Override
    public void run() {
        byte b = 0;
        for (short s = 0x00; s <= 0xFF; s++, b++) {
            System.out.print(b);
            System.out.print(",");
            if (s % 8 == 0) {
                System.out.println("");
            }
        }
        System.out.println("(END)");
    }

}
