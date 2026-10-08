package com.yandex.div.core.tooltip

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class TooltipUpdateEventsTest {

    private val activityController = Robolectric.buildActivity(Activity::class.java).setup()
    private val activity = activityController.get()
    private val rootView = FrameLayout(activity)
    private val anchor = View(activity)
    private val underTest = TooltipUpdateEvents(
        rootView = rootView,
        anchor = anchor,
        handler = Handler(Looper.getMainLooper()),
    )

    init {
        rootView.addView(anchor)
        activity.setContentView(rootView)
        rootView.layout(0, 0, 500, 1_000)
        anchor.layout(100, 200, 300, 300)
    }

    @After
    fun tearDown() {
        activityController.pause().stop().destroy()
    }

    @Test
    fun `root size change requests position update`() {
        var updateCount = 0
        val subscription = underTest.subscribeToUpdates { updateCount++ }

        rootView.layout(0, 0, 700, 300)

        assertEquals(1, updateCount)
        subscription.close()
    }

    @Test
    fun `anchor movement requests position update`() {
        var updateCount = 0
        val subscription = underTest.subscribeToAnchorUpdates { updateCount++ }

        anchor.layout(150, 200, 350, 300)

        assertEquals(1, updateCount)
        subscription.close()
    }

    @Test
    fun `configuration change restarts expired anchor tracking`() {
        var recomputeCount = 0
        val updateCoordinator = TooltipUpdateCoordinator(underTest) {
            recomputeCount++
        }
        updateCoordinator.start()
        ShadowLooper.idleMainLooper(ANCHOR_TRACKING_DURATION_MS + 1, TimeUnit.MILLISECONDS)

        updateCoordinator.onConfigurationChanged()
        val recomputeCountAfterConfigurationChange = recomputeCount
        anchor.layout(150, 200, 350, 300)

        assertTrue(recomputeCount > recomputeCountAfterConfigurationChange)
        updateCoordinator.stop()
    }
}
