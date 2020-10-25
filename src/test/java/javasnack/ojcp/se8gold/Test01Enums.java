package javasnack.ojcp.se8gold;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Test01Enums {
    static enum Card1 {
        SPADES,
        CLUBS,
        DIAMONDS,
        HEARTS
        // 最後の宣言ではセミコロン不要。あってもOK.
    }

    static String colorOfCard1(final Card1 card) {
        // switch で enum を参照するときは、enum名. は省略可能。
        switch (card) {
        case SPADES:
        case CLUBS:
            return "black";
        case DIAMONDS:
        case HEARTS:
            return "red";
        default:
            return "";
        }
    }

    @Test
    public void testEnumDemo1() {
        Card1[] cards = Card1.values();
        assertThat(cards[0]).isEqualTo(Card1.SPADES);
        assertThat(cards[1]).isEqualTo(Card1.CLUBS);
        assertThat(cards[2]).isEqualTo(Card1.DIAMONDS);
        assertThat(cards[3]).isEqualTo(Card1.HEARTS);

        assertThat(Card1.valueOf("SPADES")).isEqualTo(Card1.SPADES);
        assertThat(Card1.valueOf("CLUBS")).isEqualTo(Card1.CLUBS);
        assertThat(Card1.valueOf("DIAMONDS")).isEqualTo(Card1.DIAMONDS);
        assertThat(Card1.valueOf("HEARTS")).isEqualTo(Card1.HEARTS);

        assertThat(Card1.SPADES.name()).isEqualTo("SPADES");
        assertThat(Card1.CLUBS.name()).isEqualTo("CLUBS");
        assertThat(Card1.DIAMONDS.name()).isEqualTo("DIAMONDS");
        assertThat(Card1.HEARTS.name()).isEqualTo("HEARTS");

        assertThat(Card1.SPADES.toString()).isEqualTo("SPADES");
        assertThat(Card1.CLUBS.toString()).isEqualTo("CLUBS");
        assertThat(Card1.DIAMONDS.toString()).isEqualTo("DIAMONDS");
        assertThat(Card1.HEARTS.toString()).isEqualTo("HEARTS");

        // zero start
        assertThat(Card1.SPADES.ordinal()).isEqualTo(0);
        assertThat(Card1.CLUBS.ordinal()).isEqualTo(1);
        assertThat(Card1.DIAMONDS.ordinal()).isEqualTo(2);
        assertThat(Card1.HEARTS.ordinal()).isEqualTo(3);

        assertThat(colorOfCard1(Card1.SPADES)).isEqualTo("black");
        assertThat(colorOfCard1(Card1.CLUBS)).isEqualTo("black");
        assertThat(colorOfCard1(Card1.DIAMONDS)).isEqualTo("red");
        assertThat(colorOfCard1(Card1.HEARTS)).isEqualTo("red");

        // Comparable interface を実装していて、定数の宣言順で比較される。
        assertThat(Card1.CLUBS.compareTo(Card1.SPADES)).isEqualTo(1);
        assertThat(Card1.CLUBS.compareTo(Card1.CLUBS)).isEqualTo(0);
        assertThat(Card1.CLUBS.compareTo(Card1.DIAMONDS)).isEqualTo(-1);
        assertThat(Card1.CLUBS.compareTo(Card1.HEARTS)).isEqualTo(-2);
    }

    static enum Card2 {
        SPADES("black"),
        CLUBS("black"),
        DIAMONDS("red"),
        HEARTS("red"),
        TRANSPARENT;
        // 列挙型定数以外が続く場合はセミコロン必須(セミコロン無いと compile error)

        // instance field/constructor/instance method を定義可能
        private final String color;

        // enum でのconstructorは private か未指定のみ許可される。 public/protected は compile error
        //public Card2(final String color) {
        //protected Card2(final String color) {
        private Card2(final String color) {
            this.color = color;
        }

        Card2() {
            this.color = "transparent";
        }

        String getColor() {
            return this.color;
        }
    }

    // enum は実質finalのため継承できない : compile error
    //static enum Card2b extends Card2 {
    //}

    @Test
    public void testEnumDemo2() {
        assertThat(Card2.SPADES.getColor()).isEqualTo("black");
        assertThat(Card2.CLUBS.getColor()).isEqualTo("black");
        assertThat(Card2.DIAMONDS.getColor()).isEqualTo("red");
        assertThat(Card2.HEARTS.getColor()).isEqualTo("red");
        assertThat(Card2.TRANSPARENT.getColor()).isEqualTo("transparent");
    }

    interface SomeInterface1 {
        String returnSomeString1();
    }

    interface SomeInterface2 {
        String returnSomeString2();
    }

    // interface と abstract method を enum で組み合わせるデモ
    static enum AbstractEnumDemo implements SomeInterface1, SomeInterface2 {
        DEMO1(10) {
            // ここでの interface / abstract method の実装からは this.id は参照できない(compile error)
            @Override
            public String returnSomeString1() {
                return "hello";
            }

            @Override
            public String getSomeString() {
                return "HELLO";
            }
        },
        DEMO2(20) {
            @Override
            public String returnSomeString1() {
                return "world";
            }

            @Override
            public String getSomeString() {
                return "WORLD";
            }
        };

        private final int id;

        AbstractEnumDemo(final int id) {
            this.id = id;
        }

        @Override
        public String returnSomeString2() {
            // ここからなら this.id を参照可能
            return "id=" + this.id;
        }

        @Override
        public String toString() {
            return this.getSomeString() + ":" + this.returnSomeString1() + ":" + this.id;
        }

        abstract String getSomeString();
    }

    @Test
    public void testEnumDemo3() {
        assertThat(AbstractEnumDemo.DEMO1.returnSomeString1()).isEqualTo("hello");
        assertThat(AbstractEnumDemo.DEMO1.returnSomeString2()).isEqualTo("id=10");
        assertThat(AbstractEnumDemo.DEMO1.getSomeString()).isEqualTo("HELLO");
        assertThat(AbstractEnumDemo.DEMO1.toString()).isEqualTo("HELLO:hello:10");
        assertThat(AbstractEnumDemo.DEMO2.returnSomeString1()).isEqualTo("world");
        assertThat(AbstractEnumDemo.DEMO2.returnSomeString2()).isEqualTo("id=20");
        assertThat(AbstractEnumDemo.DEMO2.getSomeString()).isEqualTo("WORLD");
        assertThat(AbstractEnumDemo.DEMO2.toString()).isEqualTo("WORLD:world:20");
    }
}
