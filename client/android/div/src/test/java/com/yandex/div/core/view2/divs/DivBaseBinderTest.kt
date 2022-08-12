package com.yandex.div.core.view2.divs

import com.yandex.div.core.asExpression
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivText
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivBaseBinderTest {

    private val view = spy(DivLineHeightTextView(context()).apply {
        layoutParams = defaultLayoutParams()
    })

    private val paddingsBottom1 = DivEdgeInsets(bottom = 1.asExpression())
    private val paddingsBottom2 = DivEdgeInsets(bottom = 1.asExpression())
    private val paddingsTop = DivEdgeInsets(top = 1.asExpression())

    private val baseBinder = DivBaseBinder(mock(), mock(), mock(), mock(), mock())
    private val expressionResolver = mock<ExpressionResolver>()
    private val divView = mock<Div2View> {
        on { expressionResolver } doReturn expressionResolver
    }

    @Test
    fun `do not apply paddings when same`() {
        val div = DivText(text = "text".asExpression(), paddings = paddingsBottom1)
        val oldDiv = DivText(text = "text".asExpression(), paddings = paddingsBottom1)

        baseBinder.bindView(view, oldDiv, null, divView)
        clearInvocations(view)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `do not apply paddings when equals`() {
        val div = DivText(text = "text".asExpression(), paddings = paddingsBottom1)
        val oldDiv = DivText(text = "text".asExpression(), paddings = paddingsBottom2)

        baseBinder.bindView(view, oldDiv, null, divView)
        clearInvocations(view)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `apply paddings when value changed`() {
        val div = DivText(text = "text".asExpression(), paddings = paddingsBottom1)
        val oldDiv = DivText(text = "text".asExpression(), paddings = paddingsTop)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view).setPadding(any(), any(), any(), any())
        verify(view, atLeastOnce()).requestLayout()
    }
}
