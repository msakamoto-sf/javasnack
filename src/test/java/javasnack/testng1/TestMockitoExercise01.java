/*
 * Copyright 2013 the original author or authors.
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
