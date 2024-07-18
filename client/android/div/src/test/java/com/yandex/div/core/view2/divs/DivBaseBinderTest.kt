package com.yandex.div.core.view2.divs

import com.yandex.div.core.asExpression
import com.yandex.div.core.dagger.Div2ViewComponent
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.animations.DivTransitionHandler
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
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
    private val context = BindingContext.createEmpty(divView)

    @Test
    fun `do not apply paddings when same`() {
        val div = DivText(text = "text".asExpression(), paddings = paddingsBottom1)
        val oldDiv = DivText(text = "text".asExpression(), paddings = paddingsBottom1)

        baseBinder.bindView(context, view, oldDiv, null)
        clearInvocations(view)

        baseBinder.bindView(context, view, div, oldDiv)

        verify(view, never()).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `do not apply paddings when equals`() {
        val div = DivText(text = "text".asExpression(), paddings = paddingsBottom1)
        val oldDiv = DivText(text = "text".asExpression(), paddings = paddingsBottom2)

        baseBinder.bindView(context, view, oldDiv, null)
        clearInvocations(view)

        baseBinder.bindView(context, view, div, oldDiv)

        verify(view, never()).setPadding(any(), any(), any(), any())
        verify(view, never()).requestLayout()
    }

    @Test
    fun `apply paddings when value changed`() {
        val div = DivText(text = "text".asExpression(), paddings = paddingsBottom1)
        val oldDiv = DivText(text = "text".asExpression(), paddings = paddingsTop)

        baseBinder.bindView(context, view, div, oldDiv)

        verify(view).setPadding(any(), any(), any(), any())
        verify(view, atLeastOnce()).requestLayout()
    }

    @Test
    fun `not clear animation when view is visible`() {
        val div = DivText(
                text = "text".asExpression(),
                visibility = DivVisibility.VISIBLE.asExpression(),
                transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        )

        baseBinder.bindView(context, view, div, null)

        verify(view, never()).clearAnimation()
    }

    @Test
    fun `clear animation when view is invisible`() {
        val div = DivText(
                text = "text".asExpression(),
                visibility = DivVisibility.INVISIBLE.asExpression(),
                transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        )

        baseBinder.bindView(context, view, div, null)

        verify(view).clearAnimation()
    }

    @Test
    fun `clear animation when view is gone`() {
        val div = DivText(
                text = "text".asExpression(),
                visibility = DivVisibility.GONE.asExpression(),
                transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)
        )

        baseBinder.bindView(context, view, div, null)

        verify(view).clearAnimation()
    }
}
