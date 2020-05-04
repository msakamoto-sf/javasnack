package javasnack.classgraph;

@SomeMark2
@SomeType2
public class ConcreteClass2 extends SomeAbstractClass2 implements SomeInterface2 {

    @SomeMark2
    public final int iv2a;

    @SomeField2
    public final int iv2b;

    @SomeMark2
    public ConcreteClass2(final int iv2a, final int iv2b) {
        this.iv2a = iv2a;
        this.iv2b = iv2b;
    }

    @SomeConstructor2
    public ConcreteClass2() {
        this(100, 200);
    }

    @SomeMark2
    public int sum2() {
        return this.iv2a + this.iv2b;
    }

    @SomeMethod2
    public int max2() {
        return (this.iv2a > this.iv2b) ? this.iv2a : this.iv2b;
    }
}
