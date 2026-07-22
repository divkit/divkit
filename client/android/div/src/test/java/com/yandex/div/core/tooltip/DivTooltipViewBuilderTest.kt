package com.yandex.div.core.tooltip

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.yandex.div.core.asExpression
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2Builder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue
import com.yandex.div2.DivText
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivWrapContentSize
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import javax.inject.Provider

/**
 * Tests for [DivTooltipViewBuilder].
 */
@RunWith(RobolectricTestRunner::class)
class DivTooltipViewBuilderTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    private val div = Div.Text(
        DivText(
            text = "tooltip text".asExpression(),
            width = DivSize.WrapContent(
                DivWrapContentSize(maxSize = DivSizeUnitValue(value = MAX_WIDTH.toLong().asExpression()))
            ),
        )
    )

    private val divTooltip = DivTooltip(
        div = div,
        id = "tooltip_id",
        position = DivTooltip.Position.BOTTOM.asExpression(),
    )

    private val boundView = View(activity).apply {
        layoutParams = DivLayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            maxWidth = MAX_WIDTH
            maxHeight = MAX_HEIGHT
        }
    }

    private val div2Builder = mock<Div2Builder> {
        on { buildView(any(), any(), any()) } doReturn boundView
    }

    private val div2View = mock<Div2View> {
        on { getContext() } doReturn activity
    }
    private val bindingContext = BindingContext(div2View, mock<ExpressionResolver>())

    private val underTest = DivTooltipViewBuilder(Provider { div2Builder })

    @Test
    fun `size constraints applied on binding survive tooltip view preparation`() {
        val tooltipView = underTest.buildTooltipView(bindingContext, divTooltip).tooltipView

        val layoutParams = tooltipView?.layoutParams as DivLayoutParams
        assertEquals(MAX_WIDTH, layoutParams.maxWidth)
        assertEquals(MAX_HEIGHT, layoutParams.maxHeight)
    }

    @Test
    fun `tooltip view size is set from div size`() {
        val tooltipView = underTest.buildTooltipView(bindingContext, divTooltip).tooltipView

        assertEquals(WRAP_CONTENT, tooltipView?.layoutParams?.width)
        assertEquals(WRAP_CONTENT, tooltipView?.layoutParams?.height)
    }

    @Test
    fun `tooltip view without div layout params gets them on preparation`() {
        boundView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        val tooltipView = underTest.buildTooltipView(bindingContext, divTooltip).tooltipView

        val layoutParams = tooltipView?.layoutParams as DivLayoutParams
        assertEquals(WRAP_CONTENT, layoutParams.width)
        assertEquals(WRAP_CONTENT, layoutParams.height)
    }

    private companion object {
        const val MAX_WIDTH = 150
        const val MAX_HEIGHT = 200
    }
}
