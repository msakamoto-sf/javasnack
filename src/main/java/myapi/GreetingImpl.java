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
package myapi;

/**
 * for JCL, myapi-impl1.jar, myapi-impl2.jar demo.
 * 
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class GreetingImpl {
    protected String myFirstname;
    protected String myLastname;

    public GreetingImpl(String _myFirstname, String _myLastname) {
        this.myFirstname = _myFirstname;
        this.myLastname = _myLastname;
    }

    public String morning(String to) {
        return "good morning, " + to + " and " + this.myFirstname + " "
                + this.myLastname + ".";
    }

    public String afternoon(String to) {
        return "good afternoon, " + to + " and " + this.myFirstname + " "
                + this.myLastname + ".";
    }
}
