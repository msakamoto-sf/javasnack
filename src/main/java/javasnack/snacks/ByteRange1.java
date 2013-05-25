package javasnack.snacks;

public class ByteRange1 implements Runnable {
    @Override
    public void run() {
        byte b = 0;
        for (short s = 0x00; s <= 0xFF; s++, b++) {
            System.out.print(b);
            System.out.print(",");
            if (s % 8 == 0) {
                System.out.println("");
            }
        }
        System.out.println("(END)");
    }

}
