package javasnack.reflection;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestClassMetaInfo {

    public static class TypicalClassMetaInfo {
        final String name;
        final String canonicalName;
        final String simpleName;
        final String typeName;
        final String packageName;
        final String modifiers;
        final boolean isPrimitive;
        final boolean isInterface;
        final boolean isArray;

        public TypicalClassMetaInfo(String name, String canonicalName, String simpleName, String typeName,
                String packageName, String modifiers, boolean isPrimitive, boolean isInterface, boolean isArray) {
            super();
            this.name = name;
            this.canonicalName = canonicalName;
            this.simpleName = simpleName;
            this.typeName = typeName;
            this.packageName = packageName;
            this.modifiers = modifiers;
            this.isPrimitive = isPrimitive;
            this.isInterface = isInterface;
            this.isArray = isArray;
        }
    }

    public static final class publicStaticFinalClass {
    };

    public static abstract class publicStaticAbstractClass {
    };

    public static interface publicStaticInterface {
    };

    private static final class privateStaticFinalClass {
    };

    protected static final class protectedStaticFinalClass {
    };

    static class packagedStaticClass {
    };

    @DataProvider(name = "dpTypicalClassMetaInfo")
    public Object[][] dpTypicalClassMetaInfo() {
        final String pkg = this.getClass().getPackage().getName();
        final String staticPrefix = this.getClass().getName();
        final List<String> stringList = new ArrayList<>();
        return new Object[][] {
                {
        // @formatter:off
                    Object.class,
                    new TypicalClassMetaInfo(
                            "java.lang.Object",
                            "java.lang.Object",
                            "Object",
                            "java.lang.Object",
                            "java.lang", "public",
                            false, false, false),
                },{
                    String.class,
                    new TypicalClassMetaInfo(
                            "java.lang.String",
                            "java.lang.String",
                            "String",
                            "java.lang.String",
                            "java.lang", "public final",
                            false, false, false),
                },{
                    Integer.class,
                    new TypicalClassMetaInfo(
                            "java.lang.Integer",
                            "java.lang.Integer",
                            "Integer",
                            "java.lang.Integer",
                            "java.lang", "public final",
                            false, false, false),
                },{
                    byte[].class,
                    new TypicalClassMetaInfo(
                            "[B",
                            "byte[]",
                            "byte[]",
                            "byte[]",
                            "<null>", "public abstract final",
                            false, false, true),
                },{
                    String[].class,
                    new TypicalClassMetaInfo(
                            "[Ljava.lang.String;",
                            "java.lang.String[]",
                            "String[]",
                            "java.lang.String[]",
                            "<null>", "public abstract final",
                            false, false, true),
                },{
                    List.class,
                    new TypicalClassMetaInfo(
                            "java.util.List",
                            "java.util.List",
                            "List",
                            "java.util.List",
                            "java.util", "public abstract interface",
                            false, true, false),
                },{
                    stringList.getClass(),
                    new TypicalClassMetaInfo(
                            "java.util.ArrayList",
                            "java.util.ArrayList",
                            "ArrayList",
                            "java.util.ArrayList",
                            "java.util", "public",
                            false, false, false),
                },{
                publicStaticFinalClass.class,
                new TypicalClassMetaInfo(
                        staticPrefix + "$publicStaticFinalClass",
                        staticPrefix + ".publicStaticFinalClass",
                        "publicStaticFinalClass",
                        staticPrefix + "$publicStaticFinalClass",
                        pkg, "public static final",
                        false, false, false),
            },{
                publicStaticAbstractClass.class,
                new TypicalClassMetaInfo(
                        staticPrefix + "$publicStaticAbstractClass",
                        staticPrefix + ".publicStaticAbstractClass",
                        "publicStaticAbstractClass",
                        staticPrefix + "$publicStaticAbstractClass",
                        pkg, "public abstract static",
                        false, false, false),
            },{
                publicStaticInterface.class,
                new TypicalClassMetaInfo(
                        staticPrefix + "$publicStaticInterface",
                        staticPrefix + ".publicStaticInterface",
                        "publicStaticInterface",
                        staticPrefix + "$publicStaticInterface",
                        pkg, "public abstract static interface",
                        false, true, false),
            },{
                privateStaticFinalClass.class,
                new TypicalClassMetaInfo(
                        staticPrefix + "$privateStaticFinalClass",
                        staticPrefix + ".privateStaticFinalClass",
                        "privateStaticFinalClass",
                        staticPrefix + "$privateStaticFinalClass",
                        pkg, "private static final",
                        false, false, false),
            },{
                protectedStaticFinalClass.class,
                new TypicalClassMetaInfo(
                        staticPrefix + "$protectedStaticFinalClass",
                        staticPrefix + ".protectedStaticFinalClass",
                        "protectedStaticFinalClass",
                        staticPrefix + "$protectedStaticFinalClass",
                        pkg, "protected static final",
                        false, false, false),
            },{
                packagedStaticClass.class,
                new TypicalClassMetaInfo(
                        staticPrefix + "$packagedStaticClass",
                        staticPrefix + ".packagedStaticClass",
                        "packagedStaticClass",
                        staticPrefix + "$packagedStaticClass",
                        pkg, "static",
                        false, false, false),
        // @formatter:on
                } };
    }

    @Test(dataProvider = "dpTypicalClassMetaInfo")
    public void testTypicalClassMetaInfo(Class<?> c, TypicalClassMetaInfo expected) {
        TypicalClassMetaInfo actual = new TypicalClassMetaInfo(
            // @formatter:off
            c.getName(),
            c.getCanonicalName(),
            c.getSimpleName(),
            c.getTypeName(),
            (Objects.isNull(c.getPackage()) ? "<null>" : c.getPackage().getName()),
            Modifier.toString(c.getModifiers()),
            c.isPrimitive(),
            c.isInterface(),
            c.isArray()
            // @formatter:on
        );
        Assert.assertEquals(actual.name, expected.name);
        Assert.assertEquals(actual.canonicalName, expected.canonicalName);
        Assert.assertEquals(actual.simpleName, expected.simpleName);
        Assert.assertEquals(actual.typeName, expected.typeName);
        Assert.assertEquals(actual.packageName, expected.packageName);
        Assert.assertEquals(actual.modifiers, expected.modifiers);
        Assert.assertEquals(actual.isPrimitive, expected.isPrimitive);
        Assert.assertEquals(actual.isInterface, expected.isInterface);
        Assert.assertEquals(actual.isArray, expected.isArray);
    }

    @Test
    public void testTypeParameters() {
        Object o0 = new Object();
        Class<?> c0 = o0.getClass();
        TypeVariable<? extends GenericDeclaration> tv[] = c0.getTypeParameters();
        Assert.assertEquals(tv.length, 0);
        c0 = List.class;
        tv = c0.getTypeParameters();
        Assert.assertEquals(tv.length, 1);
        Assert.assertEquals(tv[0].getName(), "E");
        c0 = Map.class;
        tv = c0.getTypeParameters();
        Assert.assertEquals(tv.length, 2);
        Assert.assertEquals(tv[0].getName(), "K");
        Assert.assertEquals(tv[1].getName(), "V");
    }

    @Test
    public void testImplementedInterfaces() {
        Object o0 = new Object();
        Class<?> c0 = o0.getClass();
        Class<?> ifs[] = c0.getInterfaces();
        Type gifs[] = c0.getGenericInterfaces();
        Assert.assertEquals(ifs.length, 0);
        Assert.assertEquals(gifs.length, 0);

        c0 = ArrayList.class;
        ifs = c0.getInterfaces();
        gifs = c0.getGenericInterfaces();
        Assert.assertEquals(ifs.length, 4);
        Assert.assertEquals(ifs[0].getSimpleName(), "List");
        Assert.assertEquals(ifs[1].getSimpleName(), "RandomAccess");
        Assert.assertEquals(ifs[2].getSimpleName(), "Cloneable");
        Assert.assertEquals(ifs[3].getSimpleName(), "Serializable");
        Assert.assertEquals(gifs.length, 4);
        Assert.assertEquals(gifs[0].getTypeName(), "java.util.List<E>");
        Assert.assertEquals(gifs[1].getTypeName(), "java.util.RandomAccess");
        Assert.assertEquals(gifs[2].getTypeName(), "java.lang.Cloneable");
        Assert.assertEquals(gifs[3].getTypeName(), "java.io.Serializable");

        c0 = LinkedHashMap.class;
        ifs = c0.getInterfaces();
        gifs = c0.getGenericInterfaces();
        Assert.assertEquals(ifs.length, 1);
        Assert.assertEquals(ifs[0].getSimpleName(), "Map");
        Assert.assertEquals(gifs.length, 1);
        Assert.assertEquals(gifs[0].getTypeName(), "java.util.Map<K, V>");

        byte[] ba = new byte[] {};
        c0 = ba.getClass();
        ifs = c0.getInterfaces();
        gifs = c0.getGenericInterfaces();
        Assert.assertEquals(ifs.length, 2);
        Assert.assertEquals(ifs[0].getSimpleName(), "Cloneable");
        Assert.assertEquals(ifs[1].getSimpleName(), "Serializable");
        Assert.assertEquals(gifs.length, 2);
        Assert.assertEquals(gifs[0].getTypeName(), "java.lang.Cloneable");
        Assert.assertEquals(gifs[1].getTypeName(), "java.io.Serializable");

        String[] sa = new String[] {};
        c0 = sa.getClass();
        ifs = c0.getInterfaces();
        gifs = c0.getGenericInterfaces();
        Assert.assertEquals(ifs.length, 2);
        Assert.assertEquals(ifs[0].getSimpleName(), "Cloneable");
        Assert.assertEquals(ifs[1].getSimpleName(), "Serializable");
        Assert.assertEquals(gifs.length, 2);
        Assert.assertEquals(gifs[0].getTypeName(), "java.lang.Cloneable");
        Assert.assertEquals(gifs[1].getTypeName(), "java.io.Serializable");

    }

    public static interface BaseInterface0 {
    };

    public static interface ExtendInterface1 extends BaseInterface0 {
    };

    public static interface ExtendInterface2 extends ExtendInterface1 {
    };

    public static class Base0 implements BaseInterface0 {
    };

    public static class Extend1 extends Base0 implements ExtendInterface1 {
    };

    public static class Extend2 extends Extend1 implements ExtendInterface2 {
    };

    @Test
    public void testSuperclass() {
        final String staticPrefix = this.getClass().getName();
        Class<?> c0 = Base0.class;
        Assert.assertEquals(c0.getSuperclass().getName(), "java.lang.Object");
        Assert.assertEquals(c0.getGenericSuperclass().getTypeName(), "java.lang.Object");
        c0 = Extend1.class;
        Assert.assertEquals(c0.getSuperclass().getName(), staticPrefix + "$Base0");
        Assert.assertEquals(c0.getGenericSuperclass().getTypeName(), staticPrefix + "$Base0");
        c0 = Extend2.class;
        Assert.assertEquals(c0.getSuperclass().getName(), staticPrefix + "$Extend1");
        Assert.assertEquals(c0.getGenericSuperclass().getTypeName(), staticPrefix + "$Extend1");
        c0 = ExtendInterface1.class;
        Assert.assertNull(c0.getSuperclass());
        Assert.assertNull(c0.getGenericSuperclass());
        c0 = ExtendInterface2.class;
        Assert.assertNull(c0.getSuperclass());
        Assert.assertNull(c0.getGenericSuperclass());

        c0 = byte[].class;
        Assert.assertEquals(c0.getSuperclass().getName(), "java.lang.Object");
        Assert.assertEquals(c0.getGenericSuperclass().getTypeName(), "java.lang.Object");
        c0 = String[].class;
        Assert.assertEquals(c0.getSuperclass().getName(), "java.lang.Object");
        Assert.assertEquals(c0.getGenericSuperclass().getTypeName(), "java.lang.Object");

        c0 = LinkedHashMap.class;
        Assert.assertEquals(c0.getSuperclass().getName(), "java.util.HashMap");
        Assert.assertEquals(c0.getGenericSuperclass().getTypeName(), "java.util.HashMap<K, V>");
    }

    @Test
    public void testAssignableFrom() {
        Base0 o0 = new Base0();
        Assert.assertTrue(o0 instanceof Base0);
        Assert.assertFalse(o0 instanceof Extend1);
        Assert.assertFalse(o0 instanceof Extend2);
        Assert.assertTrue(o0 instanceof BaseInterface0);
        Assert.assertFalse(o0 instanceof ExtendInterface1);
        Assert.assertFalse(o0 instanceof ExtendInterface2);

        Extend2 o1 = new Extend2();
        Assert.assertTrue(o1 instanceof Base0);
        Assert.assertTrue(o1 instanceof Extend1);
        Assert.assertTrue(o1 instanceof Extend2);
        Assert.assertTrue(o1 instanceof BaseInterface0);
        Assert.assertTrue(o1 instanceof ExtendInterface1);
        Assert.assertTrue(o1 instanceof ExtendInterface2);

        Assert.assertFalse(Base0.class.isAssignableFrom(BaseInterface0.class));
        Assert.assertTrue(BaseInterface0.class.isAssignableFrom(Base0.class));
        Assert.assertTrue(BaseInterface0.class.isAssignableFrom(Extend1.class));
        Assert.assertTrue(BaseInterface0.class.isAssignableFrom(Extend2.class));
        Assert.assertTrue(BaseInterface0.class.isAssignableFrom(BaseInterface0.class));
        Assert.assertTrue(BaseInterface0.class.isAssignableFrom(ExtendInterface1.class));
        Assert.assertTrue(BaseInterface0.class.isAssignableFrom(ExtendInterface2.class));
    }
}
