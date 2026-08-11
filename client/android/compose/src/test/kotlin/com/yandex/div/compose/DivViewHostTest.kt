package com.yandex.div.compose

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.host.CheckVisibilityCallback
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.test.data.data
import com.yandex.div.test.data.image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.shadows.ShadowLooper
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DivViewHostTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val testScope = CoroutineScope(UnconfinedTestDispatcher())
    private val imageLoaderConfiguration = TestImageLoaderConfiguration()

    private val capturedUrls: MutableSet<String>
        get() = imageLoaderConfiguration.capturedUrls

    @Test
    fun `onVisibleBoundsChanged invokes registered callback`() {
        val host = createHost()
        var invokeCount = 0
        val callback = CheckVisibilityCallback { invokeCount++ }

        host.addCallback(callback)
        host.onVisibleBoundsChanged()

        assertEquals(1, invokeCount)
    }

    @Test
    fun `onVisibleBoundsChanged invokes each callback once`() {
        val host = createHost()
        var firstCount = 0
        var secondCount = 0

        host.addCallback { firstCount++ }
        host.addCallback { secondCount++ }
        host.onVisibleBoundsChanged()

        assertEquals(1, firstCount)
        assertEquals(1, secondCount)
    }

    @Test
    fun `removeCallback stops further invocations`() {
        val host = createHost()
        var invokeCount = 0
        val callback = CheckVisibilityCallback { invokeCount++ }

        host.addCallback(callback)
        host.onVisibleBoundsChanged()
        host.removeCallback(callback)
        host.onVisibleBoundsChanged()

        assertEquals(1, invokeCount)
    }

    @Test
    fun `duplicate callback is invoked once per onVisibleBoundsChanged`() {
        val host = createHost()
        var invokeCount = 0
        val callback = CheckVisibilityCallback { invokeCount++ }

        host.addCallback(callback)
        host.addCallback(callback)
        host.onVisibleBoundsChanged()

        assertEquals(1, invokeCount)
    }

    @Test
    fun `layout change invokes callbacks when view is attached`() {
        val host = createHost()
        attachToActivity(host)
        var invokeCount = 0
        host.addCallback { invokeCount++ }

        host.composeView.requestLayout()
        ShadowLooper.idleMainLooper()

        assertEquals(1, invokeCount)
    }

    @Test
    fun `layout change does not invoke callbacks after last callback removed`() {
        val host = createHost()
        var invokeCount = 0
        val callback = CheckVisibilityCallback { invokeCount++ }

        host.addCallback(callback)
        host.removeCallback(callback)

        attachToActivity(host)
        host.composeView.requestLayout()
        ShadowLooper.idleMainLooper()

        assertEquals(0, invokeCount)
    }

    @Test
    fun `detach from window stops layout driven invocations`() {
        val host = createHost()
        val activity = attachToActivity(host)
        var invokeCount = 0
        host.addCallback { invokeCount++ }

        host.composeView.requestLayout()
        ShadowLooper.idleMainLooper()
        assertEquals(1, invokeCount)

        val countAfterAttach = invokeCount
        activity.setContentView(View(activity))
        ShadowLooper.idleMainLooper()

        invokeCount = countAfterAttach
        host.composeView.requestLayout()
        ShadowLooper.idleMainLooper()

        assertEquals(1, invokeCount)
    }

    @Test
    fun `setContent with DivData loads images without preloadRequired`() {
        val imageUrl = "https://example.com/preload.jpg"
        createHostWithImageCapture().setContent(
            data = data(
                content = image(
                    imageUrl = imageUrl,
                    preloadRequired = false
                )
            ),
            preloadMode = PreloadMode.ACTIVE_STATE_ONLY,
        )
        composeRule.waitForIdle()

        assertEquals(setOf(imageUrl), capturedUrls)
    }

    @Test
    fun `setContent with new DivData reloads images`() {
        val firstUrl = "https://example.com/first.jpg"
        val secondUrl = "https://example.com/second.jpg"
        val host = createHostWithImageCapture()

        host.setContent(
            data = data(
                content = image(
                    imageUrl = firstUrl,
                    preloadRequired = false
                )
            ),
            preloadMode = PreloadMode.ACTIVE_STATE_ONLY,
        )
        composeRule.waitForIdle()

        assertEquals(setOf(firstUrl), capturedUrls)

        capturedUrls.clear()

        host.setContent(
            data = data(
                content = image(
                    imageUrl = secondUrl,
                    preloadRequired = false
                )
            ),
            preloadMode = PreloadMode.ACTIVE_STATE_ONLY,
        )
        composeRule.waitForIdle()

        assertEquals(setOf(secondUrl), capturedUrls)
    }

    @Test
    fun `setContent with DivData preloads when content wraps DivView`() {
        val imageUrl = "https://example.com/wrapped.jpg"
        val data = data(
            content = image(
                imageUrl = imageUrl,
                preloadRequired = false
            )
        )

        createHostWithImageCapture().setContent(
            data = data,
            preloadMode = PreloadMode.ACTIVE_STATE_ONLY
        ) {
            DivView(data = data)
        }
        composeRule.waitForIdle()

        assertEquals(setOf(imageUrl), capturedUrls)
    }

    @Test
    fun `setContent with DISABLED preloadMode does not start resource preload`() {
        createHostWithImageCapture().setContent(
            data = data(
                content = image(
                    imageUrl = "https://example.com/skip-preload.jpg",
                    preloadRequired = false
                )
            ),
            preloadMode = PreloadMode.DISABLED,
        ) { }
        composeRule.waitForIdle()

        assertEquals(emptySet(), capturedUrls)
    }

    @Test
    fun `setContent does not preload by default`() {
        createHostWithImageCapture().setContent(
            data(
                content = image(
                    imageUrl = "https://example.com/default-no-preload.jpg",
                    preloadRequired = false
                )
            )
        ) { }
        composeRule.waitForIdle()

        assertEquals(emptySet(), capturedUrls)
    }

    private fun createHost(): DivViewHost = DivViewHost(createDivContext())

    private fun createHostWithImageCapture(): DivViewHost {
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val divContext = DivContext(
            baseContext = activity,
            configuration = DivConfiguration(
                reporter = TestReporter(),
                imageLoaderConfiguration = imageLoaderConfiguration,
            ),
            debugConfiguration = DivDebugConfiguration(coroutineScope = testScope),
        )
        val host = DivViewHost(divContext)
        activity.setContentView(host.composeView)
        return host
    }

    private fun createDivContext(): DivContext {
        return DivContext(
            baseContext = ApplicationProvider.getApplicationContext(),
            configuration = DivConfiguration(reporter = TestReporter()),
            debugConfiguration = DivDebugConfiguration(),
        )
    }

    private fun attachToActivity(host: DivViewHost): ComponentActivity {
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        activity.setContentView(host.composeView)
        ShadowLooper.idleMainLooper()
        return activity
    }
}
