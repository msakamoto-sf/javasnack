/*
 * Copyright 2013 the original author or authors.
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
package javasnack.testng1;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

/**
 * Unit Testing {@link Socket#connect(SocketAddress, int)} retry logic with TestNG + Mockito.
 */
public class TestSocketConnectRetry {

    /**
     * connect with retry.
     * 
     * This method returns excepiton list ocurrered internally whenn retrying.
     * 
     * So, use {@link Socket#isConnected()} to decide connection success or fail.
     * 
     * @param socket
     * @param sa
     * @param toms
     * @param retrymax
     * @return
     */
    List<Exception> connectWithRetry(Socket socket, SocketAddress sa, int toms,
            int retrymax) {
        List<Exception> exl = new ArrayList<>();
        for (int i = 0; i < retrymax; i++) {
            try {
                socket.connect(sa, toms);
                // connect success, then
                break;
            } catch (Exception e) {
                exl.add(e);
            }
        }
        return exl;
    }

    @Test
    public void testConnectWithoutRetry() throws IOException {
        Socket s = mock(Socket.class);
        SocketAddress sa = new InetSocketAddress("localhost", 8080);
        int toms = 1;
        int retrymax = 3;

        doNothing().when(s).connect(sa, toms);
        List<Exception> exl = connectWithRetry(s, sa, toms, retrymax);

        verify(s, times(1)).connect(sa, toms);
        assertEquals(exl.size(), 0);
    }

    @Test
    public void testConnectWithRetry1() throws IOException {
        Socket s = mock(Socket.class);
        SocketAddress sa = new InetSocketAddress("localhost", 8080);
        int toms = 1;
        int retrymax = 3;

        doThrow(new SocketTimeoutException("test1")).doNothing().when(s)
                .connect(sa, toms);
        List<Exception> exl = connectWithRetry(s, sa, toms, retrymax);

        verify(s, times(2)).connect(sa, toms);
        assertEquals(exl.size(), 1);
        assertEquals(exl.get(0).getMessage(), "test1");
    }

    @Test
    public void testConnectWithRetry2() throws IOException {
        Socket s = mock(Socket.class);
        SocketAddress sa = new InetSocketAddress("localhost", 8080);
        int toms = 1;
        int retrymax = 3;

        doThrow(new SocketTimeoutException("test1"))
                .doThrow(new ConnectException("test2")).doNothing().when(s)
                .connect(sa, toms);
        List<Exception> exl = connectWithRetry(s, sa, toms, retrymax);

        verify(s, times(3)).connect(sa, toms);
        assertEquals(exl.size(), 2);
        assertEquals(exl.get(0).getMessage(), "test1");
        assertEquals(exl.get(1).getMessage(), "test2");
    }

    @Test
    public void testConnectWithRetryOver() throws IOException {
        Socket s = mock(Socket.class);
        SocketAddress sa = new InetSocketAddress("localhost", 8080);
        int toms = 1;
        int retrymax = 3;

        doThrow(new SocketTimeoutException("test1"))
                .doThrow(new ConnectException("test2"))
                .doThrow(new IOException("test3"))
                .doThrow(new IOException("NeverThrowThisException")).when(s)
                .connect(sa, toms);
        List<Exception> exl = connectWithRetry(s, sa, toms, retrymax);

        verify(s, times(3)).connect(sa, toms);
        assertEquals(exl.size(), 3);
        assertEquals(exl.get(0).getMessage(), "test1");
        assertEquals(exl.get(1).getMessage(), "test2");
        assertEquals(exl.get(2).getMessage(), "test3");
    }
}
