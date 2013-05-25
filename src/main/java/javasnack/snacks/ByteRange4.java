package javasnack.snacks;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javasnack.tool.UnsignedByte;

public class ByteRange4 implements Runnable {
    @Override
    public void run() {
        byte[] bdata = new byte[256];
        for (short s = 0; s < 256; s++) {
            byte b = UnsignedByte.from(s);
            bdata[s] = b;
        }
        FileOutputStream fos = null;
        try {
            System.out.println("");
            System.out.print("Enter FileName:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String fname = br.readLine().trim();
            fos = new FileOutputStream(fname);
            fos.write(bdata);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try { fos.close(); } catch (IOException ignore) {}
            }
        }
    }

}
