package javasnack.snacks.json.pojo;

public class EncodePojoChild {
    public final String strField = "HereAmI.";
    private final int age;

    public EncodePojoChild(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public boolean isTrueGetter() {
        return true;
    }

    public boolean isFalseGetter() {
        return false;
    }

    public boolean hasTrueGetter() {
        return true;
    }

}
