/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.snacks;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import javasnack.RunnableSnack;
import javasnack.tool.Sleep;

/**
 * {@link Set#add(Object)} と {@link Set#iterator()}(拡張for構文) を複数スレッドで並行操作するメモ。
 * 
 * @author msakamoto
 */
public class SynchronizedSetConcurrentModifyDemo implements RunnableSnack {

    private static class SomeObject {
        final int someInt;

        SomeObject(int val) {
            this.someInt = val;
        }

        int getVal() {
            return someInt;
        }
    }

    static final long MASS = 1000;

    /**
     * {@link Set} オブジェクトに {@link Set#add(Object)} し続ける書き込みスレッド。
     * {@link AtomicBoolean} のフラグがtrueになったらそこで終了する。
     */
    private static class WriterThread extends Thread {
        private final String label;
        private final Set<SomeObject> someObjects;
        private final AtomicBoolean terminator;

        WriterThread(final String label, final Set<SomeObject> someObjects, final AtomicBoolean terminator) {
            this.label = label;
            this.someObjects = someObjects;
            this.terminator = terminator;
        }

        public void run() {
            System.out.println("writer[" + label + "] started.");
            final Random rnd = new Random();
            long marker = 0L;
            for (long i = 0; i < MASS && !this.terminator.get(); i++) {
                marker = i;
                someObjects.add(new SomeObject(rnd.nextInt(10)));
                Sleep.millis(10);
            }
            if ((marker + 1) < MASS) {
                System.out.println("writer[" + label + "] terminated.");
            } else {
                System.out.println("writer[" + label + "] completed.");
            }
        }
    }

    /**
     * {@link Set#iterator()} を拡張for構文経由でアクセスし、iterationするスレッド。
     * 特別な同期化機能は使わない。
     * {@link ConcurrentModificationException} 等、何か例外が発生したら terminator フラグをセットして終了する。
     */
    private static class IterateWithoutSyncThread extends Thread {
        private final String label;
        private final Set<SomeObject> someObjects;
        private final AtomicBoolean terminator;

        IterateWithoutSyncThread(final String label, final Set<SomeObject> someObjects,
                final AtomicBoolean terminator) {
            this.label = label;
            this.someObjects = someObjects;
            this.terminator = terminator;
        }

        public void run() {
            System.out.println("iterator[" + label + "] started.");
            try {
                for (long i = 0; i < MASS; i++) {
                    long sum = 0;
                    for (SomeObject o : someObjects) {
                        sum += o.getVal();
                    }
                    System.out.println("iterator[" + label + "](" + i + ") sum = " + sum);
                    Sleep.millis(100);
                }
                System.out.println("iterator[" + label + "] completed.");
            } catch (Throwable t) {
                System.out.println("iterator[" + label + "] exception!");
                t.printStackTrace();
                terminator.set(true);
            }
        }
    }

    /**
     * {@link Set} インスタンスを synchronized で同期化してiterationするスレッド。
     */
    private static class IterateWithSyncThread extends Thread {
        private final String label;
        private final Set<SomeObject> someObjects;
        private final AtomicBoolean terminator;

        IterateWithSyncThread(final String label, final Set<SomeObject> someObjects, final AtomicBoolean terminator) {
            this.label = label;
            this.someObjects = someObjects;
            this.terminator = terminator;
        }

        public void run() {
            System.out.println("iterator[" + label + "] started.");
            try {
                for (long i = 0; i < MASS; i++) {
                    long sum = 0;
                    synchronized (someObjects) {
                        for (SomeObject o : someObjects) {
                            sum += o.getVal();
                        }
                    }
                    System.out.println("iterator[" + label + "](" + i + ") sum = " + sum);
                    Sleep.millis(100);
                }
                System.out.println("iterator[" + label + "] completed.");
            } catch (Throwable t) {
                System.out.println("iterator[" + label + "] exception!");
                t.printStackTrace();
                terminator.set(true);
            }
        }
    }

    @Override
    public void run(final String... args) {
        /* CASE-1 : synchronizedSet()でラップしたSetを、同期化していないiterationスレッドと書き込みスレッドで同時にread/writeする。
         * -> 早い段階で iteration スレッドから ConcurrentModificationException がスローされる。
         */
        final Set<SomeObject> hashedSomeObjects = Collections.synchronizedSet(new HashSet<SomeObject>());
        final AtomicBoolean terminator1 = new AtomicBoolean(false);
        new IterateWithoutSyncThread("HashSet", hashedSomeObjects, terminator1).start();
        new WriterThread("HashSet", hashedSomeObjects, terminator1).start();

        /* CASE-2 : synchronizedSet()でラップしたSetを、同期化したiterationスレッドと書き込みスレッドで同時にread/writeする。
         * -> こちらは ConcurrentModificationException はスローされない。
         */
        final Set<SomeObject> hashedSomeObjects2 = Collections.synchronizedSet(new HashSet<SomeObject>());
        final AtomicBoolean terminator2 = new AtomicBoolean(false);
        new IterateWithSyncThread("HashSet + Sync", hashedSomeObjects2, terminator2).start();
        new WriterThread("HashSet + Sync", hashedSomeObjects2, terminator2).start();

        /* CASE-3 : CopyOnWriteArraySet ではiteration中の別スレッドからの変更(追加/削除)は、配列をコピーして行われる。
         * 変更操作はiteratorに反映されないため、iteratorは"スナップショット"として使うことになる。
         * -> 同期化していないiterationスレッドを使っての同時読み書きでも特に例外はスローされない。
         * 
         * マルチスレッド上で、全体として変更操作よりもiteration操作の方が明らかに多い場合はこちらが有用。
         * (iteratorに対する要素変更はサポートされず、実行するとUnsupportedOperationExceptionがthrowされる。)
         */
        final Set<SomeObject> copyOnWriteObjects = new CopyOnWriteArraySet<>();
        final AtomicBoolean terminator3 = new AtomicBoolean(false);
        new IterateWithoutSyncThread("CopyOnWriteArraySet", copyOnWriteObjects, terminator3).start();
        new WriterThread("CopyOnWriteArraySet", copyOnWriteObjects, terminator3).start();
    }
}
