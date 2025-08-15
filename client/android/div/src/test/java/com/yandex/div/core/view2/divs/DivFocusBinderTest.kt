package com.yandex.div.core.view2.divs

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivBorder
import com.yandex.div2.DivCornersRadius
import com.yandex.div2.DivDimension
import com.yandex.div2.DivPoint
import com.yandex.div2.DivShadow
import com.yandex.div2.DivStroke
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.verification.VerificationMode
import org.robolectric.RobolectricTestRunner

private const val ELEVATION = 10f

@RunWith(RobolectricTestRunner::class)
class DivFocusBinderTest {

    private val actionBinder = mock<DivActionBinder>()
    private val resources = mock<Resources> {
        on { getDimension(any()) } doReturn ELEVATION
    }
    private var focusListener: View.OnFocusChangeListener? = null
    private val view = mock<DivLineHeightTextView> {
        on { isFocused } doReturn true
        on { onFocusChangeListener = anyOrNull() } doAnswer {
            focusListener = it.arguments[0] as? View.OnFocusChangeListener
            on { onFocusChangeListener } doReturn focusListener
            Unit
        }
    }
    private val customView = mock<View> {
        on { resources } doReturn resources
        on { isFocused } doReturn true
    }
    private val divView = mock<Div2View>()
    private val resolver = mock<ExpressionResolver>()
    private val context = BindingContext(divView, resolver)
    private val defaultBorder = DivBorder(hasShadow = Expression.constant(true))
    private val focusActions = listOf(mockDivAction("focus"))
    private val blurActions = listOf(mockDivAction("blur"))

    private val underTest = DivFocusBinder(actionBinder)

    @Test
    fun `apply blurred border on bind when focused border is null`() {
        bindBorder()
        verifyBorderSet()
    }

    @Test
    fun `apply blurred border on bind when focused border is empty`() {
        bindBorder(DivBorder())
        verifyBorderSet()
    }

    @Test
    fun `apply blurred border on bind when view is not focused`() {
        setViewNotFocused()
        val focusedBorder = DivBorder(hasShadow = Expression.constant(true))

        bindBorder(focusedBorder)

        verifyBorderSet()
    }

    @Test
    fun `apply focused border on bind when view is focused and border has corner radius`() {
        val focusedBorder = borderWithCornerRadius
        bindBorder(focusedBorder)
        verifyBorderSet(focusedBorder)
    }

    @Test
    fun `apply focused border on bind when view is focused and border has corners radius`() {
        val focusedBorder = DivBorder(
            cornersRadius = DivCornersRadius(topLeft = Expression.constant(4))
        )
        bindBorder(focusedBorder)
        verifyBorderSet(focusedBorder)
    }

    @Test
    fun `apply focused border on bind when view is focused and border has shadow`() {
        val focusedBorder = DivBorder(hasShadow = Expression.constant(true))
        bindBorder(focusedBorder)
        verifyBorderSet(focusedBorder)
    }

    @Test
    fun `apply focused border on bind when view is focused and border has shadow object`() {
        val focusedBorder = borderWithShadow
        bindBorder(focusedBorder)
        verifyBorderSet(focusedBorder)
    }

    @Test
    fun `apply focused border on bind when view is focused and border has stroke`() {
        val focusedBorder = DivBorder(stroke = DivStroke(color = Expression.constant(Color.YELLOW)))
        bindBorder(focusedBorder)
        verifyBorderSet(focusedBorder)
    }

    @Test
    fun `set no elevation for custom view when border is empty`() {
        bindBorder(blurredBorder = DivBorder(), viewToBind = customView)
        verifyNoElevation()
    }

    @Test
    fun `set no elevation for custom view when border does not have shadow`() {
        bindBorder(borderWithCornerRadius, viewToBind = customView)
        verifyNoElevation()
    }

    @Test
    fun `set no elevation for custom view when border has shadow object`() {
        bindBorder(borderWithShadow, viewToBind = customView)
        verifyNoElevation()
    }

    @Test
    fun `set elevation for custom view when border has shadow and no shadow object`() {
        bindBorder(viewToBind = customView)
        verifyElevation(ELEVATION)
    }

    @Test
    fun `not listen to focus changes on bind border when no listener and focused border is empty`() {
        bindBorder()
        assertNull(focusListener)
    }

    @Test
    fun `not listen to focus changes on bind actions when no listener and no actions`() {
        bindActions(null, null)
        assertNull(focusListener)
    }

    @Test
    fun `not listen to focus changes on bind border when no actions and focused border is empty`() {
        bindBorder(defaultBorder)
        bindBorder()
        assertNull(focusListener)
    }

    @Test
    fun `not listen to focus changes on bind actions when no border and no actions`() {
        bindActions()
        bindActions(null, null)
        assertNull(focusListener)
    }

    @Test
    fun `apply focused border on focus after bind border`() {
        val focusedBorder = borderWithCornerRadius
        setViewNotFocused()
        bindBorder(focusedBorder)

        onFocusChange(true)

        verifyBorderSet(focusedBorder)
    }

    @Test
    fun `apply blurred border on blur after bind border`() {
        val focusedBorder = borderWithCornerRadius
        bindBorder(focusedBorder)

        onFocusChange(false)

        verifyBorderSet()
    }

    @Test
    fun `handle focus actions on focus after bind border`() {
        bindActions(focusActions, null)
        bindBorder()

        onFocusChange(true)

        verifyActionsHandled(focusActions)
    }

    @Test
    fun `handle blur actions on blur after bind border`() {
        bindActions(null, blurActions)
        bindBorder()

        onFocusChange(false)

        verifyActionsHandled(blurActions)
    }

