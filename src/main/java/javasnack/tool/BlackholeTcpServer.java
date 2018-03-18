/*
 * Copyright 2018 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javasnack.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlackholeTcpServer {

    final int port;

    public BlackholeTcpServer() {
        this.port = 0; // auto bind
    }

    public BlackholeTcpServer(final int port) {
        this.port = port;
    }

    final Map<InetSocketAddress, ByteArrayOutputStream> receivedBytesMap = new HashMap<>();

    ServerSocketChannel serverChannel = null;

    ExecutorService es = null;

    public int start() throws IOException {
        if (Objects.nonNull(serverChannel) || Objects.nonNull(es)) {
            throw new IllegalStateException();
        }
        Selector selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        serverChannel.bind(new InetSocketAddress(port), 10);
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        final int localPort = serverChannel.socket().getLocalPort();

        receivedBytesMap.clear();
        es = Executors.newSingleThreadExecutor();
        es.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("BlackHoleTcpServer: listener started at port " + localPort + ".");
                    while (selector.select() > 0) {
                        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            keyIterator.remove();
                            if (key.isAcceptable()) {
                                doAccept(selector, key);
                            } else if (key.isReadable()) {
                                doRead(key);
                            }
                        }
                    }
                } catch (IOException ignore) {
                    System.out.println("BlackHoleTcpServer: listener interrupted.");
                }
                System.out.println("BlackHoleTcpServer: listener stopped.");
            }
        });
        return localPort;
    }

    public void stop() {
        es.shutdown();
        try {
            serverChannel.close();
            es.shutdownNow();
        } catch (IOException ignore) {
        } finally {
            es = null;
            serverChannel = null;
        }
    }

    public Map<InetSocketAddress, byte[]> getReceivedBytes() {
        final Map<InetSocketAddress, byte[]> r = new HashMap<>();
        for (Map.Entry<InetSocketAddress, ByteArrayOutputStream> e : receivedBytesMap.entrySet()) {
            r.put(e.getKey(), e.getValue().toByteArray());
        }
        return r;
    }

    private void doAccept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverChannel.accept();
        InetSocketAddress sa = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
        this.receivedBytesMap.put(sa, new ByteArrayOutputStream());
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        InetSocketAddress sa = (InetSocketAddress) channel.socket().getRemoteSocketAddress();
        ByteArrayOutputStream out = this.receivedBytesMap.get(sa);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int readlen = 0;
        do {
            buffer.clear();
            readlen = channel.read(buffer);
            if (readlen < 0) {
                channel.close();
                return;
            }
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            out.write(data);
        } while (readlen > 0);

        if (key.interestOps() != SelectionKey.OP_READ) {
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
