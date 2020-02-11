/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import javasnack.snacks.perfs.list.PerfArrayListFinePutGet;
import javasnack.snacks.perfs.list.PerfJavaArrayFinePutGet;
import javasnack.snacks.perfs.list.PerfLinkedListFinePutGet;
import javasnack.snacks.perfs.map.PerfHashMapFinePutGet;
import javasnack.snacks.perfs.map.PerfHashMapTotalAvg;
import javasnack.snacks.perfs.map.PerfLinkedHashMapFinePutGet;
import javasnack.snacks.perfs.map.PerfLinkedHashMapTotalAvg;
import javasnack.snacks.perfs.map.PerfTreeMapFinePutGet;
import javasnack.snacks.perfs.map.PerfTreeMapTotalAvg;

/**
 * Console Play Board:
 * <code> 
 * mvn exec:java -Dexec.mainClass=javasnack.Main
 * </code>
 * 
 * @author "Masahiko Sakamoto"(msakamoto-sf, sakamoto.gsyc.3s@gmail.com)
 */
public class Main {
    // TODO -> migrate to JMH Benchmark
    static Runnable[] snacks = new Runnable[] {
            new PerfJavaArrayFinePutGet(),
            new PerfArrayListFinePutGet(),
            new PerfLinkedListFinePutGet(),
            new PerfHashMapFinePutGet(),
            new PerfHashMapTotalAvg(),
            new PerfLinkedHashMapFinePutGet(),
            new PerfLinkedHashMapTotalAvg(),
            new PerfTreeMapFinePutGet(),
            new PerfTreeMapTotalAvg(),
    };

    static void usage(final Set<String> snackNames) {
        System.out.println("usage: java -jar javasnack-(version).jar <snackName> (args1, args2, ...)");
        System.out.println("available snack names:");
        for (final String snackName : snackNames) {
            System.out.println("  " + snackName);
        }
        System.exit(-1);
    }

    public static void main(String[] args) throws Exception {
        final Map<String, Class<?>> runnableSnackClasses = new TreeMap<>();
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .whitelistPackages("javasnack.snacks")
                .scan()) {
            for (final ClassInfo classInfo : scanResult.getClassesImplementing(RunnableSnack.class.getName())) {
                final Class<?> clazz = classInfo.loadClass();
                runnableSnackClasses.put(clazz.getSimpleName(), clazz);
            }
        }

        final String snackName;
        final String[] args1;
        if (args.length < 1) {
            System.out.println("Welcome to Java Snack!!");
            System.out.println("available snack names:");
            for (final String n : runnableSnackClasses.keySet()) {
                System.out.println("  " + n);
            }
            System.out.print("Enter snack name (exit for ENTER):");
            final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            final String readLine = br.readLine();
            if (Objects.isNull(readLine)) {
                return;
            }
            snackName = readLine.trim();
            args1 = args;
        } else {
            snackName = args[0];
            args1 = Arrays.copyOfRange(args, 1, args.length);
        }

        final Class<?> snackClazz = runnableSnackClasses.get(snackName);
        if (Objects.isNull(snackClazz)) {
            usage(runnableSnackClasses.keySet());
        }

        final Constructor<?> c0 = snackClazz.getConstructor();
        final RunnableSnack snack = (RunnableSnack) c0.newInstance();
        new Thread(() -> {
            try {
                snack.run(args1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
