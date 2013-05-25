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
