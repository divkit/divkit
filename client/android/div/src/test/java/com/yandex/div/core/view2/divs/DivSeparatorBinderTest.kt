package com.yandex.div.core.view2.divs

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.widgets.DivSeparatorView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.ExpressionResolver
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivSeparatorBinderTest : DivBinderTest() {

    private val path = DivStatePath.fromState(0)
    private val binder = DivSeparatorBinder(baseBinder)

    @Test
    fun `url action applied`() {
        val div = UnitTestData(SEPARATOR_DIR, "with_action.json").div
            .toBlock(resolver, path) as DivBlock.Separator
        val view = viewCreator.create(div.div, ExpressionResolver.EMPTY) as DivSeparatorView
        view.layoutParams = defaultLayoutParams()

        binder.bindView(view, div, divView)

        assertActionApplied(view, Expected.ACTION_URI, resolver, divView)
    }

    @Test
    fun `state action applied`() {
        val div = UnitTestData(SEPARATOR_DIR, "with_set_state_action.json").div
            .toBlock(resolver, path) as DivBlock.Separator
        val view = viewCreator.create(div.div, ExpressionResolver.EMPTY) as DivSeparatorView
        view.layoutParams = defaultLayoutParams()

        binder.bindView(view, div, divView)

        assertActionApplied(view, Expected.STATE_ACTION_URI, resolver, divView)
    }

    companion object {
        private const val SEPARATOR_DIR = "div-separator"
    }
}
