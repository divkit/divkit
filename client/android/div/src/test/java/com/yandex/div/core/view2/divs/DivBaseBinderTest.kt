package com.yandex.div.core.view2.divs

import com.yandex.div.core.asExpression
import com.yandex.div.core.dagger.Div2ViewComponent
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.animations.DivTransitionHandler
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivText
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivVisibility
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

    private val paddingsBottom1 = DivEdgeInsets(bottom = 1L.asExpression())
    private val paddingsBottom2 = DivEdgeInsets(bottom = 1L.asExpression())
    private val paddingsTop = DivEdgeInsets(top = 1L.asExpression())

    private val baseBinder = DivBaseBinder(mock(), mock(), mock(), mock())
    private val viewComponent = mock<Div2ViewComponent>()
    private val divView = mock<Div2View> {
        on { divTransitionHandler } doReturn DivTransitionHandler(mock)
        on { viewComponent } doReturn viewComponent
    }
    private val context = BindingContext(divView, ExpressionResolver.EMPTY)

    @Test
    fun `do not apply paddings when same`() {
        val div = createDiv(paddings = paddingsBottom1)
        val oldDiv = createDiv(paddings = paddingsBottom1)

        baseBinder.bindView(context, view, oldDiv, null)
        clearInvocations(view)

        baseBinder.bindView(context, view, div, oldDiv)

        verify(view, never()).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `do not apply paddings when equals`() {
        val div = createDiv(paddings = paddingsBottom1)
        val oldDiv = createDiv(paddings = paddingsBottom2)

        baseBinder.bindView(context, view, oldDiv, null)
        clearInvocations(view)

        baseBinder.bindView(context, view, div, oldDiv)

        verify(view, never()).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `apply paddings when value changed`() {
        val div = createDiv(paddings = paddingsBottom1)
        val oldDiv = createDiv(paddings = paddingsTop)

        baseBinder.bindView(context, view, div, oldDiv)

        verify(view).setPadding(any(), any(), any(), any())
        verify(view, atLeastOnce()).requestLayout()
    }

    @Test
    fun `not clear animation when view is visible`() {
        val div = createDiv(transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE))

        baseBinder.bindView(context, view, div, null)

        verify(view, never()).clearAnimation()
    }

    @Test
    fun `clear animation when view is invisible`() {
        val div = createDiv(
            visibility = DivVisibility.INVISIBLE,
            transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        )

        baseBinder.bindView(context, view, div, null)

        verify(view).clearAnimation()
    }

    @Test
    fun `clear animation when view is gone`() {
        val div = createDiv(
            visibility = DivVisibility.GONE,
            transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        )

        baseBinder.bindView(context, view, div, null)

        verify(view).clearAnimation()
    }

    private fun createDiv(
        paddings: DivEdgeInsets? = null,
        visibility: DivVisibility = DivVisibility.VISIBLE,
        transitionTriggers: List<DivTransitionTrigger>? = null
    ) = Div.Text(DivText(
        text = "text".asExpression(),
        paddings = paddings,
        visibility = visibility.asExpression(),
        transitionTriggers = transitionTriggers,
    ))
}
