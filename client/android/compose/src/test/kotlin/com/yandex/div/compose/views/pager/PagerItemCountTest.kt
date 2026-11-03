package com.yandex.div.compose.views.pager

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivVisibility
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class PagerItemCountTest {

    @get:Rule
    val rule = createComposeRule()

    private val itemCount = Variable.IntegerVariable("count", -1)
    private val visibility = Variable.StringVariable("visibility", "visible")
    private val reporter = TestReporter()
    private val controller = DivVariableController().apply {
        declare(itemCount)
        declare(visibility)
    }
    private val configuration = DivConfiguration(reporter = reporter, variableController = controller)

    @Test
    fun `count includes invisible pages and excludes gone pages`() {
        setContent(testPager(items = listOf(
            text(text = "Visible"),
            text(text = "Invisible", visibility = constant(DivVisibility.INVISIBLE)),
            text(text = "Gone", visibility = constant(DivVisibility.GONE)),
        )))

        assertEquals(2L, itemCount.getValue())
    }

    @Test
    fun `count is available to side effects of the same composition`() {
        var firstCount: Any? = null
        val content = data(testPager())
        rule.setContentWithDivContext(configuration) {
            DivView(content)
            SideEffect {
                if (firstCount == null) firstCount = itemCount.getValue()
            }
        }
        rule.waitForIdle()

        assertEquals(1L, firstCount)
    }

    @Test
    fun `count is zero when items are absent`() {
        setContent(testPager(items = null))

        assertEquals(0L, itemCount.getValue())
    }

    @Test
    fun `count becomes zero when the last page is gone`() {
        setContent(testPager())

        visibility.set("gone")
        rule.waitForIdle()

        assertEquals(0L, itemCount.getValue())
    }

    @Test
    fun `count increases when a gone page is restored`() {
        visibility.set("gone")
        setContent(testPager())

        visibility.set("visible")
        rule.waitForIdle()

        assertEquals(1L, itemCount.getValue())
    }

    @Test
    fun `count keeps an invisible page`() {
        setContent(testPager())

        visibility.set("invisible")
        rule.waitForIdle()

        assertEquals(1L, itemCount.getValue())
    }

    @Test
    fun `count excludes virtual pages in infinite scroll`() {
        setContent(testPager(infiniteScroll = true))

        assertEquals(1L, itemCount.getValue())
    }

    @Test
    fun `count writes the variable in the pager scope`() {
        setContent(container(
            variables = listOf(variable("count", -2)),
            items = listOf(testPager(), text(text = expression("Local count: @{count}"))),
        ))

        rule.onNodeWithText("Local count: 1").assertIsDisplayed()
        assertEquals(-1L, itemCount.getValue())
    }

    @Test
    fun `missing count variable is reported`() {
        reporter.failOnError = false

        setContent(testPager(itemCountVariable = "missing"))

        assertEquals("Variable 'missing' is missing.", reporter.lastError)
    }

    @Test
    fun `non integer count variable is reported`() {
        reporter.failOnError = false

        setContent(testPager(itemCountVariable = "visibility"))

        assertEquals("variable [visibility] is not an integer variable", reporter.lastError)
    }

    @Test
    fun `parent visibility remains reactive with unrelated page variables`() {
        setContent(testPager(items = listOf(text(
            text = "Page",
            variables = listOf(variable("title", "Local title")),
            visibility = visibilityExpression("@{visibility}"),
        ))))

        visibility.set("gone")
        rule.waitForIdle()

        assertEquals(0L, itemCount.getValue())
    }

    @Test
    fun `count is written to the new variable when data is replaced`() {
        val replacementCount = Variable.IntegerVariable("replacement_count", -2)
        controller.declare(replacementCount)
        val content = mutableStateOf(data(testPager()))
        rule.setContentWithDivContext(configuration) { DivView(content.value) }
        rule.waitForIdle()

        content.value = data(testPager(itemCountVariable = "replacement_count"))
        rule.waitForIdle()

        assertEquals(1L, replacementCount.getValue())
    }

    @Test
    fun `count reflects current visibility when pager reenters composition`() {
        val showPager = mutableStateOf(true)
        val content = data(testPager())
        rule.setContentWithDivContext(configuration) {
            if (showPager.value) DivView(content)
        }
        rule.waitForIdle()
        showPager.value = false
        rule.waitForIdle()
        visibility.set("gone")

        showPager.value = true
        rule.waitForIdle()

        assertEquals(0L, itemCount.getValue())
    }

    private fun testPager(
        items: List<Div>? = listOf(text(text = "Page", visibility = visibilityExpression("@{visibility}"))),
        itemCountVariable: String = "count",
        infiniteScroll: Boolean = false,
    ): Div = pager(
        height = fixed(constant(100L)),
        id = "pager",
        infiniteScroll = infiniteScroll,
        itemCountVariable = itemCountVariable,
        items = items,
        layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize()),
        width = fixed(constant(300L)),
    )

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
        rule.waitForIdle()
    }
}
