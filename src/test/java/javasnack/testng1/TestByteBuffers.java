package javasnack.testng1;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestByteBuffers {

    @Test(expectedExceptions = { java.nio.BufferOverflowException.class })
    public void bufferOverflowMustOccurr() {
        ByteBuffer b = ByteBuffer.allocate(2);
        b.put((byte)1);
        b.put((byte)2);
        assertThat(b.position(), is(2));
        b.put((byte)3);
        Assert.fail();
    }

    @Test
    public void usageOfByteArrayOutputStream() throws UnsupportedEncodingException {
        // Allocate initial size as 2 byte.
        ByteArrayOutputStream o = new ByteArrayOutputStream(2);
        o.write((byte)0);
        o.write((byte)1);
        assertThat(o.size(), is(2));
        // Add more 1 byte.
        o.write((byte)2);
        // Size must expand 1.
        assertThat(o.size(), is(3));

        // get string
        String s = o.toString("ISO-8859-1");
        // get byte array from string
        byte[] r = s.getBytes("ISO-8859-1");
        // original toByteArray() must be equal to r.
        assertThat(r, is(o.toByteArray()));
    }
}
