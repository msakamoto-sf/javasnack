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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import javasnack.testee.AngryStaticUtil;
import javasnack.testee.AngryStaticUtilUser;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

@PrepareForTest(AngryStaticUtil.class)
public class TestAngryStaticUtilUser {

// Following declaration leads "Inconsistent stackmap frames at branch target ..." TestNGException.
//public class TestAngryStaticUtilUser2 extends PowerMockTestCase {

    /**
     * We need a special {@link IObjectFactory}.
     *
     * @return {@link PowerMockObjectFactory}.
     */
    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @Test
    public void powerMockStaticForTestNG() {
        PowerMockito.mockStatic(AngryStaticUtil.class);
        Mockito.when(AngryStaticUtil.addSecretTo(1)).thenReturn(2);
        assertThat(2, is(AngryStaticUtilUser.addDelegate(1)));

        AngryStaticUtilUser client = new AngryStaticUtilUser(10);
        assertThat(12, is(client.addSecretAndMe(1)));
    }
}
