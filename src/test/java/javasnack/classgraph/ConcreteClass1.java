package javasnack.classgraph;

@SomeMark1
@SomeType1
public class ConcreteClass1 extends SomeAbstractClass1 implements SomeInterface1 {

    @SomeMark1
    public final int iv1a;

    @SomeField1
    public final int iv1b;

    @SomeMark1
    public ConcreteClass1(final int iv1a, final int iv1b) {
        this.iv1a = iv1a;
        this.iv1b = iv1b;
    }

    @SomeConstructor1
    public ConcreteClass1() {
        this(100, 200);
    }

    @SomeMark1
    public int sum1() {
        return this.iv1a + this.iv1b;
    }

    @SomeMethod1
    public int max1() {
        return (this.iv1a > this.iv1b) ? this.iv1a : this.iv1b;
    }
}
