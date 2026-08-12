package com.yandex.div.core.view2.divs

import com.yandex.div.core.asExpression
import com.yandex.div.core.dagger.DivDataComponent
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.animations.DivTransitionHandler
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFocus
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

    private val baseBinder = DivBaseBinder(mock(), mock(), mock(), mock(), mock())
    private val dataComponent = mock<DivDataComponent> {
        on { layoutProviderBinder } doReturn mock()
    }
    private val divView = mock<Div2View> {
        on { divTransitionHandler } doReturn DivTransitionHandler(mock)
        on { dataComponent } doReturn dataComponent
        on { viewComponent } doReturn mock()
    }
    private val resolver = mock<ExpressionResolver>()
    private val path = DivStatePath.fromState(0)

    @Test
    fun `do not apply paddings when same`() {
        val div = createDiv(paddings = paddingsBottom1).toBlock(resolver, path)
        val oldDiv = createDiv(paddings = paddingsBottom1).toBlock(resolver, path)

        baseBinder.bindView(view, oldDiv, null, divView)
        clearInvocations(view)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view, never()).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `do not apply paddings when equals`() {
        val div = createDiv(paddings = paddingsBottom1).toBlock(resolver, path)
        val oldDiv = createDiv(paddings = paddingsBottom2).toBlock(resolver, path)

        baseBinder.bindView(view, oldDiv, null, divView)
        clearInvocations(view)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view, never()).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `apply paddings when value changed`() {
        val div = createDiv(paddings = paddingsBottom1).toBlock(resolver, path)
        val oldDiv = createDiv(paddings = paddingsTop).toBlock(resolver, path)

        baseBinder.bindView(view, div, oldDiv, divView)

        verify(view).setPadding(any(), any(), any(), any())
        verify(view, atLeastOnce()).requestLayout()
    }

    @Test
    fun `not clear animation when view is visible`() {
        val div = createDiv(transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        verify(view, never()).clearAnimation()
    }

    @Test
    fun `clear animation when view is invisible`() {
        val div = createDiv(
            visibility = DivVisibility.INVISIBLE,
            transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        ).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        verify(view).clearAnimation()
    }

    @Test
    fun `clear animation when view is gone`() {
        val div = createDiv(
            visibility = DivVisibility.GONE,
            transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        ).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        verify(view).clearAnimation()
    }

    @Test
    fun `set isFocusableInTouchMode true when div has focus block`() {
        val div = createDiv(focus = DivFocus()).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        assert(view.isFocusableInTouchMode)
    }

    @Test
    fun `set isFocusableInTouchMode false when div has no focus block`() {
        val div = createDiv(focus = null).toBlock(resolver, path)

        baseBinder.bindView(view, div, null, divView)

        assert(!view.isFocusableInTouchMode)
    }

    private fun createDiv(
        paddings: DivEdgeInsets? = null,
        visibility: DivVisibility = DivVisibility.VISIBLE,
        transitionTriggers: List<DivTransitionTrigger>? = null,
        focus: DivFocus? = null
    ) = Div.Text(DivText(
        text = "text".asExpression(),
        paddings = paddings,
        visibility = visibility.asExpression(),
        transitionTriggers = transitionTriggers,
        focus = focus,
    ))
}
