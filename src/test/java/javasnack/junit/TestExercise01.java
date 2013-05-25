package javasnack.junit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestExercise01 {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.out.println(Thread.currentThread() + " - setUpBeforeClass()");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.out.println(Thread.currentThread() + " - tearDownAfterClass()");
    }

    @Before
    public void setUp() throws Exception {
        System.out.println(Thread.currentThread() + " - setUp()");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println(Thread.currentThread() + " - tearDown()");
    }

    List<String> mylist = Arrays.asList("123", "456");

    @Test
    public void listLength1() {
        mylist = Arrays.asList("abc", "def", "ghi");
        System.out.println(mylist);
        assertEquals(3, mylist.size());
    }

    @Test
    public void listLength2() {
        System.out.println(mylist);
        assertEquals(2, mylist.size());
    }

}
