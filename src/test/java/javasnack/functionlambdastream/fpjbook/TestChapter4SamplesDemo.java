package javasnack.functionlambdastream.fpjbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

public class TestChapter4SamplesDemo {

    // chapter 4.1 : demonstration of Strategy Design Pattern using Function

    static class Asset {

        public enum AssetType {
            BOND, STOCK
        }

        private final AssetType type;
        private final int value;

        public Asset(final AssetType assetType, final int assetValue) {
            this.type = assetType;
            this.value = assetValue;
        }

        public AssetType getType() {
            return this.type;
        }

        public int getValue() {
            return this.value;
        }
    }

    static int totalAssetValues(final List<Asset> assets) {
        return assets.stream().mapToInt(Asset::getValue).sum();
    }

    static int totalBondValues(final List<Asset> assets) {
        return assets.stream()
                .filter(asset -> asset.getType() == Asset.AssetType.BOND)
                .mapToInt(Asset::getValue).sum();
    }

    // improved pattern using strategy design pattern
    static int totalAssetValues(final List<Asset> assets, final Predicate<Asset> assetSelector) {
        return assets.stream()
                .filter(assetSelector)
                .mapToInt(Asset::getValue).sum();
    }

    @Test
    public void refactorDemoWithStrategyDesignPattern() {
        final List<Asset> assets = Arrays.asList(
                new Asset(Asset.AssetType.BOND, 1000),
                new Asset(Asset.AssetType.BOND, 2000),
                new Asset(Asset.AssetType.STOCK, 3000),
                new Asset(Asset.AssetType.STOCK, 4000));

        // classic
        assertThat(totalAssetValues(assets)).isEqualTo(10000);
        assertThat(totalBondValues(assets)).isEqualTo(3000);

        // improved
        assertThat(totalAssetValues(assets, asset -> true)).isEqualTo(10000);
        assertThat(totalAssetValues(assets, asset -> asset.getType() == Asset.AssetType.BOND)).isEqualTo(3000);
        assertThat(totalAssetValues(assets, asset -> asset.getType() == Asset.AssetType.STOCK)).isEqualTo(7000);
    }

    // chapter 4.2 : demonstration of Delegation and Test Double (Mocking) using Function

    static class CalculateNAV {
        private final Function<String, BigDecimal> priceFinder;

        public CalculateNAV(final Function<String, BigDecimal> aPriceFinder) {
            this.priceFinder = aPriceFinder;
        }

        public BigDecimal computeStockWorth(final String ticker, final int shares) {
            return priceFinder.apply(ticker).multiply(BigDecimal.valueOf(shares));
        }
    }

    @Test
    public void computeStockWorth() {
        final CalculateNAV mockNAV = new CalculateNAV(ticker -> {
            switch (ticker) {
            case "AAAA":
                return new BigDecimal("6.01");
            case "BBBB":
                return new BigDecimal("7.14");
            default:
                return BigDecimal.ZERO;
            }
        });
        assertThat(mockNAV.computeStockWorth("AAAA", 1000)).isCloseTo(new BigDecimal("6010.00"),
                Offset.offset(new BigDecimal("0.001")));
        assertThat(mockNAV.computeStockWorth("BBBB", 100)).isCloseTo(new BigDecimal("714.00"),
                Offset.offset(new BigDecimal("0.001")));
    }

    // chapter 4.3 : demonstration of Function.compose as Decorator design pattern

    @SuppressWarnings("unchecked")
    static class Camera {
        private Function<Color, Color> filter;

        public void setFilters(final Function<Color, Color>... filters) {
            this.filter = Stream.of(filters)
                    .reduce((filter, next) -> filter.compose(next))
                    .orElseGet(Function::identity);
        }

        public Camera() {
            setFilters();
        }

