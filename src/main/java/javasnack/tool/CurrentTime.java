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

package javasnack.tool;

public class CurrentTime {

    protected static final ICurrentTimeProvider CURRENTTIME_SYSTEM = new ICurrentTimeProvider() {
        @Override
        public long millis() {
            return System.currentTimeMillis();
        }
    };

    protected static ThreadLocal<ICurrentTimeProvider> threadLocalProvider = new ThreadLocal<ICurrentTimeProvider>() {
        protected ICurrentTimeProvider initialValue() {
            return CURRENTTIME_SYSTEM;
        }
    };

    public static long millis() {
        return threadLocalProvider.get().millis();
    }

    public static void mock(ICurrentTimeProvider mockProvider) {
        threadLocalProvider.remove();
        threadLocalProvider.set(mockProvider);
    }

    public static void unmock() {
        threadLocalProvider.remove();
    }
}
