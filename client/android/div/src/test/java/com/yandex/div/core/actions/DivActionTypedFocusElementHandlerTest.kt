package com.yandex.div.core.actions

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.view2.divs.divView
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivActionFocusElement
import com.yandex.div2.DivActionTyped
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class DivActionTypedFocusElementHandlerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val accessibilityManager = context
        .getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    private val target = mock<View> {
        on { context } doReturn this@DivActionTypedFocusElementHandlerTest.context
        on { tag } doReturn "chip"
        on { width } doReturn 100
        on { height } doReturn 100
        on { isAttachedToWindow } doReturn true
        on { isShown } doReturn true
        on { isImportantForAccessibility } doReturn true
    }
    private val divView = divView().apply {
        whenever(childCount) doReturn 1
        whenever(getChildAt(0)) doReturn target
    }
    private val underTest = DivActionTypedFocusElementHandler()

    @BeforeTest
    fun setUp() {
        shadowOf(accessibilityManager).setEnabled(true)
        shadowOf(accessibilityManager).setTouchExplorationEnabled(true)
    }

    @Test
    fun `requests input and screen reader focus when touch exploration is enabled`() {
        requestFocus()

        verify(target).requestFocus()
        verify(target).performAccessibilityAction(ACTION_ACCESSIBILITY_FOCUS, null)
    }

    @Test
    fun `keeps input focus without screen reader focus when touch exploration is disabled`() {
        shadowOf(accessibilityManager).setTouchExplorationEnabled(false)

        requestFocus()

        verify(target).requestFocus()
        verify(target, never()).performAccessibilityAction(ACTION_ACCESSIBILITY_FOCUS, null)
    }

    @Test
    fun `requests screen reader focus after pending layout completes`() {
        whenever(target.isLayoutRequested) doReturn true

        requestFocus()

        verify(target, never()).performAccessibilityAction(ACTION_ACCESSIBILITY_FOCUS, null)
        completeLayout()
        verify(target).performAccessibilityAction(ACTION_ACCESSIBILITY_FOCUS, null)
    }

    @Test
    fun `does not request screen reader focus when target detaches before layout`() {
        whenever(target.isLayoutRequested) doReturn true
        requestFocus()
        whenever(target.isAttachedToWindow) doReturn false

        completeLayout()

        verify(target, never()).performAccessibilityAction(ACTION_ACCESSIBILITY_FOCUS, null)
    }

    private fun requestFocus() {
        val action = DivActionTyped.FocusElement(
            DivActionFocusElement(elementId = Expression.constant("chip")),
        )
        assertTrue(underTest.handleAction(null, action, divView, divView.expressionResolver))
    }

    private fun completeLayout() {
        val listener = argumentCaptor<View.OnLayoutChangeListener>()
        verify(target).addOnLayoutChangeListener(listener.capture())
        listener.firstValue.onLayoutChange(target, 0, 0, 100, 100, 0, 0, 0, 0)
    }
}
