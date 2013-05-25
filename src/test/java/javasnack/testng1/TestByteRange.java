package javasnack.testng1;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javasnack.tool.UnsignedByte;

import org.testng.annotations.Test;

public class TestByteRange {
    @Test
    public void read0x00to0xFFRawBinaryToByteArray() throws IOException {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        InputStream is = getClass().getResourceAsStream(
                "/testdata/0x00_to_0xFF.binarydata");
        int b = 0;
        while ((b = is.read()) != -1) {
            bas.write(b);
        }
        is.close();
        bas.flush();
        bas.close();

        byte[] r = bas.toByteArray();
        
        byte[] expected = new byte[r.length];
        for (int i = 0; i < r.length; i++) {
            expected[i] = UnsignedByte.from(i);
        }
        assertThat(r, is(expected));
    }
}
