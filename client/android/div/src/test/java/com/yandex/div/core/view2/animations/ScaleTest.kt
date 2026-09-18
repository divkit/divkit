package com.yandex.div.core.view2.animations

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.transition.TransitionValues
import androidx.transition.Visibility
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

@RunWith(AndroidJUnit4::class)
class ScaleTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val sceneRoot =
        FrameLayout(context).apply {
            layout(0, 0, ROOT_SIZE, ROOT_SIZE)
        }

    @Test
    fun `disappearing overlay starts with captured scale before animator runs`() {
        val startView = createView(scaleX = 0.85f, scaleY = 0.75f)
        val endView = createView()
        val transition = Scale(scaleFactor = 0.5f).apply { mode = Visibility.MODE_OUT }
        val startValues = TransitionValues(startView).also(transition::captureStartValues)
        val animator = transition.onDisappear(sceneRoot, endView, startValues, null) as ObjectAnimator
        val overlayView = animator.target as View

        assertNotSame(endView, overlayView)
        assertEquals(ScaleState(View.VISIBLE, 0.85f, 0.75f), overlayView.scaleState)
    }

    @Test
    fun `disappearing overlay restores its scale when animator ends`() {
        val startView = createView(scaleX = 0.85f, scaleY = 0.75f)
        val endView = createView()
        val transition = Scale(scaleFactor = 0.5f).apply { mode = Visibility.MODE_OUT }
        val startValues = TransitionValues(startView).also(transition::captureStartValues)
        val animator = transition.onDisappear(sceneRoot, endView, startValues, null) as ObjectAnimator
        val overlayView = animator.target as View
        animator.duration = ANIMATION_DURATION_MS

        animator.start()
        animator.end()

        assertEquals(1f to 1f, overlayView.scaleX to overlayView.scaleY)
    }

    @Test
    fun `appearing view is hidden before animator starts`() {
        val view = createView()
        val transition = Scale(scaleFactor = 0.5f).apply { mode = Visibility.MODE_IN }
        val endValues = TransitionValues(view).also(transition::captureEndValues)

        val animator = transition.onAppear(sceneRoot, view, null, endValues) as ObjectAnimator

        assertEquals(View.INVISIBLE, (animator.target as View).visibility)
    }

    @Test
    fun `appearing view becomes visible at start scale when animator starts`() {
        val view = createView()
        val transition = Scale(scaleFactor = 0.5f).apply { mode = Visibility.MODE_IN }
        val endValues = TransitionValues(view).also(transition::captureEndValues)
        val animator = transition.onAppear(sceneRoot, view, null, endValues) as ObjectAnimator
        animator.duration = ANIMATION_DURATION_MS

        animator.start()
        animator.currentPlayTime = 0L

        assertEquals(ScaleState(View.VISIBLE, 0.5f, 0.5f), (animator.target as View).scaleState)
    }

    @Test
    fun `view scale is restored when animator with custom pivot ends`() {
        val view = createView(scaleX = 1.2f, scaleY = 0.8f)
        val transition =
            Scale(scaleFactor = 0.5f, pivotX = 0.25f, pivotY = 0.75f)
                .apply { mode = Visibility.MODE_OUT }
        val startValues = TransitionValues(view).also(transition::captureStartValues)
        val animator = transition.onDisappear(sceneRoot, view, startValues, null)!!
        animator.duration = ANIMATION_DURATION_MS

        animator.start()
        animator.end()

        assertEquals(1.2f to 0.8f, view.scaleX to view.scaleY)
    }

    private fun createView(
        scaleX: Float = 1f,
        scaleY: Float = 1f,
    ): View =
        View(context).apply {
            layoutParams = ViewGroup.LayoutParams(VIEW_WIDTH, VIEW_HEIGHT)
            this.scaleX = scaleX
            this.scaleY = scaleY
            sceneRoot.addView(this)
            layout(0, 0, VIEW_WIDTH, VIEW_HEIGHT)
        }

    private val View.scaleState: ScaleState
        get() = ScaleState(visibility, scaleX, scaleY)

    private data class ScaleState(
        val visibility: Int,
        val scaleX: Float,
        val scaleY: Float,
    )

    private companion object {
        private const val ROOT_SIZE = 200
        private const val VIEW_WIDTH = 100
        private const val VIEW_HEIGHT = 80
        private const val ANIMATION_DURATION_MS = 1_000L
    }
}
