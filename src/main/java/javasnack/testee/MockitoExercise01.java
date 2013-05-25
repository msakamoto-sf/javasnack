package javasnack.testee;

import java.util.List;

public class MockitoExercise01 {
    List<String> m;

    public MockitoExercise01(List<String> mock) {
        this.m = mock;
    }

    public void doSomething() {
        this.m.add("Bonjour");
        this.m.clear();
    }

    public String getIndex2() {
        return this.m.get(2);
    }

}
