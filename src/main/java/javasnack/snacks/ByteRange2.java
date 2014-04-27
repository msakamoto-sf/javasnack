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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ByteRange2 implements Runnable {
    @Override
    public void run() {
        byte[] bdata = new byte[256];
        byte b = 0;
        for (short s = 0; s < 256; s++, b++) {
            bdata[s] = b;
        }
        FileOutputStream fos = null;
        try {
            System.out.println("");
            System.out.print("Enter FileName:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String fname = br.readLine().trim();
            fos = new FileOutputStream(fname);
            fos.write(bdata);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try { fos.close(); } catch (IOException ignore) {}
            }
        }
    }

}
