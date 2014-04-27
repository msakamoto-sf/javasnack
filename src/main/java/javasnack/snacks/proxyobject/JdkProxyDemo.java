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
package javasnack.snacks.proxyobject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * java.lang.reflect.Proxy demo usage.
 * 
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class JdkProxyDemo implements Runnable {

    @Override
    public void run() {
        InvocationHandler handler = new InvocationHandler() {
            String teaType;
            int temp = 0;
            int cc = 0;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                String mname = method.getName();
                if ("setTeaType".equals(mname)) {
                    switch (args.length) {
                    case 2:
                        teaType = (String) args[0];
                        temp = ((Integer) args[1]).intValue();
                    case 1:
                        teaType = (String) args[0];
                    }
                } else if ("getTemperature".equals(mname)) {
                    return new Integer(temp);
                } else if ("addWater".equals(mname)) {
                    cc += ((Integer) args[0]).intValue();
                    return new Integer(cc);
                } else if ("drip".equals(mname)) {
                    return teaType + ", temp:" + temp + ", " + cc + "cc.";
                }
                return null;
            }
        };
        DrinkDripperInterface ddi = (DrinkDripperInterface) Proxy
                .newProxyInstance(DrinkDripperInterface.class.getClassLoader(),
                        new Class[] { DrinkDripperInterface.class }, handler);
        System.out.println("current temperature:" + ddi.getTemperature());
        ddi.setTeaType("coffee");
        ddi.addWater(100);
        System.out.println("drip : [" + ddi.drip() + "]");
        ddi.setTeaType("green-tea", 60);
        ddi.addWater(200);
        System.out.println("drip : [" + ddi.drip() + "]");
        System.out.println("(END)");
    }
}
