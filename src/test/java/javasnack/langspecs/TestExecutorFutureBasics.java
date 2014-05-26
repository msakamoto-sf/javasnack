package javasnack.langspecs;

import static org.testng.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.annotations.Test;

public class TestExecutorFutureBasics {

    @Test(expectedExceptions = ExecutionException.class)
    public void testUncheckedExceptionCaughtThroughExecutionException()
            throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<?> f = es.submit(new Runnable() {
            @Override
            public void run() {
                String s = "hello";
                s = null;
                int l = s.length();
            }
        });
        try {
            f.get();
        } catch (ExecutionException expected) {
            Throwable cause = expected.getCause();
            assertEquals(cause.getClass(), NullPointerException.class);
            throw expected;
        }
        es.shutdown();
    }

    @Test(expectedExceptions = ExecutionException.class)
    public void testAssertionErrorCaughtThroughExecutionException()
            throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<?> f = es.submit(new Runnable() {
            @Override
            public void run() {
                int a = 10;
                int b = 20;
                assert a == b;
            }
        });
        try {
            f.get();
        } catch (ExecutionException expected) {
            Throwable cause = expected.getCause();
            assertEquals(cause.getClass(), AssertionError.class);
            throw expected;
        }
        es.shutdown();
    }
}
