package javasnack.tool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestBlackholeTcpServer {

    @Test
    public void testStartStop() throws IOException {
        BlackholeTcpServer server = new BlackholeTcpServer();
        final int localPort = server.start();
        assertThat(localPort).isGreaterThan(0);
        try {
            server.start();
            fail("shold not reach here.");
        } catch (IllegalStateException expected) {
        }
        server.stop();
    }

    @Test
    public void testReceivedData() throws IOException, InterruptedException {
        final BlackholeTcpServer server = new BlackholeTcpServer();
        Map<InetSocketAddress, byte[]> receivedData = server.getReceivedBytes();
        assertThat(receivedData).isEmpty();

        final int localPort = server.start();
        final InetSocketAddress connectTo = new InetSocketAddress(InetAddress.getLoopbackAddress(), localPort);
        Socket socket = new Socket();
        socket.connect(connectTo);
        final InetSocketAddress local1 = (InetSocketAddress) socket.getLocalSocketAddress();
        OutputStream out = socket.getOutputStream();
        out.write(new byte[] { 0x00, 0x01, 0x02 });
        out.write(new byte[] { 0x03, 0x04, 0x05 });
        out.flush();
        socket.close();

        socket = new Socket();
        socket.connect(connectTo);
        final InetSocketAddress local2 = (InetSocketAddress) socket.getLocalSocketAddress();
        out = socket.getOutputStream();
        out.write(new byte[] { 0x06, 0x07, 0x08 });
        out.write(new byte[] { 0x09, 0x0a, 0x0b });
        out.flush();
        socket.close();

        // wait data receiving ... :P
        Thread.sleep(50);

        server.stop();
        receivedData = server.getReceivedBytes();
        assertThat(receivedData).hasSize(2).containsKeys(local1, local2);
        assertThat(receivedData.get(local1)).isEqualTo(new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05 });
        assertThat(receivedData.get(local2)).isEqualTo(new byte[] { 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b });
    }
}
