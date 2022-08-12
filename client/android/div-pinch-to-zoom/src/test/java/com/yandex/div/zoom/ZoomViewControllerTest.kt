package com.yandex.div.zoom

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZoomViewControllerTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    private val decorView = FrameLayout(activity)

    private val zoomView: View?
        get() = decorView.getChildAt(0)

    private val window = mock<Window> {
        on { decorView } doReturn decorView
    }

    private val configuration = DivPinchToZoomConfiguration.Builder(activity)
        .host(window)
        .dimColor(DIM_COLOR)
        .build()

    private val viewController = ZoomViewController(configuration)

    @Test
    fun `controller is in active state after showImage called`() {
        viewController.showImage(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f),
            imageBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        )

        assertEquals(ZoomState.ACTIVE, viewController.state)
    }

    @Test
    fun `controller is in exiting state after hideImage with animation called`() {
        viewController.showImage(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f),
            imageBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        )

        viewController.hideImage(animated = true) {  }

        assertEquals(ZoomState.EXITING, viewController.state)
    }

    @Test
    fun `controller is in exiting idle after hideImage without animation called`() {
        viewController.showImage(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f),
            imageBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        )

        viewController.hideImage(animated = false) { }

        assertEquals(ZoomState.IDLE, viewController.state)
    }

    @Test
    fun `zoom view removed from decor view`() {
        viewController.showImage(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f),
            imageBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        )

        viewController.hideImage(animated = false) { }

        assertEquals(0, decorView.childCount)
    }

    @Test
    fun `dim color is transparent when there is no scale`() {
        viewController.showImage(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f),
            imageBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        )

        val backgroundColor = (zoomView?.background as? ColorDrawable)?.color
        assertEquals(Color.TRANSPARENT, backgroundColor)
    }

    @Test
    fun `dim color is blended at 1,5x scale factor`() {
        viewController.showImage(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f),
            imageBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        )

        viewController.scaleImageBy(1.5f)

        val backgroundColor = (zoomView?.background as? ColorDrawable)?.color
        assertEquals(0x80000000.toInt(), backgroundColor)
    }

    @Test
    fun `dim color is fully applied at 2x scale factor`() {
        viewController.showImage(
            location = PointF(0.0f, 0.0f),
            pivotPoint = PointF(20.0f, 20.0f),
            imageBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        )

        viewController.scaleImageBy(2.0f)

        val backgroundColor = (zoomView?.background as? ColorDrawable)?.color
        assertEquals(DIM_COLOR, backgroundColor)
    }

    private companion object {
        private const val DIM_COLOR = 0xFF000000.toInt()
    }
}
