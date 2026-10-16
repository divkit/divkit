package com.yandex.div.core.view2.divs.widgets

import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.view2.divs.context
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class DivRecyclerViewTest {

    private val recyclerView = DivRecyclerView(context())

    @Test
    fun `restores default animator when animations are enabled again`() {
        val animator = requireNotNull(recyclerView.itemAnimator)
        recyclerView.setItemAnimationsEnabled(false)

        recyclerView.setItemAnimationsEnabled(true)

        assertSame(animator, recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animations disabled when client sets an animator`() {
        recyclerView.setItemAnimationsEnabled(false)

        recyclerView.itemAnimator = mock()

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `uses latest client animator when animations are enabled again`() {
        recyclerView.itemAnimator = mock()
        recyclerView.setItemAnimationsEnabled(false)
        val animator = mock<RecyclerView.ItemAnimator>()
        recyclerView.itemAnimator = animator

        recyclerView.setItemAnimationsEnabled(true)

        assertSame(animator, recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animator cleared by client when animations are enabled again`() {
        recyclerView.setItemAnimationsEnabled(false)
        recyclerView.itemAnimator = null

        recyclerView.setItemAnimationsEnabled(true)

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animations suppressed when client sets an animator before layout`() {
        recyclerView.suppressItemAnimationsUntilLayout()

        recyclerView.itemAnimator = mock()

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `uses latest client animator after layout`() {
        recyclerView.suppressItemAnimationsUntilLayout()
        val animator = mock<RecyclerView.ItemAnimator>()
        recyclerView.itemAnimator = animator

        recyclerView.layout(0, 0, 100, 100)

        assertSame(animator, recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animator cleared by client after layout`() {
        recyclerView.suppressItemAnimationsUntilLayout()
        recyclerView.itemAnimator = null

        recyclerView.layout(0, 0, 100, 100)

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animations suppressed when enabled before layout`() {
        recyclerView.setItemAnimationsEnabled(false)
        recyclerView.suppressItemAnimationsUntilLayout()

        recyclerView.setItemAnimationsEnabled(true)

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `keeps animations disabled after layout`() {
        recyclerView.suppressItemAnimationsUntilLayout()
        recyclerView.setItemAnimationsEnabled(false)

        recyclerView.layout(0, 0, 100, 100)

        assertNull(recyclerView.itemAnimator)
    }

    @Test
    fun `does not end animations when enablement stays unchanged`() {
        val animator = mock<RecyclerView.ItemAnimator>()
        recyclerView.itemAnimator = animator

        recyclerView.setItemAnimationsEnabled(true)

        verify(animator, never()).endAnimations()
    }

    @Test
    fun `ends animations when client explicitly sets the same animator`() {
        val animator = mock<RecyclerView.ItemAnimator>()
        recyclerView.itemAnimator = animator

        recyclerView.itemAnimator = animator

        verify(animator).endAnimations()
    }
}
