package com.yandex.div.core.view2.divs.pager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(ParameterizedRobolectricTestRunner::class)
class WrapContentPageSizeItemDecorationTest(private val testCase: TestCase) {

    companion object {
        private const val PARENT_SIZE = 720
        private const val ITEM_SIZE = 299

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun data() = listOf(
            TestCase(
                "horizontal center",
                Aligned(top = 0, bottom = 0),
                Rect(211, 0, 210, 0),
            ),
            TestCase(
                "horizontal left",
                Aligned(left = 2, top = 0, bottom = 0),
                Rect(2, 0, 419, 0),
            ),
            TestCase(
                "horizontal right",
                Aligned(top = 0, right = 2, bottom = 0),
                Rect(419, 0, 2, 0),
            ),
            TestCase(
                "vertical center",
                Aligned(left = 0, right = 0),
                Rect(0, 211, 0, 210),
            ),
            TestCase(
                "vertical top",
                Aligned(left = 0, top = 2, right = 0),
                Rect(0, 2, 0, 419),
            ),
            TestCase(
                "vertical bottom",
                Aligned(left = 0, right = 0, bottom = 2),
                Rect(0, 419, 0, 2),
            ),
        )
    }

    @Test
    fun `page keeps item size along scroll axis when free space is odd`() {
        val paddings = mock<DivPagerPaddingsHolder> {
            on { alignedLeft } doReturn testCase.aligned.left
            on { alignedTop } doReturn testCase.aligned.top
            on { alignedRight } doReturn testCase.aligned.right
            on { alignedBottom } doReturn testCase.aligned.bottom
        }
        val item = mock<View> {
            on { measuredWidth } doReturn ITEM_SIZE
            on { measuredHeight } doReturn ITEM_SIZE
        }
        val page = mock<DivPagerPageLayout> { on { child } doReturn item }
        val parent = mock<RecyclerView> {
            on { width } doReturn PARENT_SIZE
            on { height } doReturn PARENT_SIZE
        }
        val offsets = Rect()

        WrapContentPageSizeItemDecoration(PARENT_SIZE, paddings)
            .getItemOffsets(offsets, page, parent, RecyclerView.State())

        assertEquals(testCase.expected, offsets)
    }

    class Aligned(
        val left: Int? = null,
        val top: Int? = null,
        val right: Int? = null,
        val bottom: Int? = null,
    )

    class TestCase(private val name: String, val aligned: Aligned, val expected: Rect) {
        override fun toString() = name
    }
}
