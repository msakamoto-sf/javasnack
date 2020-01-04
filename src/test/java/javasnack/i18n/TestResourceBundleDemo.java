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
