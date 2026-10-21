package com.yandex.div.core.util.binding

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class BindingRequestCancellationTest {
    private val events = mutableListOf<String>()
    private val request = BindingRequestCancellation({ events += "cancelled" }, { events += "released" })

    @Test
    fun `completion suppresses later cancellation`() {
        request.complete { events += "completed" }

        request.cancel()
        ShadowLooper.idleMainLooper()

        assertEquals(listOf("released", "completed"), events)
    }

    @Test
    fun `cancellation suppresses later completion`() {
        request.cancel()

        request.complete { events += "completed" }
        ShadowLooper.idleMainLooper()

        assertEquals(listOf("released", "cancelled"), events)
    }
}
