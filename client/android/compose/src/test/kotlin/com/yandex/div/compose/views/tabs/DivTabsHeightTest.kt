package com.yandex.div.compose.views.tabs

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.separator
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue
import com.yandex.div2.DivWrapContentSize
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The rest of the height behaviour is covered by snapshot cases: `div-tabs/wrap-content-max-size`
 * and `div-tabs/wrap-content-max-size-dynamic-height` show pages wrapping under a bounded tabs
 * height, `div-tabs/match-parent-height-with-paddings` shows an exact height stretching
 * `match_parent` pages. The cap has no golden: the tabs themselves stay within `max_size` either
 * way, only the height the pages are measured with differs.
 */
@RunWith(AndroidJUnit4::class)
class DivTabsHeightTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `max_size caps the height match_parent pages are measured with`() {
        rule.setContent(
            configuration = DivConfiguration(reporter = TestReporter()),
            data = data(
                tabs(
                    height = wrapContentUpTo(MAX_SIZE_DP),
                    items = listOf(100, 200).mapIndexed { index, contentHeight ->
                        tabItem(
                            div = container(
                                height = matchParent(),
                                id = "page$index",
                                items = listOf(separator(height = fixed(contentHeight)))
                            ),
                            title = "Tab $index"
                        )
                    }
                )
            )
        )

        val firstPage = rule.onNode(hasTestTag("page0") and isPlaced()).fetchSemanticsNode()
        val expected = with(rule.density) { (MAX_SIZE_DP - TITLE_ROW_HEIGHT_DP).dp.roundToPx() }
        assertEquals(expected, firstPage.size.height, "first page height")
    }
}

private const val MAX_SIZE_DP = 100

// Default tab_title_style: 12 sp * 1.3 line height + 6 + 6 paddings, plus title_paddings.bottom = 8.
private const val TITLE_ROW_HEIGHT_DP = 35

private fun wrapContentUpTo(maxSizeDp: Int): DivSize = DivSize.WrapContent(
    DivWrapContentSize(maxSize = DivSizeUnitValue(value = constant(maxSizeDp.toLong())))
)

// TabsContent subcomposes every measured page a second time without placing that copy.
private fun isPlaced() = SemanticsMatcher("is placed") { it.layoutInfo.isPlaced }
