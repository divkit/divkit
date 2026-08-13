package com.yandex.div.lottie

import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.AsyncUpdates
import com.yandex.div.core.widget.LoadableImageView
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LottieControllerAsyncUpdatesTest {

    private fun imageView() = LoadableImageView(ApplicationProvider.getApplicationContext())

    @Test
    fun `async updates are enabled by default`() {
        val controller = LottieController(imageView())

        Assert.assertEquals(AsyncUpdates.ENABLED, controller.getAsyncUpdates())
    }

    @Test
    fun `async updates are explicitly disabled when opted out`() {
        val controller = LottieController(imageView(), asyncUpdatesEnabled = false)

        Assert.assertEquals(AsyncUpdates.DISABLED, controller.getAsyncUpdates())
    }
}
