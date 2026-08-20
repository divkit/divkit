package com.yandex.div.backdrop

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OccludingViewsHiderTest {

    private val context: Context = Robolectric.buildActivity(Activity::class.java).get()
    private val hider = OccludingViewsHider()

    @Test
    fun `hide makes the views invisible`() {
        val view = View(context)

        hider.hide(listOf(view))

        assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun `restore puts back the visibility the view had`() {
        val view = View(context).apply { visibility = View.GONE }

        hider.hide(listOf(view))
        hider.restore()

        assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun `restore leaves a view taken over during the capture alone`() {
        val view = View(context)

        hider.hide(listOf(view))
        view.visibility = View.GONE
        hider.restore()

        assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun `restore puts back the views hidden before hiding failed`() {
        val view = View(context)
        val failing = FailingView(context)

        runCatching { hider.hide(listOf(view, failing)) }
        hider.restore()

        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun `a view that requests a layout when hidden is not hidden again`() {
        val view = LayoutRequestingView(context).also { layOut(it) }

        hider.hide(listOf(view))
        hider.restore()
        hider.hide(listOf(view))

        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun `a view that already awaited a layout is hidden again`() {
        val view = LayoutRequestingView(context).also {
            layOut(it)
            it.requestLayout()
        }

        hider.hide(listOf(view))
        hider.restore()
        hider.hide(listOf(view))

        assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun `a view whose parent requests a layout when it is hidden is not hidden again`() {
        val view = ParentLayoutRequestingView(context)
        val parent = FrameLayout(context).apply { addView(view) }
        layOut(parent)

        hider.hide(listOf(view))
        hider.restore()
        hider.hide(listOf(view))

        assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun `release forgets the views that were not hidden again`() {
        val view = LayoutRequestingView(context).also { layOut(it) }

        hider.hide(listOf(view))
        hider.restore()
        hider.release()
        hider.hide(listOf(view))

        assertEquals(View.INVISIBLE, view.visibility)
    }

    private fun layOut(view: View) {
        val spec = View.MeasureSpec.makeMeasureSpec(VIEW_SIZE, View.MeasureSpec.EXACTLY)
        view.measure(spec, spec)
        view.layout(0, 0, VIEW_SIZE, VIEW_SIZE)
    }

    /**
     * Behaves like [android.view.SurfaceView], which requests a layout on every visibility change.
     */
    private class LayoutRequestingView(context: Context) : View(context) {

        override fun setVisibility(visibility: Int) {
            super.setVisibility(visibility)
            requestLayout()
        }
    }

    /**
     * Stands in for a child of a parent running an [android.animation.LayoutTransition]: the
     * layout is requested on the parent, while the view's own flag stays clear.
     */
    private class ParentLayoutRequestingView(context: Context) : View(context) {

        override fun setVisibility(visibility: Int) {
            super.setVisibility(visibility)
            (parent as? View)?.requestLayout()
        }
    }

    private class FailingView(context: Context) : View(context) {

        override fun setVisibility(visibility: Int) {
            throw IllegalStateException("visibility change is not welcome here")
        }
    }

    private companion object {
        const val VIEW_SIZE = 100
    }
}
