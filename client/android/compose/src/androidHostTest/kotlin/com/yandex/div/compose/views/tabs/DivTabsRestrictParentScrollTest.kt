package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.text
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivTabsRestrictParentScrollTest {

    @get:Rule
    val rule = createComposeRule()

    private val restrictParentScroll = Variable.BooleanVariable("restrict_parent_scroll", false)
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(restrictParentScroll) },
    )
    private val parentScroll = ScrollState(0)

    @Test
    fun `tabs with restrict_parent_scroll keep a vertical drag on their content from the parent scroll`() {
        setTabsInVerticalScroll(restrictParentScroll = constant(true))

        onPage("First page").performTouchInput { swipeUp() }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value, "parent scroll offset")
    }

    @Test
    fun `tabs without restrict_parent_scroll pass a vertical drag on their content to the parent scroll`() {
        setTabsInVerticalScroll(restrictParentScroll = constant(false))

        onPage("First page").performTouchInput { swipeUp() }
        rule.waitForIdle()

        assertTrue(parentScroll.value > 0, "parent scroll offset ${parentScroll.value} is not positive")
    }

    @Test
    fun `restrict_parent_scroll follows its expression`() {
        setTabsInVerticalScroll(restrictParentScroll = booleanExpression("@{restrict_parent_scroll}"))

        onPage("First page").performTouchInput { swipeUp() }
        rule.waitForIdle()
        assertTrue(parentScroll.value > 0, "parent scroll offset ${parentScroll.value} is not positive while unrestricted")

        rule.runOnIdle { parentScroll.dispatchRawDelta(-parentScroll.value.toFloat()) }
        restrictParentScroll.set(true)
        rule.waitForIdle()

        onPage("First page").performTouchInput { swipeUp() }
        rule.waitForIdle()
        assertEquals(0, parentScroll.value, "parent scroll offset while restricted")
    }

    @Test
    fun `tabs with restrict_parent_scroll keep the swipe leftover on their last page from the parent scroll`() {
        setTabsInHorizontalScroll(restrictParentScroll = constant(true))

        onPage("Second page").performTouchInput { swipeLeft() }
        rule.waitForIdle()

        assertEquals(0, parentScroll.value, "parent scroll offset")
    }

    @Test
    fun `tabs without restrict_parent_scroll pass the swipe leftover on their last page to the parent scroll`() {
        setTabsInHorizontalScroll(restrictParentScroll = constant(false))

        onPage("Second page").performTouchInput { swipeLeft() }
        rule.waitForIdle()

        assertTrue(parentScroll.value > 0, "parent scroll offset ${parentScroll.value} is not positive")
    }

    @Test
    fun `tabs with restrict_parent_scroll still switch pages by swipe`() {
        setTabsInVerticalScroll(restrictParentScroll = constant(true))

        onPage("First page").performTouchInput { swipeLeft() }
        rule.waitForIdle()

        onPage("Second page").assertIsDisplayed()
    }

    private fun onPage(text: String) = rule.onNode(hasText(text) and isPlaced())

    private fun setTabsInVerticalScroll(restrictParentScroll: Expression<Boolean>) {
        rule.setContentWithDivContext(configuration) {
            Column(modifier = Modifier.size(width = TABS_WIDTH, height = 300.dp).verticalScroll(parentScroll)) {
                DivView(data = tabsData(restrictParentScroll, selectedTab = 0))
                Spacer(modifier = Modifier.height(PARENT_FILLER))
            }
        }
        rule.waitForIdle()
    }

    private fun setTabsInHorizontalScroll(restrictParentScroll: Expression<Boolean>) {
        rule.setContentWithDivContext(configuration) {
            Row(modifier = Modifier.size(width = TABS_WIDTH, height = 300.dp).horizontalScroll(parentScroll)) {
                DivView(data = tabsData(restrictParentScroll, selectedTab = 1))
                Spacer(modifier = Modifier.width(PARENT_FILLER))
            }
        }
        rule.waitForIdle()
    }

    private fun tabsData(restrictParentScroll: Expression<Boolean>, selectedTab: Long) = data(
        tabs(
            items = listOf(
                tabItem(div = page("First page"), title = "First"),
                tabItem(div = page("Second page"), title = "Second"),
            ),
            restrictParentScroll = restrictParentScroll,
            selectedTab = constant(selectedTab),
            width = fixed(constant(TABS_WIDTH.value.toLong())),
        )
    )

    private fun page(text: String) = text(height = fixed(constant(200L)), text = constant(text))
}

private val TABS_WIDTH = 200.dp
private val PARENT_FILLER = 1000.dp

// TabsContent measures pages in their own subcompositions, which it never places.
private fun isPlaced() = SemanticsMatcher("is placed") { it.layoutInfo.isPlaced }
