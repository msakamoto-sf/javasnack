/*
 * Copyright 2017 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.langspecs;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestClassExtends {

    class P1 {
        public String whoami() {
            return this.getClass().getSimpleName();
        }
    }

    class C1 extends P1 {
    }

    class C2 extends P1 {
    }

    @Test
    public void testWhoAmI() {
        P1 p1 = new P1();
        Assert.assertEquals(p1.whoami(), "P1");
        C1 c1 = new C1();
        Assert.assertEquals(c1.whoami(), "C1");
        C2 c2 = new C2();
        Assert.assertEquals(c2.whoami(), "C2");

        p1 = c1;
        Assert.assertEquals(p1.whoami(), "C1");
    }

}
