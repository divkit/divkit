package com.yandex.div.core.view2.animations

import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.transition.R as TransitionR
import com.yandex.div.R as DivR
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ViewCopiesTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun `suppressOverlayVisibilityRestore sets override tag when overlay is active`() {
        val view = View(context)
        // Simulate overlay being active (save_overlay_view tag is set)
        view.setTag(TransitionR.id.save_overlay_view, View(context))

        view.suppressOverlayVisibilityRestore()

        assertEquals(true, view.getTag(DivR.id.div_transition_visibility_overridden))
    }

    @Test
    fun `suppressOverlayVisibilityRestore does nothing when no overlay is active`() {
        val view = View(context)
        // No overlay tag set

        view.suppressOverlayVisibilityRestore()

        assertNull(view.getTag(DivR.id.div_transition_visibility_overridden))
    }

    @Test
    fun `suppressOverlayVisibilityRestore is idempotent when called multiple times`() {
        val view = View(context)
        view.setTag(TransitionR.id.save_overlay_view, View(context))

        view.suppressOverlayVisibilityRestore()
        view.suppressOverlayVisibilityRestore()

        assertEquals(true, view.getTag(DivR.id.div_transition_visibility_overridden))
    }
}
