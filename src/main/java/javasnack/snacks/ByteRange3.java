package javasnack.snacks;

import javasnack.tool.UnsignedByte;

public class ByteRange3 implements Runnable {

    @Override
    public void run() {
        for (short s = 0x00; s <= 0xFF; s++) {
            byte b = UnsignedByte.from(s);
            System.out.print(b);
            System.out.print(",");
            if (s % 8 == 0) {
                System.out.println("");
            }
        }
        System.out.println("(END)");
    }

}
