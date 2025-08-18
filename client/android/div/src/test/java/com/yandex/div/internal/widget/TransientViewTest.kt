package com.yandex.div.internal.widget

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransientViewTest {

    private val context = Robolectric.buildActivity(Activity::class.java).get()

    @Test
    fun `transient view is not in transient hierarchy when it is not in transient state`() {
        val view = TransientViewTestImpl(context)

        assertFalse(view.isInTransientHierarchy())
    }

    @Test
    fun `transient view is in transient hierarchy when it is in transient state`() {
        val view = TransientViewTestImpl(context)

        view.transitionStarted(view)

        assertTrue(view.isInTransientHierarchy())
    }

    @Test
    fun `transient view is in transient hierarchy when one of ascendants is in transient state`() {
        val view = TransientViewTestImpl(context)
        val innerViewGroup = TransientViewGroup(context).apply {
            addView(view)
        }
        val outerViewGroup = TransientViewGroup(context).apply {
            addView(innerViewGroup)
        }

        outerViewGroup.transitionStarted(view)

        assertTrue(view.isInTransientHierarchy())
    }

    @Test
    fun `non transient view breaks transient hierarchy`() {
        val view = TransientViewTestImpl(context)
        val innerViewGroup = NonTransientViewGroup(context).apply {
            addView(view)
        }
        val outerViewGroup = TransientViewGroup(context).apply {
            addView(innerViewGroup)
        }

        outerViewGroup.transitionStarted(view)

        assertFalse(view.isInTransientHierarchy())
    }
}

private class TransientViewTestImpl(
    context: Context
) : View(context), TransientView by TransientViewMixin()

private class TransientViewGroup(
    context: Context
) : ViewGroup(context), TransientView by TransientViewMixin() {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = Unit
}

private class NonTransientViewGroup(
    context: Context
) : ViewGroup(context) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) = Unit
}
