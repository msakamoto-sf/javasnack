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

package javasnack.snacks.jsch;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javasnack.tool.ConsoleUtil;

/* see:
 * https://qiita.com/tool-taro/items/ab68353970ee792163c6
 * https://dev.classmethod.jp/server-side/java/exec_remote_program/
 * http://www.jcraft.com/jsch/examples/Exec.java.html
 * http://www.jcraft.com/jsch/examples/UserAuthPubKey.java.html
 */
public class JSchRemoeExecPubKeyAuthDemo implements Runnable {
    @Override
    public void run() {
        final String hostname = ConsoleUtil.readLine("hostname:");
        final int port = Integer.parseInt(ConsoleUtil.readLine("port num:"));
        final String username = ConsoleUtil.readLine("username:");
        final String prvkeyfp = ConsoleUtil.readLine("private key file path:");
        final String passphrase = ConsoleUtil.readLine("passphrase(empty if non-encrypted pubkey):"); // for test purpose
        final String cmd = ConsoleUtil.readLine("exec command:");
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        BufferedInputStream bin = null;

        try {
            if (Objects.isNull(passphrase) || passphrase.trim().length() == 0) {
                jsch.addIdentity(prvkeyfp);
            } else {
                jsch.addIdentity(prvkeyfp, passphrase);
            }
            session = jsch.getSession(username, hostname, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            channel.connect();
            bin = new BufferedInputStream(channel.getInputStream());
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int length;
            while (true) {
                length = bin.read(buf);
                if (length == -1) {
                    break;
                }
                bout.write(buf, 0, length);
            }
            System.out.format("result=%1$s", new String(bout.toByteArray(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(bin)) {
                try {
                    bin.close();
                } catch (IOException ignore) {
                }
            }
            if (Objects.nonNull(channel)) {
                channel.disconnect();
                System.out.format("exitStatus=%1$d", channel.getExitStatus());
            }
            if (Objects.nonNull(session)) {
                session.disconnect();
            }
        }
    }
}
