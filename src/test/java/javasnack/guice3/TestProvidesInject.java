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

import static org.testng.Assert.*;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * @see http://d.hatena.ne.jp/nodchip/20130126/1359161946
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class TestProvidesInject {
    public static class ProvidesInjecteeArg {
        final String msg;

        public ProvidesInjecteeArg() {
            this.msg = "Hello, Provides Injection.";
        }

        public String hello() {
            return this.msg;
        }
    }

    public interface ProvidesInjecteeInterface {
        public String greet();
    }

    public static class ProvidesInjectee implements ProvidesInjecteeInterface {
        final ProvidesInjecteeArg arg;

        public ProvidesInjectee(ProvidesInjecteeArg arg) {
            this.arg = Preconditions.checkNotNull(arg);
        }

        @Override
        public String greet() {
            return this.arg.hello();
        }
    }

    public static class ConstructorArgIsProvided {
        final ProvidesInjecteeInterface injectee;

        @Inject
        public ConstructorArgIsProvided(ProvidesInjecteeInterface i) {
            this.injectee = i;
        }

        public String getResult() {
            return injectee.greet();
        }
    }

    public static class TestProvidesModule extends AbstractModule {
        @Override
        protected void configure() {
        }

        @Provides
        private ProvidesInjecteeInterface provides(ProvidesInjecteeArg arg) {
            return new ProvidesInjectee(arg);
        }
    }

    @Test
    public void testSimpleInject() {
        Injector i = Guice.createInjector(new TestProvidesModule());
        ConstructorArgIsProvided c = i.getInstance(ConstructorArgIsProvided.class);
        assertEquals(c.getResult(), "Hello, Provides Injection.");
    }
}
