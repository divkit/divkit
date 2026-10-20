package com.yandex.div.core.view2.animations

import android.app.Activity
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.createAnimatedTouchListener
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAnimation
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class TouchAnimationBindingTest {

    private val controller = Robolectric.buildActivity(Activity::class.java).setup()
    private val context = controller.get()
    private val root = FrameLayout(context)
    private val content = View(context)
    private val overlay = FrameLayout(context)
    private val resolver = mock<ExpressionResolver>()
    private var contentClicks = 0
    private var overlayClicks = 0
    private var eventTime = 0L

    @BeforeTest
    fun setUp() {
        content.setOnClickListener { contentClicks++ }
        overlay.setOnClickListener { overlayClicks++ }
        root.addView(content, FrameLayout.LayoutParams(200, 200))
        root.addView(overlay, FrameLayout.LayoutParams(200, 200))
        context.setContentView(root)
        root.measure(
            View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY),
        )
        root.layout(0, 0, 200, 200)
        bind()
    }

    @AfterTest
    fun tearDown() {
        controller.pause().stop().destroy()
    }

    @Test
    fun `up after hiding overlay leaves fresh taps available to content`() {
        dispatch(MotionEvent.ACTION_DOWN)
        overlay.visibility = View.GONE
        overlay.clearAnimation()
        dispatch(MotionEvent.ACTION_UP)
        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)
        shadowOf(Looper.getMainLooper()).idle()

        assertNull(overlay.animation)
        assertEquals(1, contentClicks)
    }

    @Test
    fun `cancel after hiding overlay does not restart animation`() {
        dispatch(MotionEvent.ACTION_DOWN)
        overlay.visibility = View.INVISIBLE
        overlay.clearAnimation()

        dispatch(MotionEvent.ACTION_CANCEL)

        assertNull(overlay.animation)
    }

    @Test
    fun `hidden ancestor prevents late up from starting child animation`() {
        dispatch(MotionEvent.ACTION_DOWN)
        root.visibility = View.GONE

        dispatch(MotionEvent.ACTION_UP)

        assertNull(overlay.animation)
    }

    @Test
    fun `visibility cancellation invalidates gesture even if ancestor is shown before up`() {
        dispatch(MotionEvent.ACTION_DOWN)
        root.cancelTouchAnimations()
        root.visibility = View.GONE
        root.visibility = View.VISIBLE

        dispatch(MotionEvent.ACTION_UP)

        assertNull(overlay.animation)
    }

    @Test
    fun `rebind between down and up clears old animation without completing new gesture`() {
        dispatch(MotionEvent.ACTION_DOWN)
        bind()

        dispatch(MotionEvent.ACTION_UP)

        assertNull(overlay.animation)
    }

    @Test
    fun `removing animation binding clears owned animation`() {
        dispatch(MotionEvent.ACTION_DOWN)

        val listener = overlay.createAnimatedTouchListener(
            null, null, resolver, mock(), preserveGesture = false,
        )

        assertNull(overlay.animation)
        assertNull(listener)
    }

    @Test
    fun `disabling animations during gesture clears retained touch animation`() {
        val divView = mock<Div2View>(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
        val enabledController = divView.div2Component.animationsEnabledController
        whenever(enabledController.isEnabled()) doReturn true
        overlay.setOnTouchListener(overlay.createAnimatedTouchListener(
            DEFAULT_CLICK_ANIMATION, null, resolver, divView, preserveGesture = false,
        ))
        dispatch(MotionEvent.ACTION_DOWN)
        val directAnimation = overlay.animation
        whenever(enabledController.isEnabled()) doReturn false

        dispatch(MotionEvent.ACTION_UP)

        assertNotNull(directAnimation)
        assertNull(overlay.animation)
    }

    @Test
    fun `detach clears owned animation and reattach accepts a fresh gesture`() {
        dispatch(MotionEvent.ACTION_DOWN)
        root.removeView(overlay)
        root.clearDisappearingChildren()
        val wasAttached = overlay.isAttachedToWindow
        val detachedAnimation = overlay.animation
        root.addView(overlay, FrameLayout.LayoutParams(200, 200))
        overlay.layout(0, 0, 200, 200)

        dispatch(MotionEvent.ACTION_DOWN)

        assertFalse(wasAttached)
        assertNull(detachedAnimation)
        assertNotNull(overlay.animation)
    }

    @Test
    fun `detach and reattach do not revive the old gesture`() {
        dispatch(MotionEvent.ACTION_DOWN)
        root.removeView(overlay)
        root.clearDisappearingChildren()
        val wasAttached = overlay.isAttachedToWindow
        root.addView(overlay, FrameLayout.LayoutParams(200, 200))
        overlay.layout(0, 0, 200, 200)

        dispatch(MotionEvent.ACTION_UP, overlay)

        assertFalse(wasAttached)
        assertNull(overlay.animation)
    }

    @Test
    fun `cancelling binding preserves unrelated animation`() {
        dispatch(MotionEvent.ACTION_DOWN)
        val unrelated = AlphaAnimation(0f, 1f)
        overlay.startAnimation(unrelated)

        overlay.cancelTouchAnimations()

        assertSame(unrelated, overlay.animation)
    }

    @Test
    fun `visible up preserves reverse animation fill after and regular click`() {
        dispatch(MotionEvent.ACTION_DOWN)
        val directAnimation = overlay.animation

        dispatch(MotionEvent.ACTION_UP)
        shadowOf(Looper.getMainLooper()).idle()

        assertNotNull(directAnimation)
        assertNotNull(overlay.animation)
        assertFalse(directAnimation === overlay.animation)
        assertTrue(overlay.animation.fillAfter)
        assertEquals(1, overlayClicks)
        assertEquals(0, contentClicks)
    }

    @Test
    fun `visible cancel preserves reverse animation without click`() {
        dispatch(MotionEvent.ACTION_DOWN)
        val directAnimation = overlay.animation

        dispatch(MotionEvent.ACTION_CANCEL)
        shadowOf(Looper.getMainLooper()).idle()

        assertNotNull(overlay.animation)
        assertFalse(directAnimation === overlay.animation)
        assertEquals(0, overlayClicks)
    }

    @Test
    fun `scale animation remains available for visible taps`() {
        bind(DivAnimation(
            name = Expression.constant(DivAnimation.Name.SCALE),
            duration = Expression.constant(340L),
            startDelay = Expression.constant(25L),
        ))

        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)

        assertTrue(overlay.animation is ScaleAnimation)
        assertEquals(340L, overlay.animation.duration)
        assertEquals(25L, overlay.animation.startOffset)
        assertTrue(overlay.animation.fillAfter)
    }

    @Test
    fun `composed animation remains available for visible taps`() {
        bind(DivAnimation(
            name = Expression.constant(DivAnimation.Name.SET),
            items = listOf(
                DEFAULT_CLICK_ANIMATION,
                DivAnimation(
                    name = Expression.constant(DivAnimation.Name.SCALE),
                    duration = Expression.constant(340L),
                ),
            ),
        ))

        dispatch(MotionEvent.ACTION_DOWN)
        dispatch(MotionEvent.ACTION_UP)

        val animation = overlay.animation as AnimationSet
        assertEquals(2, animation.animations.size)
        assertTrue(animation.animations[0] is AlphaAnimation)
        assertTrue(animation.animations[1] is ScaleAnimation)
        assertEquals(100L, animation.animations[0].duration)
        assertEquals(340L, animation.animations[1].duration)
        assertTrue(overlay.animation.fillAfter)
    }

    private fun bind(animation: DivAnimation = DEFAULT_CLICK_ANIMATION) {
        val listener = animation.asTouchListener(resolver, overlay, preserveGesture = false)
        overlay.setOnTouchListener { view, event ->
            listener?.onTouch(view, event)
            false
        }
    }

    private fun dispatch(action: Int, target: View = root) {
        eventTime += 10
        val event = MotionEvent.obtain(0L, eventTime, action, 50f, 50f, 0)
        target.dispatchTouchEvent(event)
        event.recycle()
    }
}
