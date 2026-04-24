package com.yandex.div.core.view2.divs

import android.view.View
import android.widget.FrameLayout
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class BaseDivViewExtensionsClickTest {

    private val context = RuntimeEnvironment.application

    @Test
    fun `performClickOnAncestors triggers click on nearest clickable parent`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val root = FrameLayout(context).apply { addView(parent) }

        var parentClicked = false
        parent.setOnClickListener { parentClicked = true }

        val result = child.performClickOnAncestors()

        assertTrue(result)
        assertTrue(parentClicked)
    }

    @Test
    fun `performClickOnAncestors skips non-clickable parent and triggers on grandparent`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val grandparent = FrameLayout(context).apply { addView(parent) }
        val root = FrameLayout(context).apply { addView(grandparent) }

        var grandparentClicked = false
        grandparent.setOnClickListener { grandparentClicked = true }

        val result = child.performClickOnAncestors()

        assertTrue(result)
        assertTrue(grandparentClicked)
    }

    @Test
    fun `performClickOnAncestors returns false when no ancestor is clickable`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val root = FrameLayout(context).apply { addView(parent) }

        val result = child.performClickOnAncestors()

        assertFalse(result)
    }

    @Test
    fun `performClickOnAncestors returns false when view has no parent`() {
        val orphan = View(context)

        val result = orphan.performClickOnAncestors()

        assertFalse(result)
    }

    @Test
    fun `performLongClickOnAncestors triggers long click on nearest long-clickable parent`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val root = FrameLayout(context).apply { addView(parent) }

        var parentLongClicked = false
        parent.setOnLongClickListener { parentLongClicked = true; true }

        val result = child.performLongClickOnAncestors()

        assertTrue(result)
        assertTrue(parentLongClicked)
    }

    @Test
    fun `performLongClickOnAncestors skips non-long-clickable parent and triggers on grandparent`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val grandparent = FrameLayout(context).apply { addView(parent) }
        val root = FrameLayout(context).apply { addView(grandparent) }

        var grandparentLongClicked = false
        grandparent.setOnLongClickListener { grandparentLongClicked = true; true }

        val result = child.performLongClickOnAncestors()

        assertTrue(result)
        assertTrue(grandparentLongClicked)
    }

    @Test
    fun `performLongClickOnAncestors returns false when no ancestor is long-clickable`() {
        val child = View(context)
        val parent = FrameLayout(context).apply { addView(child) }
        val root = FrameLayout(context).apply { addView(parent) }

        val result = child.performLongClickOnAncestors()

        assertFalse(result)
    }
}
