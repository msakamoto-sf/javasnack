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
package javasnack.junit;

import static org.junit.Assert.*;
import javasnack.testee.AngryStaticUtil;
import javasnack.testee.AngryStaticUtilUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AngryStaticUtil.class)
public class TestAngryStaticUtilUser {

    @Test
    public void testAngryStaticUtil() {
        PowerMockito.mockStatic(AngryStaticUtil.class);
        Mockito.when(AngryStaticUtil.addSecretTo(1)).thenReturn(2);
        assertEquals(AngryStaticUtilUser.addDelegate(1), 2);

        AngryStaticUtilUser client = new AngryStaticUtilUser(10);
        assertEquals(client.addSecretAndMe(1), 12);
    }

}
