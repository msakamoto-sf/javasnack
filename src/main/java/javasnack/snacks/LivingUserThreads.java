package javasnack.snacks;

public class LivingUserThreads implements Runnable {

    @Override
    public void run() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread() + " - " + i);
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        };
        new Thread(r, "Thread-A").start();
        new Thread(r, "Thread-B").start();
    }

}
