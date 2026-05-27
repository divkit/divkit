package com.yandex.div.compose

import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.host.CheckVisibilityCallback
import com.yandex.div.compose.internal.DivDebugConfiguration
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.shadows.ShadowLooper
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewHostTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

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

    private fun createHost(): DivViewHost = DivViewHost(createDivContext())

    private fun createDivContext(): DivContext {
        return DivContext(
            baseContext = ApplicationProvider.getApplicationContext(),
            configuration = DivComposeConfiguration(reporter = TestReporter()),
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
