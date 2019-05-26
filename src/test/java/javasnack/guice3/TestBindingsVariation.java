/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.guice3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * @see http://d.hatena.ne.jp/nodchip/20130126/1359161946
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class TestBindingsVariation {
    public interface Greeting {
        String greet(String you);
    }

    public static class Morning implements Greeting {
        @Override
        public String greet(String you) {
            return "Good Mornig " + you + ".";
        }
    }

    public static class TestLinkedBindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Greeting.class).to(Morning.class);
        }
    }

    @Test
    public void testLinkedBindings() {
        Injector i = Guice.createInjector(new TestLinkedBindingModule());
        Greeting g = i.getInstance(Greeting.class);
        assertEquals("Good Mornig jon.", g.greet("jon"));
    }

    public static class SingleInstanceA {
        int count = 0;

        public int countup() {
            count++;
            return count;
        }
    }

    public static class SingleInstanceUser1 {
        final SingleInstanceA si;

        @Inject
        public SingleInstanceUser1(SingleInstanceA si) {
            this.si = si;
        }

        public int getCount() {
            return this.si.countup();
        }
    }

    public static class SingleInstanceUser2 {
        final SingleInstanceA si;

        @Inject
        public SingleInstanceUser2(SingleInstanceA si) {
            this.si = si;
        }

        public int getCount() {
            return this.si.countup();
        }
    }

    public static class TestScopeBindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(SingleInstanceA.class).in(Scopes.SINGLETON);
        }
    }

    @Test
    public void testSingletonScopeBindings() {
        Injector i = Guice.createInjector(new TestScopeBindingModule());
        SingleInstanceUser1 u1 = i.getInstance(SingleInstanceUser1.class);
        SingleInstanceUser2 u2 = i.getInstance(SingleInstanceUser2.class);
        assertEquals(1, u1.getCount());
        assertEquals(2, u2.getCount());
    }

    public static class TestInstanceBindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(SingleInstanceA.class).toInstance(new SingleInstanceA());
        }
    }

    @Test
    public void testInstanceBindings() {
        Injector i = Guice.createInjector(new TestInstanceBindingModule());
        SingleInstanceUser1 u1 = i.getInstance(SingleInstanceUser1.class);
        SingleInstanceUser2 u2 = i.getInstance(SingleInstanceUser2.class);
        assertEquals(1, u1.getCount());
        assertEquals(2, u2.getCount());
    }

    public static class Afternoon implements Greeting {
        @Override
        public String greet(String you) {
            return "Good Afternoon " + you + ".";
        }
    }

    public static class NamedBindingUser {
        final Greeting g1;
        final Greeting g2;

        @Inject
        public NamedBindingUser(@Named("BBB") Greeting g1,
                @Named("CCC") Greeting g2) {
            this.g1 = g1;
            this.g2 = g2;
        }

        public String greetBoth(String you) {
            return this.g1.greet(you) + "/" + this.g2.greet(you);
        }
    }

    public static class TestNamedBindingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Greeting.class).annotatedWith(Names.named("BBB")).to(
                    Morning.class);
            bind(Greeting.class).annotatedWith(Names.named("CCC")).to(
                    Afternoon.class);
        }
    }

    @Test
    public void testNamedAnnotationBindings() {
        Injector i = Guice.createInjector(new TestNamedBindingModule());
        NamedBindingUser u = i.getInstance(NamedBindingUser.class);
        assertEquals("Good Mornig bob./Good Afternoon bob.", u.greetBoth("bob"));
    }
}
