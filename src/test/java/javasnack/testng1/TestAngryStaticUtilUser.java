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
