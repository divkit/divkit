package com.yandex.div.core.view2.divs

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.ExpressionResolver
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivGridBinderTest : DivBinderTest() {

    private val binder = DivGridBinder(
        baseBinder = baseBinder,
        divBinder = { mock() },
        divViewCreator = { viewCreator },
    )

    @Test
    fun `url action applied`() {
        val (divGrid, view) = createTestData("with-action.json")

        binder.bindView(view, divGrid, divView)

        assertActionApplied(view, Expected.ACTION_URI, resolver, divView)
    }

    @Test
    fun `state action applied`() {
        val (divGrid, view) = createTestData("with-set-state-action.json")

        binder.bindView(view, divGrid, divView)

        assertActionApplied(view, Expected.STATE_ACTION_URI, resolver, divView)
    }

    private fun createTestData(filename: String): Pair<DivBlock.Grid, DivGridLayout> {
        val div = UnitTestData(GRID_DIR, filename).div.toBlock(resolver, DivStatePath.parse("0")) as DivBlock.Grid
        val view = viewCreator.create(div.div, ExpressionResolver.EMPTY) as DivGridLayout
        view.layoutParams = defaultLayoutParams()
        return div to view
    }

    companion object {
        private const val GRID_DIR = "div-grid"
    }
}
