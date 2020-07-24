package javasnack.ojcp.se8silver.test08sub;

public class AccessScopeDemo {
    public String mPublic() {
        return "public scope method";
    }

    // protected = package + 継承先クラスからのアクセス許可
    protected String mProtected() {
        return "protected scope method";
    }

    // package という修飾子は無い : compile error
    //package String mPackage() {
    String mPackage() {
        return "package scope method";
    }

    private String mPrivate() {
        return "private scope method";
    }

    void m1() {
        // 同じ .java 内の non-public class を参照できる。
        AccessScopeDemoNonScoped o1 = new AccessScopeDemoNonScoped();
    }
}

class AccessScopeDemoNonScoped {
    public String mPublic2() {
        return "public scope method2";
    }

    // protected = package + 継承先クラスからのアクセス許可
    protected String mProtected2() {
        return "protected scope method2";
    }

    // package という修飾子は無い : compile error
    //package String mPackage() {
    String mPackage2() {
        return "package scope method2";
    }

    private String mPrivate2() {
        return "private scope method2";
    }

    void m1() {
        // 同じ .java 内の public class を参照できる。
        AccessScopeDemo o1 = new AccessScopeDemo();
    }
}

// class 宣言に private を付けると compile error
//private AccessScopeDemoPrivate {
//}

// class 宣言に protected を付けると compile error
//protected AccessScopeDemoProtected {
//}