package javasnack.snacks.jsse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javasnack.RunnableSnack;

public class ConfusingCertificateProxiedClient implements RunnableSnack {

    @Override
    public void run(final String... args) throws Exception {

        if (args.length < 3) {
            System.out.println("Usage: <proxy-host> <proxy-port> <ConfusingCertificateWebServer-listening-port>");
            return;
        }
        final String proxyHost = args[0];
        final int proxyPort = Integer.parseInt(args[1]);
        final String destinationHost = "localhost";
        final int destinationPort = Integer.parseInt(args[2]);

        try (final Socket proxySocket = new Socket(proxyHost, proxyPort)) {
            PrintWriter out = new PrintWriter(proxySocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(proxySocket.getInputStream()));

            // send CONNECT request (implicitly set different hostname on Host header for demonstration)
            out.printf("CONNECT %s:%d HTTP/1.1\r\nHost: host-header-connect.test:%d\r\n\r\n",
                destinationHost,
                destinationPort,
                destinationPort);
            out.flush();

            // receive proxy response
            String line;
            boolean connected = false;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("HTTP/1.1 200")) {
                    connected = true;
                }
                if (line.isEmpty())
                    break; // end of http request headers
            }
            if (!connected) {
                System.err.println("Proxy CONNECT failed.");
                return;
            }

            // disable certificate validation (for demo/test only)
            final TrustManager[] trustAllCertificateManagers = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificateManagers, new SecureRandom());

            // upgrade to TLS
            final SSLSocketFactory factory = sslContext.getSocketFactory();
            final SSLSocket sslSocket = (SSLSocket) factory.createSocket(proxySocket, proxyHost, proxyPort, true);

            // enable SNI (ServerNameIndication)
            final SSLParameters sslParameters = sslSocket.getSSLParameters();
            sslParameters.setServerNames(List.of(new SNIHostName("client-sni0.test")));
            sslSocket.setSSLParameters(sslParameters);

            sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                @Override
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    try {
                        X509Certificate[] certs = (X509Certificate[]) event.getPeerCertificates();
                        for (int i = 0; i < certs.length; i++) {
                            System.out.println("cert[" + i + "] - Subject: " + certs[i].getSubjectX500Principal().getName());
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to print server certificates: " + e);
                    }
                }
            });

            sslSocket.startHandshake();

            // Send HTTPS GET request
            PrintWriter sslOut = new PrintWriter(sslSocket.getOutputStream(), true);
            BufferedReader sslIn = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));

            sslOut.print("GET / HTTP/1.1\r\nHost: plaintext-req.test\r\nConnection: close\r\n\r\n");
            sslOut.flush();

            // Print response
            while ((line = sslIn.readLine()) != null) {
                System.out.println(line);
            }

            sslSocket.close();
        }
    }
}
