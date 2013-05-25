package javasnack.testee;

public class AngryStaticUtilUser {
    int state;

    public AngryStaticUtilUser(int state) {
        this.state = state;
    }

    public int addSecretAndMe(int v) {
        return AngryStaticUtil.addSecretTo(v) + this.state;
    }

    public static int addDelegate(int v) {
        return AngryStaticUtil.addSecretTo(v);
    }
}