        public Color capture(final Color inputColor) {
            final Color processedColor = filter.apply(inputColor);
            return processedColor;
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void decoratorDesignPatternDemo() {
        final Camera cam1 = new Camera();
        final Color c1 = cam1.capture(new Color(200, 100, 50));
        assertThat(c1.getRed()).isEqualTo(200);
        assertThat(c1.getGreen()).isEqualTo(100);
        assertThat(c1.getBlue()).isEqualTo(50);

        cam1.setFilters(Color::brighter);
        final Color c2 = cam1.capture(new Color(200, 100, 50));
        assertThat(c2.getRed()).isEqualTo(255);
        assertThat(c2.getGreen()).isEqualTo(142);
        assertThat(c2.getBlue()).isEqualTo(71);

        cam1.setFilters(Color::darker);
        final Color c3 = cam1.capture(new Color(200, 100, 50));
        assertThat(c3.getRed()).isEqualTo(140);
        assertThat(c3.getGreen()).isEqualTo(70);
        assertThat(c3.getBlue()).isEqualTo(35);

        cam1.setFilters(Color::brighter, Color::darker);
        final Color c4 = cam1.capture(new Color(200, 100, 50));
        assertThat(c4.getRed()).isEqualTo(200);
        assertThat(c4.getGreen()).isEqualTo(100);
        assertThat(c4.getBlue()).isEqualTo(50);
    }

    // chapter 4.4 : default method examples

    interface Fly {
        default String takeOff() {
            return "Fly::takeOff";
        }

        default String land() {
            return "Fly::land";
        }

        default String turn() {
            return "Fly::turn";
        }

        default String cruise() {
            return "Fly::cruise";
        }
    }

    interface FastFly extends Fly {
        default String takeOff() {
            return "FastFly::takeOff";
        }
    }

    interface Sail {
        default String cruise() {
            return "Sail::cruise";
        }

        default String turn() {
            return "Sail::turn";
        }
    }

    static class Vehicle {
        public String turn() {
            return "Vehicle::turn";
        }
    }

    static class SeaPlane extends Vehicle implements FastFly, Sail {
        public int altitude = 0;

        @Override
        public String cruise() {
            return "SeaPlane::cruise currently cruise like: " + ((altitude > 0) ? FastFly.super.cruise()
                    : Sail.super.cruise());
        }
    }

    @Test
    public void testSeaPlane() {
        final SeaPlane sp = new SeaPlane();
        assertThat(sp.takeOff()).isEqualTo("FastFly::takeOff");
        assertThat(sp.turn()).isEqualTo("Vehicle::turn");
        assertThat(sp.cruise()).isEqualTo("SeaPlane::cruise currently cruise like: Sail::cruise");
        assertThat(sp.land()).isEqualTo("Fly::land");

        sp.altitude = 1;
        assertThat(sp.cruise()).isEqualTo("SeaPlane::cruise currently cruise like: Fly::cruise");
    }

    // chapter 4.5 : demonstration of fluent interface and loan pattern

    // old style
    static class Mailer {
        public void from(final String address) {
            // ...
        }

        public void to(final String address) {
            // ...
        }

        public void subject(final String line) {
            // ...
        }

        public void body(final String message) {
            // ...
        }

        public void send() {
            System.out.println("old style Mailer::send() called.");
        }
    }

    @Test
    public void testOldStyleMailer() {
        final Mailer m0 = new Mailer();
        m0.from("foo@example.com");
        m0.to("bar@example.com");
        m0.subject("hello");
        m0.body("world");
        m0.send();
    }

    // builder style (fluent interface)
    static class MailBuilder {
        public MailBuilder from(final String address) {
            // ...
            return this;
        }

        public MailBuilder to(final String address) {
            // ...
            return this;
        }

        public MailBuilder subject(final String line) {
            // ...
            return this;
        }

        public MailBuilder body(final String message) {
            // ...
            return this;
        }

        public void send() {
            System.out.println("old style MailBuilder::send() called.");
        }
    }

    @Test
    public void testBuilderStyleMailer() {
        new MailBuilder().from("foo@example.com")
                .to("bar@example.com")
                .subject("hello")
                .body("world")
                .send();
    }

    // fluent style with loan pattern
    static class FluentMailer {
        private String from = "";
        private String to = "";
        private String subject = "";
        private String body = "";

        private FluentMailer() {
        }

        public FluentMailer from(final String address) {
            from = address;
            return this;
        }

        public FluentMailer to(final String address) {
            to = address;
            return this;
        }

        public FluentMailer subject(final String line) {
            subject = line;
            return this;
        }

        public FluentMailer body(final String message) {
            body = message;
            return this;
        }

        public static String send(final Consumer<FluentMailer> setup) {
            final FluentMailer mailer = new FluentMailer();
            setup.accept(mailer);
            return "from: " + mailer.from + ", to: " + mailer.to + ", subject: " + mailer.subject + ", body: "
                    + mailer.body;
        }
    }

    @Test
    public void testFluentMailer() {
        assertThat(
                FluentMailer.send(mailer -> mailer.from("foo@example.com")
                        .to("bar@example.com")
                        .subject("hello")
                        .body("world")))
                                .isEqualTo("from: foo@example.com, to: bar@example.com, "
                                        + "subject: hello, body: world");
    }

}