    @Test
    fun `not apply border on focus after bind border when no focused border`() {
        setViewNotFocused()
        bindBorder(borderWithCornerRadius)
        bindActions()
        bindBorder()
        clearInvocations(view)

        onFocusChange(true)

        verify(view, never()).setBorder(any(), any(), eq(view))
    }

    @Test
    fun `not apply border on blur after bind border when no focused border`() {
        bindBorder(borderWithCornerRadius)
        bindActions()
        bindBorder()
        clearInvocations(view)

        onFocusChange(false)

        verify(view, never()).setBorder(any(), any(), eq(view))
    }

    @Test
    fun `apply focused border on focus after bind actions`() {
        val focusedBorder = borderWithCornerRadius
        setViewNotFocused()
        bindBorder(focusedBorder)
        bindActions()

        onFocusChange(true)

        verifyBorderSet(focusedBorder)
    }

    @Test
    fun `apply blurred border on blur after bind actions`() {
        bindBorder(borderWithCornerRadius)
        bindActions()

        onFocusChange(false)

        verifyBorderSet(defaultBorder)
    }

    @Test
    fun `handle focus actions on focus after bind actions`() {
        bindActions(focusActions, null)
        onFocusChange(true)
        verifyActionsHandled(focusActions)
    }

    @Test
    fun `not handle blur actions on focus`() {
        bindActions(focusActions, blurActions)
        onFocusChange(true)
        verify(actionBinder, never()).handleBulkActions(any(), any(), eq(blurActions), any())
    }

    @Test
    fun `not handle focus actions on blur`() {
        bindActions(focusActions, blurActions)
        onFocusChange(false)
        verifyActionsHandled(focusActions, never())
    }

    @Test
    fun `handle blur actions on blur after bind actions`() {
        bindActions(null, blurActions)
        onFocusChange(false)
        verifyActionsHandled(blurActions)
    }

    @Test
    fun `not handle focus actions on focus after bind actions when no focus actions`() {
        bindActions()
        bindBorder(borderWithCornerRadius)
        bindActions(null, blurActions)

        onFocusChange(true)

        verify(actionBinder, never()).handleBulkActions(any(), any(), any(), any())
    }

    @Test
    fun `not handle blur actions on blur after bind actions when no blur actions`() {
        bindActions()
        bindBorder(borderWithCornerRadius)
        bindActions(focusActions, null)

        onFocusChange(false)

        verify(actionBinder, never()).handleBulkActions(any(), any(), any(), any())
    }

    @Test
    fun `apply focused border on focus after rebind border`() {
        val oldBorder = borderWithCornerRadius
        val newBorder = borderWithShadow
        setViewNotFocused()
        bindBorder(oldBorder)
        bindBorder(newBorder)

        onFocusChange(true)

        verifyBorderSet(newBorder)
        verifyBorderSet(oldBorder, never())
    }

    @Test
    fun `apply blurred border on blur after rebind actions`() {
        val oldBorder = borderWithCornerRadius
        val newBorder = borderWithShadow
        bindBorder(defaultBorder, oldBorder)
        bindBorder(defaultBorder, newBorder)

        onFocusChange(false)

        verifyBorderSet(newBorder)
        verifyBorderSet(oldBorder, never())
    }

    @Test
    fun `handle focus actions on focus after rebind actions`() {
        val newActions = listOf(mockDivAction("new_focus"))
        bindActions(focusActions, null)
        bindActions(newActions, null)

        onFocusChange(true)

        verifyActionsHandled(newActions)
        verifyActionsHandled(focusActions, never())
    }

    @Test
    fun `handle blur actions on blur after rebind actions`() {
        val newActions = listOf(mockDivAction("new_blur"))
        bindActions(null, blurActions)
        bindActions(null, newActions)

        onFocusChange(false)

        verifyActionsHandled(newActions)
        verifyActionsHandled(blurActions, never())
    }

    private fun bindBorder(
        focusedBorder: DivBorder? = null,
        blurredBorder: DivBorder = defaultBorder,
        viewToBind: View = view
    ) = underTest.bindDivBorder(viewToBind, context, focusedBorder, blurredBorder)

    private fun setViewNotFocused() = whenever(view.isFocused).thenReturn(false)

    private val borderWithCornerRadius get() = DivBorder(cornerRadius = Expression.constant(4))

    private val borderWithShadow get() = DivBorder(shadow = DivShadow(offset = DivPoint(
        DivDimension(value = Expression.constant(2.0)),
        DivDimension(value = Expression.constant(2.0))
    )))

    private fun bindActions(
        onFocus: List<DivAction>? = focusActions,
        onBlur: List<DivAction>? = blurActions
    ) = underTest.bindDivFocusActions(view, context, onFocus, onBlur)

    private fun onFocusChange(hasFocus: Boolean) = focusListener?.onFocusChange(view, hasFocus)

    private fun mockDivAction(id: String): DivAction {
        return DivAction(
            logId = Expression.constant(id),
            url = mock()
        )
    }

    private fun verifyBorderSet(
        border: DivBorder = defaultBorder,
        mode: VerificationMode = times(1)
    ) = verify(view, mode).setBorder(any(), eq(border), eq(view))

    private fun verifyNoElevation() = verifyElevation(0f)

    private fun verifyElevation(elevation: Float) {
        verify(customView).elevation = elevation
    }

    private fun verifyActionsHandled(actions: List<DivAction>, mode: VerificationMode = times(1)) =
        verify(actionBinder, mode).handleBulkActions(any(), any(), eq(actions), any())
}
