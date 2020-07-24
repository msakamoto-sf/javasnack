package javasnack.ojcp.se8silver.test08sub;

public class AccessScopeDemoAccessDemo extends AccessScopeDemo {
    public void callSuperClassMethods() {
        StringBuilder sb = new StringBuilder();
        sb.append(mPublic());
        sb.append(mProtected());
        // 同一パッケージからなので呼べる。
        sb.append(mPackage());
        // 継承先からは
        //sb.append(mPrivate());
    }
}

class AccessScopeDemo2Sub {
    public void callOtherClassMethods() {
        AccessScopeDemo o1 = new AccessScopeDemo();
        o1.mPublic();
        // protected は package スコープも含むので、
        // 同一パッケージから呼べる。
        o1.mProtected();
        // 同一パッケージからなので呼べる。
        o1.mPackage();
    }
}
