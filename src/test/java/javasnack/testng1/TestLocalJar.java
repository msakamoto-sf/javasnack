package javasnack.testng1;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import testjar1.Greeting;

public class TestLocalJar {

    @Test
    public void useLocalJar() {
        Greeting g = new Greeting();
        assertEquals(g.afternoon("abc"), "Good Afternoon, abc.");
    }

}
