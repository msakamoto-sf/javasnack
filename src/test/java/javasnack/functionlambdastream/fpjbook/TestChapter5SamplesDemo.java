package javasnack.functionlambdastream.fpjbook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TestChapter5SamplesDemo {

    // chapter 5.1 : auto resource management

    static class FileWriterARM implements AutoCloseable {
        private final FileWriter writer;
        private final AtomicBoolean closed;

        public FileWriterARM(final String fileName, final AtomicBoolean closedFlag) throws IOException {
            this.writer = new FileWriter(fileName);
            this.closed = closedFlag;
        }

        public void writeStuff(final String message) throws IOException {
            this.writer.write(message);
        }

        @Override
        public void close() throws IOException {
            this.writer.close();
            this.closed.set(true);
        }
    }

    @Test
    public void testAutoCloseableFileWriter(@TempDir final Path tempDir) throws IOException {
        final Path tempFile = Path.of(tempDir.toString(), "aaa.txt");
        final AtomicBoolean closed = new AtomicBoolean(false);
        try (final FileWriterARM writerARM = new FileWriterARM(tempFile.toString(), closed)) {
            writerARM.writeStuff("hello");
        }
        final String data = Files.readString(tempFile);
        assertThat(data).isEqualTo("hello");
        assertThat(closed.get()).isTrue();
    }

    // chapter 5.2 : execute around method

    @FunctionalInterface
    static interface MyThrowableConsumer<T, X extends Throwable> {
        void accept(T instance) throws X;
    }

    static class FileWriterEAM {
        private final FileWriter writer;

        private FileWriterEAM(final String fileName) throws IOException {
            this.writer = new FileWriter(fileName);
        }

        public void writeStuff(final String message) throws IOException {
            this.writer.write(message);
        }

        public void close() throws IOException {
            this.writer.close();
        }

        public static void use(final String fileName, final MyThrowableConsumer<FileWriterEAM, IOException> block)
                throws IOException {
            final FileWriterEAM writerEAM = new FileWriterEAM(fileName);
            try {
                block.accept(writerEAM);
            } finally {
                writerEAM.close();
            }
        }
    }

    @Test
    public void testFileWriterEAM(@TempDir final Path tempDir) throws IOException {
        final Path tempFile = Path.of(tempDir.toString(), "aaa.txt");
        FileWriterEAM.use(tempFile.toString(), eam -> {
            eam.writeStuff("hello");
            eam.writeStuff("world");
        });
        final String data = Files.readString(tempFile);
        assertThat(data).isEqualTo("helloworld");
    }

    // chapter 5.3 : execute around method (Lock operation)

    static void runLocked(final Lock lock, final Runnable block) {
        lock.lock();
        try {
            block.run();
        } finally {
            lock.unlock();
        }
    }

    static class Counter {
        int cnt = 0;

        public void inc() {
            this.cnt++;
        }
    }

    @Test
    public void testEAMLockDemo() throws InterruptedException {
        final Counter cnt = new Counter();
        final Lock lock = new ReentrantLock();
        final CountDownLatch latch0 = new CountDownLatch(1);
        final CountDownLatch latch1 = new CountDownLatch(4);
        final ExecutorService es = Executors.newFixedThreadPool(4);
        final Runnable runner = () -> {
            try {
                latch0.await();
                for (int i = 0; i < 1000_000; i++) {
                    runLocked(lock, () -> cnt.inc());
                }
                latch1.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        es.submit(runner);
        es.submit(runner);
        es.submit(runner);
        es.submit(runner);
        latch0.countDown();
        es.shutdown();
        latch1.await();
        assertThat(cnt.cnt).isEqualTo(4 * 1000_000);
    }

    // chapter 5.4 : assert Exception demo

    static <X extends Throwable> Throwable myAssertThrows(final Class<X> exceptionClass, final Runnable block) {
        try {
            block.run();
        } catch (Throwable ex) {
            if (exceptionClass.isInstance(ex)) {
                return ex;
            }
        }
        fail("Failed to throw expected exception");
        return null;
    }

    static class RodCutter {
        public void setPrices(final List<Integer> prices) {
        }

        public int maxProfit(final int length) {
            if (length == 0) {
                throw new RuntimeException("rodcutter");
            }
            return 0;
        }
    }

    @Test
    public void testMyAssertThrows() {
        final RodCutter rc = new RodCutter();
        rc.setPrices(List.of(1, 2, 3));
        myAssertThrows(RuntimeException.class, () -> rc.maxProfit(0));
    }
}
