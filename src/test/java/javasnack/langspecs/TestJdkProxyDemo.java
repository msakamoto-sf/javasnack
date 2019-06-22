package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;

interface DrinkDripperInterface {

    public void setTeaType(String teaType);

    public void setTeaType(String teaType, int temp);

    public int getTemperature();

    public int addWater(int cc);

    public String drip();
}

public class TestJdkProxyDemo {

    @Test
    public void testJdkProxyDemo() {
        final InvocationHandler handler = new InvocationHandler() {
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
                    return Integer.valueOf(temp);
                } else if ("addWater".equals(mname)) {
                    cc += ((Integer) args[0]).intValue();
                    return Integer.valueOf(cc);
                } else if ("drip".equals(mname)) {
                    return teaType + ", temp:" + temp + ", " + cc + "cc.";
                }
                return null;
            }
        };
        final DrinkDripperInterface ddi = (DrinkDripperInterface) Proxy.newProxyInstance(
                DrinkDripperInterface.class.getClassLoader(),
                new Class[] { DrinkDripperInterface.class },
                handler);
        assertThat(ddi.getTemperature()).isEqualTo(0);
        ddi.setTeaType("coffee");
        ddi.addWater(100);
        assertThat(ddi.drip()).isEqualTo("coffee, temp:0, 100cc.");
        ddi.setTeaType("green-tea", 60);
        ddi.addWater(200);
        assertThat(ddi.drip()).isEqualTo("green-tea, temp:60, 300cc.");
    }
}
