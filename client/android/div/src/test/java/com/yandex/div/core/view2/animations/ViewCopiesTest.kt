package com.yandex.div.core.view2.animations

import android.view.View
import androidx.transition.R as TransitionR
import com.yandex.div.R as DivR
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewCopiesTest {

    private val context = RuntimeEnvironment.application

    @Test
    fun `suppressOverlayVisibilityRestore sets override tag when overlay is active`() {
        val view = View(context)
        val overlayView = View(context)
        view.setTag(TransitionR.id.save_overlay_view, overlayView)

        view.suppressOverlayVisibilityRestore()

        val tag = view.getTag(DivR.id.div_transition_visibility_overridden) as? Boolean
        assertTrue("Override tag should be set to true when overlay is active", tag == true)
    }

    @Test
    fun `suppressOverlayVisibilityRestore does nothing when no overlay is active`() {
        val view = View(context)

        view.suppressOverlayVisibilityRestore()

        val tag = view.getTag(DivR.id.div_transition_visibility_overridden)
        assertNull("Override tag should remain null when overlay is not active", tag)
    }

    @Test
    fun `suppressOverlayVisibilityRestore is idempotent when called multiple times`() {
        val view = View(context)
        val overlayView = View(context)
        view.setTag(TransitionR.id.save_overlay_view, overlayView)

        view.suppressOverlayVisibilityRestore()
        view.suppressOverlayVisibilityRestore()
        view.suppressOverlayVisibilityRestore()

        val tag = view.getTag(DivR.id.div_transition_visibility_overridden) as? Boolean
        assertTrue("Override tag should still be true after multiple calls", tag == true)
    }
}
