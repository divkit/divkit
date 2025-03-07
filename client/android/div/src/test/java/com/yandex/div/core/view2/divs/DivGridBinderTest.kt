package com.yandex.div.core.view2.divs

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.widgets.DivGridLayout
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivGridBinderTest : DivBinderTest() {

    private val binder = DivGridBinder(
        baseBinder = baseBinder,
        divPatchManager = mock(),
        divBinder = { mock() },
        divViewCreator = mock(),
    )

    @Test
    fun `url action applied`() {
        val (divGrid, view) = createTestData("with-action.json")

        binder.bindView(bindingContext, view, divGrid, DivStatePath.parse("0"))

        assertActionApplied(bindingContext, view, Expected.ACTION_URI)
    }

    @Test
    fun `state action applied`() {
        val (divGrid, view) = createTestData("with-set-state-action.json")

        binder.bindView(bindingContext, view, divGrid, DivStatePath.parse("0"))

        assertActionApplied(bindingContext, view, Expected.STATE_ACTION_URI)
    }

    private fun createTestData(filename: String): Pair<Div.Grid, DivGridLayout> {
        val div = UnitTestData(GRID_DIR, filename).div as Div.Grid
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivGridLayout
        view.layoutParams = defaultLayoutParams()
        return div to view
    }

    companion object {
        private const val GRID_DIR = "div-grid"
    }
}
