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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @see http://d.hatena.ne.jp/nodchip/20130126/1359161946
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class TestAssistedInject {
    public static class InjectedArgument {
        public String doSomething() {
            return "Hello, Assisted Injection.";
        }
    }

    public interface SomeInterface {
        public String getResult();
    }

    public static class SomeImpl implements SomeInterface {
        final InjectedArgument arg;
        final int number;

        @Inject
        public SomeImpl(InjectedArgument arg, @Assisted int number) {
            this.arg = arg;
            this.number = number;
        }

        @Override
        public String getResult() {
            return this.arg.doSomething() + "[" + this.number + "]";
        }
    }

    public interface SomeInterfaceFactory {
        public SomeInterface create(int number);
    }

    public static class SomeInterfaceUser {
        final SomeInterfaceFactory f;

        @Inject
        public SomeInterfaceUser(SomeInterfaceFactory factory) {
            this.f = factory;
        }

        public String getResult() {
            SomeInterface i = this.f.create(100);
            return i.getResult();
        }
    }

    public static class TestAssistedModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new FactoryModuleBuilder().implement(SomeInterface.class,
                    SomeImpl.class).build(SomeInterfaceFactory.class));
        }
    }

    @Test
    public void testAssistedInject() {
        Injector i = Guice.createInjector(new TestAssistedModule());
        SomeInterfaceUser u = i.getInstance(SomeInterfaceUser.class);
        assertEquals(u.getResult(), "Hello, Assisted Injection.[100]");
    }
}
