package com.yandex.div.core.view2.divs

import android.view.View
import com.yandex.div.core.childrenToFlatList
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.animations.DIV_STATE_DIR
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivContainer
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

const val CONTAINER_DIR = "div-container"
private const val TEST_DIR = "$DIV_STATE_DIR/autoanimations"

@RunWith(RobolectricTestRunner::class)
class DivContainerBinderTest : DivBinderTest() {

    private val binder = DivContainerBinder(
        baseBinder, { viewCreator }, mock(),
        mock(), { mock() }, mock()
    )

    @Test
    fun `url action applied`() {
        val div = UnitTestData(CONTAINER_DIR, "with_action.json").div
        val divContainer = div.value() as DivContainer
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivLinearLayout
        view.layoutParams = defaultLayoutParams()

        binder.bindView(view, divContainer, divView, rootPath())

        assertActionApplied(divView, view, Expected.ACTION_URI)
    }

    @Test
    fun `state action applied`() {
        val div = UnitTestData(CONTAINER_DIR, "with_set_state_action.json").div
        val divContainer = div.value() as DivContainer
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivLinearLayout
        view.layoutParams = defaultLayoutParams()

        binder.bindView(view, divContainer, divView, rootPath())

        assertActionApplied(divView, view, Expected.STATE_ACTION_URI)
    }

    @Test
    fun `rebind releases unused views`() {
        val div = UnitTestData(CONTAINER_DIR, "vertical_orientation_no_alignments.json").div
        val otherDiv = UnitTestData(CONTAINER_DIR, "item_with_action.json").div
        val divContainer = div.value() as DivContainer
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivLinearLayout
        view.layoutParams = defaultLayoutParams()

        binder.bindView(view, divContainer, divView, rootPath())

        verify(visitor, never()).release(any())

        val children: List<View> = view.childrenToFlatList()

        binder.bindView(view, otherDiv.value() as DivContainer, divView, rootPath())

        verify(visitor, times(children.size)).release(any())
        children.forEach { child: View ->
            verify(visitor).release(child)
        }
    }

    @Test
    fun `adding other items release unused items`() {
        val oldData = UnitTestData(TEST_DIR, "old_container.json").div
        val newData = UnitTestData(TEST_DIR, "new_container_other_items.json").div
        val divContainer = oldData.value() as DivContainer
        val view = viewCreator.create(oldData, ExpressionResolver.EMPTY) as DivLinearLayout
        view.layoutParams = defaultLayoutParams()
        view.layout(0, 0, 100, 100)

        binder.bindView(view, divContainer, divView, rootPath())

        binder.bindView(view, newData.value() as DivContainer, divView, rootPath())

        verify(visitor, atLeast(1)).release(any())
    }

    private fun rootPath() = DivStatePath.parse("0")
}
