package javasnack.reflection;

public class SomeConcreteIndependentClass {
    byte packagedByteField = 1;
    @SuppressWarnings("unused")
    private short privateShortField = 2;
    protected int protectedIntField = 3;
    public long publicLongField = 4L;
    volatile boolean volatileBooleanField = true;
    final String finalStringField = "abc";
    public static final String PUBLIC_FINAL_STR = "ABC";
}
