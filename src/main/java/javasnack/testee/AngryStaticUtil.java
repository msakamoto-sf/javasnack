package javasnack.testee;

public class AngryStaticUtil {
    protected static int secret = 5;

    public static int addSecretTo(int v) {
        return v + secret;
    }
}
