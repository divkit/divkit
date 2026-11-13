package com.yandex.div.core.view2.divs.pager

import android.view.View
import android.view.View.LAYOUT_DIRECTION_LTR
import android.view.View.LAYOUT_DIRECTION_RTL
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div2.DivPager.ItemAlignment
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(ParameterizedRobolectricTestRunner::class)
class WrapContentPageSizeProviderTest(private val testCase: TestCase) {

    companion object {
        private const val PARENT_SIZE = 720
        private const val ITEM_SIZE = 299

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun data() = listOf(
            TestCase("horizontal ltr", isHorizontal = true, LAYOUT_DIRECTION_LTR, 1f to 3f),
            TestCase("horizontal rtl", isHorizontal = true, LAYOUT_DIRECTION_RTL, 3f to 1f),
            TestCase("vertical", isHorizontal = false, LAYOUT_DIRECTION_LTR, 2f to 4f),
        )
    }

    @Test
    fun `centered neighbour sizes are page decoration offsets`() {
        val page = mock<View> {
            on { width } doReturn ITEM_SIZE
            on { height } doReturn ITEM_SIZE
        }
        val layoutManager = mock<RecyclerView.LayoutManager> {
            on { findViewByPosition(0) } doReturn page
            on { getLeftDecorationWidth(page) } doReturn 1
            on { getTopDecorationHeight(page) } doReturn 2
            on { getRightDecorationWidth(page) } doReturn 3
            on { getBottomDecorationHeight(page) } doReturn 4
        }
        val recyclerView = mock<RecyclerView> {
            on { getLayoutManager() } doReturn layoutManager
            on { layoutDirection } doReturn testCase.layoutDirection
        }
        val provider = WrapContentPageSizeProvider(
            recyclerView,
            testCase.isHorizontal,
            PARENT_SIZE,
            mock(),
            ItemAlignment.CENTER,
        )

        val neighbourSizes = provider.getPrevNeighbourSize(0) to provider.getNextNeighbourSize(0)

        assertEquals(testCase.expected, neighbourSizes)
    }

    class TestCase(
        private val name: String,
        val isHorizontal: Boolean,
        val layoutDirection: Int,
        val expected: Pair<Float, Float>,
    ) {
        override fun toString() = name
    }
}
