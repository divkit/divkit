package com.yandex.div.core.view2.divs

import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivSeparatorBinderTest : DivBinderTest() {

    private val binder = DivSeparatorBinder(
        baseBinder,
    )

    @Test
    fun `url action applied`() {
        val div = UnitTestData(SEPARATOR_DIR, "with_action.json").div as Div.Separator
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivSeparatorView
        view.layoutParams = defaultLayoutParams()

        binder.bindView(bindingContext, view, div)

        assertActionApplied(bindingContext, view, Expected.ACTION_URI)
    }

    @Test
    fun `state action applied`() {
        val div = UnitTestData(SEPARATOR_DIR, "with_set_state_action.json").div as Div.Separator
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivSeparatorView
        view.layoutParams = defaultLayoutParams()

        binder.bindView(bindingContext, view, div)

        assertActionApplied(bindingContext, view, Expected.STATE_ACTION_URI)
    }

    companion object {
        private const val SEPARATOR_DIR = "div-separator"
    }
}
