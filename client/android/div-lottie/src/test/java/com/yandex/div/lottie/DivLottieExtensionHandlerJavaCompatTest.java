package com.yandex.div.lottie;

import com.yandex.div.core.Disposable;
import com.yandex.div.internal.core.ExpressionSubscriber;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlinx.coroutines.GlobalScope;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Guards source and binary compatibility of {@link DivLottieExtensionHandler}'s public API
 * for Java clients: these exact signatures are used by hosts compiled against previous
 * DivKit artifacts, changing them breaks such hosts with {@link NoSuchMethodError}.
 */
@RunWith(RobolectricTestRunner.class)
public class DivLottieExtensionHandlerJavaCompatTest {

    @Test
    public void addSubscriptionDoesNotRetainSubscription() {
        ExpressionSubscriber subscriber = new DivLottieExtensionHandler(/* asyncUpdatesEnabled= */ false);

        subscriber.addSubscription(() -> { });

        Assert.assertEquals(Collections.emptyList(), subscriber.getSubscriptions());
    }

    @Test
    public void releaseDoesNotCloseOrRemoveSubscriptions() {
        ExpressionSubscriber subscriber = new DivLottieExtensionHandler(/* asyncUpdatesEnabled= */ false);
        AtomicBoolean closed = new AtomicBoolean();
        Disposable subscription = () -> closed.set(true);
        subscriber.getSubscriptions().add(subscription);

        subscriber.release();

        Assert.assertFalse(closed.get());
        Assert.assertEquals(Collections.singletonList(subscription), subscriber.getSubscriptions());
    }

    @Test
    public void closeAllSubscriptionDoesNotCloseOrRemoveSubscriptions() {
        ExpressionSubscriber subscriber = new DivLottieExtensionHandler(/* asyncUpdatesEnabled= */ false);
        AtomicBoolean closed = new AtomicBoolean();
        Disposable subscription = () -> closed.set(true);
        subscriber.getSubscriptions().add(subscription);

        subscriber.closeAllSubscription();

        Assert.assertFalse(closed.get());
        Assert.assertEquals(Collections.singletonList(subscription), subscriber.getSubscriptions());
    }

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
