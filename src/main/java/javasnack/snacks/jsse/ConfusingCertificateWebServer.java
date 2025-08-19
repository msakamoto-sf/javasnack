package javasnack.snacks.jsse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import javasnack.RunnableSnack;

public class ConfusingCertificateWebServer implements RunnableSnack {

    @Override
    public void run(final String... args) throws Exception {

        if (args.length < 1) {
            System.out.println("Usage: <port>");
            return;
        }
        final int port = Integer.parseInt(args[0]);

        // load demo keystore(pkcs12)
        final InputStream pkcs12 = this.getClass().getClassLoader().getResourceAsStream("jsse/ConfusingCertificate/server.p12");
        final KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(pkcs12, "".toCharArray());

        final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, "".toCharArray());

        final SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), null, null);

        final SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        try (SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(port)) {
            System.out.println("ConfusingCertificateWebServer started on port " + port);
            System.out.println("Ctrl-C stops the server.");

            while (true) {
                final SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    private void handleClient(SSLSocket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // demo : don't use http request, simply ignored.
            }
            out.write("HTTP/1.1 200 OK\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("\r\n");
            out.write("Hello from ConfusingCertificateWebServer!\r\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
