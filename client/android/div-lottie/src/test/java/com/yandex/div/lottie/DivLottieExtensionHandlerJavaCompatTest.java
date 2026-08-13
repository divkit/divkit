package com.yandex.div.lottie;

import kotlinx.coroutines.GlobalScope;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Guards source and binary compatibility of {@link DivLottieExtensionHandler} constructors
 * for Java clients: these exact signatures are used by hosts compiled against previous
 * DivKit artifacts, changing them breaks such hosts with {@link NoSuchMethodError}.
 */
@RunWith(RobolectricTestRunner.class)
public class DivLottieExtensionHandlerJavaCompatTest {

    @Test
    public void keepsThreeArgumentConstructor() {
        DivLottieExtensionHandler handler = new DivLottieExtensionHandler(
                DivLottieRawResProvider.Companion.getSTUB(),
                DivLottieLogger.Companion.getSTUB(),
                DivLottieNetworkCache.Companion.getSTUB()
        );
        Assert.assertNotNull(handler);
    }

    @Test
    public void providesFullConstructorWithAsyncUpdatesAndPreloadScope() {
        DivLottieExtensionHandler handler = new DivLottieExtensionHandler(
                DivLottieRawResProvider.Companion.getSTUB(),
                DivLottieLogger.Companion.getSTUB(),
                DivLottieNetworkCache.Companion.getSTUB(),
                false,
                GlobalScope.INSTANCE
        );
        Assert.assertNotNull(handler);
    }
}
