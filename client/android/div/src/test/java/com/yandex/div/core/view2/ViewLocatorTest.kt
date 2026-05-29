package com.yandex.div.core.view2

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.internal.widget.FrameContainerLayout
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

private const val ID = "id"
private const val SCOPE_ID = "scope"

@RunWith(RobolectricTestRunner::class)
class ViewLocatorTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val context = Div2Context(activity, DivConfiguration.Builder(mock()).build())
    private val divView = Div2View(context)

    @Test
    fun `throw missing view error when no target view found without scope`() {
        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, null).exceptionOrNull()
        Assert.assertTrue((error as? ViewLocator.MissingTarget).isTargetError)
    }

    @Test
    fun `throw missing view error when no target view found with missing scope`() {
        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, SCOPE_ID).exceptionOrNull()
        Assert.assertTrue((error as? ViewLocator.MissingTarget).isTargetError)
    }

    @Test
    fun `throw missing view error when no target view found with correct scope`() {
        val scopeView = mock<ViewGroup> {
            on { tag } doReturn SCOPE_ID
        }
        divView.addView(scopeView)

        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, SCOPE_ID).exceptionOrNull()

        Assert.assertTrue((error as? ViewLocator.MissingTarget).isTargetError)
    }

    @Test
    fun `throw duplicate scope error when several views have scope id`() {
        val scopeView1 = mock<ViewGroup> {
            on { tag } doReturn SCOPE_ID
        }
        val scopeView2 = mock<ViewGroup> {
            on { tag } doReturn SCOPE_ID
        }
        divView.addView(scopeView1)
        divView.addView(scopeView2)

        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, SCOPE_ID).exceptionOrNull()

        Assert.assertTrue((error as? ViewLocator.DuplicateTarget).isScopeError)
    }

    @Test
    fun `throw duplicate scope error when several views have scope id on different levels`() {
        val scopeView1 = mock<ViewGroup> {
            on { tag } doReturn SCOPE_ID
        }
        val scopeView2 = mock<ViewGroup> {
            on { tag } doReturn SCOPE_ID
        }
        val container = FrameContainerLayout(activity)
        container.addView(scopeView1)
        divView.addView(container)
        divView.addView(scopeView2)

        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, SCOPE_ID).exceptionOrNull()

        Assert.assertTrue((error as? ViewLocator.DuplicateTarget).isScopeError)
    }

    @Test
    fun `throw missing view error when target has another type`() {
        val viewWithTag = mock<ViewGroup> {
            on { tag } doReturn ID
        }
        divView.addView(viewWithTag)

        val error = ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, null).exceptionOrNull()

        Assert.assertTrue((error as? ViewLocator.MissingTarget).isTargetError)
    }

    @Test
    fun `throw duplicate target error when several views have same id without scope`() {
        val target1 = mock<View> {
            on { tag } doReturn ID
        }
        val target2 = mock<ViewGroup> {
            on { tag } doReturn ID
        }
        divView.addView(target1)
        divView.addView(target2)

        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, null).exceptionOrNull()

        Assert.assertTrue((error as? ViewLocator.DuplicateTarget).isTargetError)
    }

    @Test
    fun `throw missing scope error when scope is missing and several views have same id`() {
        val target1 = mock<View> {
            on { tag } doReturn ID
        }
        val target2 = mock<ViewGroup> {
            on { tag } doReturn ID
        }
        divView.addView(target1)
        divView.addView(target2)

        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, SCOPE_ID).exceptionOrNull()

        Assert.assertTrue((error as? ViewLocator.MissingTarget).isScopeError)
    }

    @Test
    fun `throw duplicate target error when several views have same id with correct scope`() {
        val target1 = mock<View> {
            on { tag } doReturn ID
        }
        val target2 = mock<ViewGroup> {
            on { tag } doReturn ID
        }
        val scopeView = FrameContainerLayout(activity).apply {
            tag = SCOPE_ID
            addView(target1)
            addView(target2)
        }
        divView.addView(scopeView)

        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, SCOPE_ID).exceptionOrNull()

        Assert.assertTrue((error as? ViewLocator.DuplicateTarget).isTargetError)
    }

    @Test
    fun `throw duplicate target error when several views have same id on different levels with correct scope`() {
        val target1 = mock<View> {
            on { tag } doReturn ID
        }
        val target2 = mock<ViewGroup> {
            on { tag } doReturn ID
        }
        val container = FrameContainerLayout(activity)
        container.addView(target2)
        val scopeView = FrameContainerLayout(activity).apply {
            tag = SCOPE_ID
            addView(target1)
            addView(container)
        }
        divView.addView(scopeView)

        val error = ViewLocator.findSingleViewWithTag<View>(divView, ID, SCOPE_ID).exceptionOrNull()

        Assert.assertTrue((error as? ViewLocator.DuplicateTarget).isTargetError)
    }

    @Test
    fun `return target when has tag and requested type without scope`() {
        val viewWithTag = mock<DivInputView> {
            on { tag } doReturn ID
        }
        divView.addView(viewWithTag)

        val target = ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, null).getOrNull()

        Assert.assertEquals(viewWithTag, target)
    }

    @Test
    fun `return target when has tag and requested type inside scope`() {
        val viewWithTag = mock<DivInputView> {
            on { tag } doReturn ID
        }
        val scopeView = FrameContainerLayout(activity).apply {
            tag = SCOPE_ID
            addView(viewWithTag)
        }
        divView.addView(scopeView)

        val target = ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, SCOPE_ID).getOrNull()

        Assert.assertEquals(viewWithTag, target)
    }

    @Test
    fun `return target from correct scope when same tag exists outside scope`() {
        val targetInsideScope = mock<DivInputView> {
            on { tag } doReturn ID
        }
        val targetOutsideScope = mock<DivInputView> {
            on { tag } doReturn ID
        }
        val scopeView = FrameContainerLayout(activity).apply {
            tag = SCOPE_ID
            addView(targetInsideScope)
        }
        divView.addView(targetOutsideScope)
        divView.addView(scopeView)

        val target = ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, SCOPE_ID).getOrNull()

        Assert.assertEquals(targetInsideScope, target)
    }

    @Test
    fun `throw missing view error when target exists only outside correct scope`() {
        val targetOutsideScope = mock<DivInputView> {
            on { tag } doReturn ID
        }
        val scopeView = FrameContainerLayout(activity).apply {
            tag = SCOPE_ID
        }
        divView.addView(targetOutsideScope)
        divView.addView(scopeView)

        val error = ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, SCOPE_ID).exceptionOrNull()
        Assert.assertTrue((error as? ViewLocator.MissingTarget).isTargetError)
    }

    @Test
    fun `return target when scope is missing and target is unique`() {
        val viewWithTag = mock<DivInputView> {
            on { tag } doReturn ID
        }
        divView.addView(viewWithTag)

        val target = ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, SCOPE_ID).getOrNull()

        Assert.assertEquals(viewWithTag, target)
    }

    @Test
    fun `log warning when scope is missing and target is unique`() {
        val viewWithTag = mock<DivInputView> {
            on { tag } doReturn ID
        }
        divView.addView(viewWithTag)

        ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, SCOPE_ID)

        val warnings = divView.viewComponent
            .errorCollectors
            .getOrCreate(divView.dataTag, divView.divData)
            .getWarnings()
            .toList()
        Assert.assertEquals(1, warnings.size)
        Assert.assertTrue(warnings.single() is ViewLocator.MissingTarget)
    }

    @Test
    fun `return target when has tag and requested type deeper in hierarchy`() {
        val viewWithTag = mock<DivInputView> {
            on { tag } doReturn ID
        }
        val container = FrameContainerLayout(activity)
        container.addView(viewWithTag)
        divView.addView(container)

        val target = ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, null).getOrNull()

        Assert.assertEquals(viewWithTag, target)
    }

    @Test
    fun `return target when two views have tag but only one has requested type`() {
        val viewWithTag = mock<DivInputView> {
            on { tag } doReturn ID
        }
        val container = FrameContainerLayout(activity).apply {
            tag = ID
            addView(viewWithTag)
        }
        divView.addView(container)

        val target = ViewLocator.findSingleViewWithTag<DivInputView>(divView, ID, null).getOrNull()

        Assert.assertEquals(viewWithTag, target)
    }

    private val Exception?.isTargetError get() = this?.message?.contains("Element") == true
    private val Exception?.isScopeError get() = this?.message?.contains("Scope") == true
}
