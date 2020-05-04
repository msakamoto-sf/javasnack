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

package javasnack.i18n;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

public class TestResourceBundleDemo {

    @Test
    public void classpathPropertyDemo() {
        final ResourceBundle rb1 = ResourceBundle.getBundle("i18n/sample-resource", Locale.US);
        assertThat(rb1.getString("key1")).isEqualTo("Hello, World");
        assertThat(rb1.getString("key2")).isEqualTo("ABCDEF");
        final Exception e1 = assertThrows(MissingResourceException.class, () -> rb1.getString("key3"));
        assertEquals("Can't find resource for bundle java.util.PropertyResourceBundle, key key3", e1.getMessage());

        final ResourceBundle rb2 = ResourceBundle.getBundle("i18n/sample-resource", Locale.JAPAN);
        assertThat(rb2.getString("key1")).isEqualTo("こんにちは");
        assertThat(rb2.getString("key2")).isEqualTo("あいうえお");
        final Exception e2 = assertThrows(MissingResourceException.class, () -> rb2.getString("key3"));
        assertEquals("Can't find resource for bundle java.util.PropertyResourceBundle, key key3", e2.getMessage());

        final ResourceBundle rb3 = ResourceBundle.getBundle("i18n/sample-resource", Locale.GERMAN);
        // hmmm... ???
        assertThat(rb3.getString("key1")).isEqualTo("こんにちは");
        assertThat(rb3.getString("key2")).isEqualTo("あいうえお");
    }
}
