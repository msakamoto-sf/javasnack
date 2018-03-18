package javasnack.tool;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import org.testng.annotations.Test;

public class TestBlackholeTcpServer {

    @Test
    public void testStartStop() throws IOException {
        BlackholeTcpServer server = new BlackholeTcpServer();
        final int localPort = server.start();
        assertTrue(localPort > 0);
        try {
            server.start();
            fail("shold not reach here.");
        } catch (IllegalStateException expected) {
        }
        server.stop();
    }

    @Test
    public void testReceivedData() throws IOException {
        BlackholeTcpServer server = new BlackholeTcpServer();
        final int localPort = server.start();
        InetSocketAddress connectTo = new InetSocketAddress("127.0.0.1", localPort);
        Socket socket = new Socket();
        socket.connect(connectTo);
        InetSocketAddress local1 = (InetSocketAddress) socket.getLocalSocketAddress();
        OutputStream out = socket.getOutputStream();
        out.write(new byte[] { 0x00, 0x01, 0x02 });
        out.write(new byte[] { 0x03, 0x04, 0x05 });
        out.flush();
        socket.close();

        socket = new Socket();
        socket.connect(connectTo);
        InetSocketAddress local2 = (InetSocketAddress) socket.getLocalSocketAddress();
        out = socket.getOutputStream();
        out.write(new byte[] { 0x06, 0x07, 0x08 });
        out.write(new byte[] { 0x09, 0x0a, 0x0b });
        out.flush();
        socket.close();

        server.stop();
        Map<InetSocketAddress, byte[]> receivedData = server.getReceivedBytes();
        assertEquals(receivedData.size(), 2);
        assertTrue(receivedData.containsKey(local1));
        assertTrue(receivedData.containsKey(local2));
        assertEquals(receivedData.get(local1), new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05 });
        assertEquals(receivedData.get(local2), new byte[] { 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b });
    }
}
