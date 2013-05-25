package javasnack.junit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javasnack.testee.MockitoExercise01;

import org.junit.Test;

public class TestMockitoExercise01 {

    @Test
    public void test() {
        @SuppressWarnings("unchecked")
        List<String> mockedList = mock(List.class);
        String expectedIndex2 = "Hello";
        when(mockedList.get(2)).thenReturn(expectedIndex2);

        MockitoExercise01 t = new MockitoExercise01(mockedList);
        t.doSomething();

        verify(mockedList).add("Bonjour");
        verify(mockedList).clear();

        assertEquals(expectedIndex2, t.getIndex2());
    }

}
