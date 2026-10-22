package com.yandex.div.core.tooltip

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class TooltipGeometryTest {

    @Test
    @Config(sdk = [Build.VERSION_CODES.R])
    fun `window size comes from current window metrics on Android R`() {
        val bounds = Rect(10, 20, 1090, 2420)
        val windowManager = mock<WindowManager> {
            on { currentWindowMetrics } doReturn WindowMetrics(bounds, WindowInsets.CONSUMED)
        }
        val context = mock<Context> {
            on { getSystemService(WindowManager::class.java) } doReturn windowManager
        }
        val view = mock<View> {
            on { getContext() } doReturn context
        }

        assertEquals(Point(1080, 2400), view.getWindowSize())
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun `window size comes from real display size before Android R`() {
        val display = mock<Display> {
            on { getRealSize(any()) } doAnswer { invocation ->
                invocation.getArgument<Point>(0).set(1080, 2400)
                Unit
            }
        }
        val view = mock<View> {
            on { getDisplay() } doReturn display
        }

        assertEquals(Point(1080, 2400), view.getWindowSize())
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.Q])
    fun `window size falls back to display metrics when view has no display`() {
        val metrics = DisplayMetrics().apply {
            widthPixels = 1080
            heightPixels = 2400
        }
        val resources = mock<Resources> {
            on { displayMetrics } doReturn metrics
        }
        val view = mock<View> {
            on { getDisplay() } doReturn null
            on { getResources() } doReturn resources
        }

        assertEquals(Point(1080, 2400), view.getWindowSize())
    }
}
