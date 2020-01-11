package javasnack.stream.fpjbook;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class TestChapter8SamplesDemo {
    static final Map<String, BigDecimal> TICKER_PRICE;
    static final List<String> TICKERS;
    static {
        final Map<String, BigDecimal> src = new HashMap<>();
        src.put("AMD", BigDecimal.valueOf(100.0));
        src.put("HPQ", BigDecimal.valueOf(200.0));
        src.put("IBM", BigDecimal.valueOf(300.0));
        src.put("TXN", BigDecimal.valueOf(400.0));
        src.put("VMW", BigDecimal.valueOf(500.0));
        src.put("XRX", BigDecimal.valueOf(600.0));
        src.put("APPL", BigDecimal.valueOf(700.0));
        src.put("ADBE", BigDecimal.valueOf(800.0));
        src.put("AMZN", BigDecimal.valueOf(900.0));
        src.put("CRAY", BigDecimal.valueOf(100.0));
        src.put("CSCO", BigDecimal.valueOf(200.0));
        src.put("SNE", BigDecimal.valueOf(300.0));
        src.put("GOOG", BigDecimal.valueOf(400.0));
        src.put("INTC", BigDecimal.valueOf(500.0));
        src.put("INTU", BigDecimal.valueOf(600.0));
        src.put("MSFT", BigDecimal.valueOf(700.0));
        src.put("ORCL", BigDecimal.valueOf(800.0));
        src.put("TIBX", BigDecimal.valueOf(900.0));
        src.put("VRSN", BigDecimal.valueOf(100.0));
        src.put("YHOO", BigDecimal.valueOf(200.0));
        TICKER_PRICE = Collections.unmodifiableMap(src);
        TICKERS = Collections.unmodifiableList(new ArrayList<>(TICKER_PRICE.keySet()));
    }

    // chapter 8.1 : typical stream operation

    static BigDecimal getPrice1(final String ticket) {
        return TICKER_PRICE.getOrDefault(ticket, BigDecimal.ZERO);
    }

    @Test
    public void testTypicalStreamOperation() {
        final String greaterThan700 = TICKERS.stream()
                .filter(ticker -> getPrice1(ticker).compareTo(BigDecimal.valueOf(700)) > 0)
                .sorted()
                .collect(Collectors.joining(", "));
        assertThat(greaterThan700).isEqualTo("ADBE, AMZN, ORCL, TIBX");
    }

    // chapter 8.2 : map reduce example

    static class StockInfo {
        public final String ticker;
        public final BigDecimal price;

        public StockInfo(final String ticker, final BigDecimal price) {
            this.ticker = ticker;
            this.price = price;
        }

        @Override
        public String toString() {
            return String.format("ticker:%s price: %g", ticker, price);
        }
    }

    StockInfo getPrice2(final String ticker) {
        try {
            Thread.sleep(10); // wait a moment for chapter 8.3 demo
        } catch (InterruptedException ignore) {
        }
        return new StockInfo(ticker, getPrice1(ticker));
    }

    Predicate<StockInfo> isPriceLessThan(final int price) {
        return stockInfo -> stockInfo.price.compareTo(BigDecimal.valueOf(price)) < 0;
    }

    StockInfo pickHigh(final StockInfo stock1, final StockInfo stock2) {
        return stock1.price.compareTo(stock2.price) > 0 ? stock1 : stock2;
    }

    @Test
    public void testClassicStyle() {
        final List<StockInfo> stocks = new ArrayList<>();
        for (final String ticker : TICKERS) {
            stocks.add(getPrice2(ticker));
        }

        final List<StockInfo> stocksPriceUnder500 = new ArrayList<>();
        final Predicate<StockInfo> isPriceLessThan500 = isPriceLessThan(500);
        for (final StockInfo stock : stocks) {
            if (isPriceLessThan500.test(stock)) {
                stocksPriceUnder500.add(stock);
            }
        }
        StockInfo highPriced = new StockInfo("", BigDecimal.ZERO);
        for (final StockInfo stock : stocksPriceUnder500) {
            highPriced = pickHigh(highPriced, stock);
        }

        assertThat(highPriced.toString()).isEqualTo("ticker:TXN price: 400.000");
    }

    StockInfo findHighPriced(final Stream<String> tickers) {
        return tickers.map(this::getPrice2)
                .filter(isPriceLessThan(500))
                .reduce(this::pickHigh)
                .get();
    }

    @Test
    public void testStreamStyle() {
        final StockInfo highPriced = findHighPriced(TICKERS.stream());
        assertThat(highPriced.toString()).isEqualTo("ticker:TXN price: 400.000");
    }

    // chapter 8.3 : parallel stream example

    @Test
    public void testParallelStreamExample() {
        final long start1 = System.currentTimeMillis();
        final StockInfo highPriced1 = findHighPriced(TICKERS.stream());
        final long elapsed1 = System.currentTimeMillis() - start1;
        assertThat(highPriced1.toString()).isEqualTo("ticker:TXN price: 400.000");

        final long start2 = System.currentTimeMillis();
        final StockInfo highPriced2 = findHighPriced(TICKERS.parallelStream());
        final long elapsed2 = System.currentTimeMillis() - start2;
        assertThat(highPriced2.toString()).isEqualTo("ticker:TXN price: 400.000");

        assertThat(elapsed2).isLessThan(elapsed1);
    }
}
