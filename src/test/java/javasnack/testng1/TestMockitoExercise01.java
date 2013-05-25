package javasnack.testng1;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import java.util.List;

import javasnack.testee.MockitoExercise01;

import org.testng.annotations.Test;

public class TestMockitoExercise01 {
    @Test
    public void mockitoOnTestNG() {
        @SuppressWarnings("unchecked")
        List<String> mockedList = mock(List.class);
        String expectedIndex2 = "Hello";
        when(mockedList.get(2)).thenReturn(expectedIndex2);

        MockitoExercise01 t = new MockitoExercise01(mockedList);
        t.doSomething();

        verify(mockedList).add("Bonjour");
        verify(mockedList).clear();
        assertEquals(t.getIndex2(), expectedIndex2);
    }
}
