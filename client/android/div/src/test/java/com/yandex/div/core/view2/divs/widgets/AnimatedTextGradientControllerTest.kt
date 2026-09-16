package com.yandex.div.core.view2.divs.widgets

import android.app.Activity
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RadialGradient
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.RelativeSizeSpan
import android.view.Choreographer
import android.view.Gravity
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import com.yandex.div.core.util.AnimatedTextGradientMath
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.graphics.Colormap
import com.yandex.div.internal.spannable.TextColorSpan
import kotlin.math.ceil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AnimatedTextGradientControllerTest {

    private val context = Robolectric.buildActivity(Activity::class.java).get()

    @Test
    fun `static animated gradient applies shader`() {
        val (view, controller) = createController()

        controller.update(data(), animationsEnabled = false)

        assertTrue(view.paint.shader is LinearGradient)
    }

    @Test
    fun `zero duration keeps static gradient`() {
        val (view, controller) = createVisibleController()
        controller.update(data(duration = 0L), animationsEnabled = true)

        controller.doFrame(FRAME_TIME_NANOS)

        assertEquals(0f, view.shaderTranslationX(), 0.01f)
    }

    @Test
    fun `zero duration stops animation frames`() {
        val (_, controller, choreographer) = createVisibleController()

        controller.update(data(duration = 0L), animationsEnabled = true)

        verify(choreographer).removeFrameCallback(controller)
    }

    @Test
    fun `positive duration resumes animation after zero duration`() {
        val (_, controller, choreographer) = createVisibleController()
        controller.update(data(duration = 0L), animationsEnabled = true)
        clearInvocations(choreographer)

        controller.update(data(), animationsEnabled = true)

        verify(choreographer).postFrameCallback(controller)
    }

    @Test
    fun `animation frame applies exact linear translation`() {
        val (view, controller) = createVisibleController()
        controller.update(data(), animationsEnabled = true)

        controller.doFrame(FRAME_TIME_NANOS)

        assertEquals(
            -ceil(view.paint.measureText(view.text.toString())).toFloat() / 2f,
            view.shaderTranslationX(),
            0.01f,
        )
    }

    @Test
    fun `next animation frame reuses shader and updates translation`() {
        val (view, controller) = createVisibleController()
        val shader = view.paint.shader
        controller.doFrame(FRAME_TIME_NANOS)

        controller.doFrame(3 * FRAME_TIME_NANOS)

        assertSame(shader, view.paint.shader)
        assertEquals(ceil(view.paint.measureText("animated")) / 2f, view.shaderTranslationX(), 0.01f)
    }

    @Test
    fun `linear translation overwrites the supplied point`() {
        val point = PointF(12f, 34f)

        val result = AnimatedTextGradientMath.linearTranslation(
            angleDegrees = 90f, width = 200f, height = 40f, phase = 0.25f, result = point,
        )

        assertSame(point, result)
        assertEquals(0f, point.x, 0.01f)
        assertEquals(20f, point.y, 0.01f)
    }

    @Test
    fun `hiding window removes animation frame callback`() {
        val (view, controller, choreographer) = createVisibleController()

        doReturn(View.GONE).whenever(view).windowVisibility
        controller.onVisibilityChanged()

        verify(choreographer).removeFrameCallback(controller)
    }

    @Test
    fun `showing window resumes animation frame callback`() {
        val (view, controller, choreographer) = createVisibleController()
        doReturn(View.GONE).whenever(view).windowVisibility
        controller.onVisibilityChanged()
        clearInvocations(choreographer)

        doReturn(View.VISIBLE).whenever(view).windowVisibility
        controller.onVisibilityChanged()

        verify(choreographer).postFrameCallback(controller)
    }

    @Test
    fun `centered static gradient starts at text left edge`() {
        val (view, controller) = createAlignedController(Gravity.CENTER_HORIZONTAL)

        controller.update(data(), animationsEnabled = false)

        assertEquals(view.layout.getLineLeft(0), view.shaderTranslationX(), 0.01f)
    }

    @Test
    fun `right aligned radial gradient starts at text left edge`() {
        val (view, controller) = createAlignedController(Gravity.RIGHT)

        controller.update(data(gradient = radialGradient()), animationsEnabled = false)

        assertEquals(view.layout.getLineLeft(0), view.shaderTranslationX(), 0.01f)
    }

    @Test
    fun `centered animation moves relative to text left edge`() {
        val alignedView = createAlignedController(Gravity.CENTER_HORIZONTAL).first
        val (view, controller) = createVisibleController(alignedView)
        controller.update(data(), animationsEnabled = true)

        controller.doFrame(FRAME_TIME_NANOS)

        assertEquals(
            view.layout.getLineLeft(0) - ceil(view.layout.getLineRight(0) - view.layout.getLineLeft(0)) / 2f,
            view.shaderTranslationX(),
            0.01f,
        )
    }

    @Test
    fun `right aligned radial animation moves relative to text left edge`() {
        val alignedView = createAlignedController(Gravity.RIGHT).first
        val (view, controller) = createVisibleController(alignedView)
        controller.update(data(gradient = radialGradient()), animationsEnabled = true)

        controller.doFrame(FRAME_TIME_NANOS)

        assertEquals(
            view.layout.getLineLeft(0) - ceil(view.layout.getLineRight(0) - view.layout.getLineLeft(0)) / 2f,
            view.shaderTranslationX(),
            0.01f,
        )
    }

    @Test
    fun `vertical animated gradient keeps shader matrix finite`() {
        val (view, controller) = createVisibleController()
        controller.update(data(gradient = linearGradient(angle = 90f)), animationsEnabled = true)

        controller.doFrame(FRAME_TIME_NANOS)

        assertTrue(view.paint.shader?.matrixValues()?.all(Float::isFinite) == true)
    }

    @Test
    fun `radial animated gradient keeps shader type and applies exact translation`() {
        val (view, controller) = createVisibleController()
        controller.update(data(gradient = radialGradient()), animationsEnabled = true)
        val shader = view.paint.shader ?: error("Expected animated shader")

        controller.doFrame(FRAME_TIME_NANOS)

        assertTrue(shader is RadialGradient)
        assertEquals(
            -ceil(view.paint.measureText(view.text.toString())).toFloat() / 2f,
            view.shaderTranslationX(),
            0.01f,
        )
    }

    @Test
    fun `disabled animations use identity transform`() {
        val (view, controller) = createController()

        controller.update(data(), animationsEnabled = false)

        assertTrue(view.paint.shader?.matrixValues()?.contentEquals(Matrix().values()) == true)
    }

    @Test
    fun `disabling running animation restores identity transform`() {
        val (view, controller) = createVisibleController()
        controller.update(data(), animationsEnabled = true)
        controller.doFrame(FRAME_TIME_NANOS)

        controller.setAnimationsEnabled(false)

        assertTrue(view.paint.shader?.matrixValues()?.contentEquals(Matrix().values()) == true)
    }

    @Test
    fun `resize rebuilds shader for new text bounds`() {
        val (view, controller) = createController()
        controller.update(data(), animationsEnabled = false)
        val initialShader = view.paint.shader
        view.text = "animated ".repeat(10)
        view.layout(0, 0, 300, 80)

        controller.invalidate()

        assertFalse(initialShader === view.paint.shader)
    }

    @Test
    fun `text metrics change rebuilds static shader with fixed bounds`() {
        val view = DivLineHeightTextView(context).apply {
            text = "animated"
            measureAndLayout(200, 80)
            setAnimatedTextGradient(data(), animationsEnabled = false)
        }
        val initialShader = view.paint.shader
        view.text = SpannableString("animated").apply {
            setSpan(RelativeSizeSpan(2f), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        view.measureAndLayout(200, 80)

        assertFalse(initialShader === view.paint.shader)
    }

    @Test
    fun `animation translation follows rich multiline layout`() {
        val view = DivLineHeightTextView(context).apply {
            text = SpannableString("small\nwide line").apply {
                setSpan(RelativeSizeSpan(1.5f), 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            measureAndLayout(200, 120)
        }
        val expectedWidth = (0 until view.layout.lineCount)
            .maxOf { line -> ceil(view.layout.getLineWidth(line)).toInt() }

        val (animatedView, controller) = createVisibleController(view)

        controller.doFrame(FRAME_TIME_NANOS)

        assertEquals(-expectedWidth / 2f, animatedView.shaderTranslationX(), 0.01f)
    }

    @Test
    fun `animation spans left and right aligned paragraphs`() {
        val view = DivLineHeightTextView(context).apply {
            text = "short\nשלום"
            textDirection = View.TEXT_DIRECTION_FIRST_STRONG
        }

        view.measureAndLayout(300, 120)
        val (animatedView, controller) = createVisibleController(view)

        controller.doFrame(FRAME_TIME_NANOS)

        assertEquals(-150f, animatedView.shaderTranslationX(), 0.01f)
    }

    @Test
    fun `detach clears shader and attach restores it`() {
        val (view, controller) = createController()
        controller.update(data(), animationsEnabled = true)

        controller.onDetachedFromWindow()

        assertNull(view.paint.shader)

        controller.onAttachedToWindow()

        assertTrue(view.paint.shader is LinearGradient)
    }

    @Test
    fun `phase is synchronized by frame time and clamps duration to one millisecond`() {
        assertEquals(0.25f, AnimatedTextGradientMath.phase(400L, 1600L))
        assertEquals(
            AnimatedTextGradientMath.phase(400L, 1600L),
            AnimatedTextGradientMath.phase(2000L, 1600L),
        )
        assertEquals(0f, AnimatedTextGradientMath.phase(400L, 0L))
    }

    @Test
    fun `release clears shader`() {
        val (view, controller) = createController()
        controller.update(data(), animationsEnabled = true)

        controller.release()

        assertNull(view.paint.shader)
    }

    @Test
    fun `range text color clears animated gradient locally`() {
        val paint = TextPaint().apply {
            shader = LinearGradient(0f, 0f, 100f, 0f, Color.RED, Color.BLUE, android.graphics.Shader.TileMode.CLAMP)
        }

        TextColorSpan(Color.GREEN).updateDrawState(paint)

        assertNull(paint.shader)
        assertEquals(Color.GREEN, paint.color)
    }

    private fun createController(): Pair<DivLineHeightTextView, AnimatedTextGradientController> {
        val view = DivLineHeightTextView(context).apply {
            text = "animated"
            layout(0, 0, 200, 80)
        }
        return view to AnimatedTextGradientController(view)
    }

    private fun createVisibleController(
        sourceView: DivLineHeightTextView = createController().first,
    ): Triple<DivLineHeightTextView, AnimatedTextGradientController, Choreographer> {
        val view = spy(sourceView)
        doReturn(true).whenever(view).isShown
        doReturn(View.VISIBLE).whenever(view).windowVisibility
        val choreographer = mock<Choreographer>()
        val controller = AnimatedTextGradientController(view, choreographer)
        controller.update(data(), animationsEnabled = true)
        return Triple(view, controller, choreographer)
    }

    private fun createAlignedController(gravity: Int): Pair<DivLineHeightTextView, AnimatedTextGradientController> {
        val view = DivLineHeightTextView(context).apply {
            text = "short"
            this.gravity = gravity
            measureAndLayout(300, 80)
        }
        return view to AnimatedTextGradientController(view)
    }

    private fun DivLineHeightTextView.shaderTranslationX(): Float {
        return requireNotNull(paint.shader).matrixValues()[Matrix.MTRANS_X]
    }

    private fun data(
        gradient: AnimatedTextGradientData.Gradient = linearGradient(),
        duration: Long = 1600L,
    ) = AnimatedTextGradientData(
        gradient = gradient,
        duration = duration,
    )

    private fun linearGradient(angle: Float = 0f) = AnimatedTextGradientData.Gradient.Linear(
        colormap = colormap(),
        angle = angle,
    )

    private fun radialGradient() = AnimatedTextGradientData.Gradient.Radial(
        colormap = colormap(),
        radius = RadialGradientDrawable.Radius.Relative(
            RadialGradientDrawable.Radius.Relative.Type.FARTHEST_CORNER,
        ),
        centerX = RadialGradientDrawable.Center.Relative(0.5f),
        centerY = RadialGradientDrawable.Center.Relative(0.5f),
    )

    private fun colormap() = Colormap(
        colors = intArrayOf(Color.RED, Color.GREEN, Color.BLUE),
        positions = floatArrayOf(0f, 0.5f, 1f),
    )

    private fun android.graphics.Shader.matrixValues(): FloatArray {
        return Matrix().also(::getLocalMatrix).values()
    }

    private fun Matrix.values(): FloatArray {
        return FloatArray(9).also(::getValues)
    }

    private fun DivLineHeightTextView.measureAndLayout(width: Int, height: Int) {
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(width, height)
        }
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
        )
        layout(0, 0, width, height)
    }

    private companion object {
        const val FRAME_TIME_NANOS = 400L * 1_000_000L
    }
}
