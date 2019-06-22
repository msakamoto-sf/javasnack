/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.json.pojo;

public enum EncodePojoEnum2 {
    ABC(10, "abc"), DEF(20, "def"), GHI(30, "ghi"), JKL(40, "jkl");
    final int num;
    final String name;

    EncodePojoEnum2(int _num, String _name) {
        this.num = _num;
        this.name = _name;
    }

    String getContent() {
        return String.format("num=[%d], name=[%s]", num, name);
    }
}
