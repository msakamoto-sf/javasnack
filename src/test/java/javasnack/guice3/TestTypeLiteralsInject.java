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
import com.google.inject.TypeLiteral;

/**
 * @see http://d.hatena.ne.jp/nodchip/20130126/1359161946
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class TestTypeLiteralsInject {
    public interface AcceptGenericTypeInterface<T> {
        public String getType(T t);
    }

    public static class AcceptGenericTypeImpl<T> implements
            AcceptGenericTypeInterface<T> {
        @Override
        public String getType(T t) {
            return "type = " + t.getClass().getName();
        }
    }

    public static class GenericTypeInjectee {
        final AcceptGenericTypeInterface<Integer> impl;

        @Inject
        public GenericTypeInjectee(AcceptGenericTypeInterface<Integer> impl) {
            this.impl = Preconditions.checkNotNull(impl);
        }

        public String getResult(Integer arg) {
            return impl.getType(arg);
        }
    }

    public static class TestGenericTypeModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(new TypeLiteral<AcceptGenericTypeInterface<Integer>>() {
            }).to(new TypeLiteral<AcceptGenericTypeImpl<Integer>>() {
            });
        }
    }

    @Test
    public void testTypeLiteralsInject() {
        Injector i = Guice.createInjector(new TestGenericTypeModule());
        GenericTypeInjectee g = i.getInstance(GenericTypeInjectee.class);
        assertEquals(g.getResult(100), "type = java.lang.Integer");
    }
}
