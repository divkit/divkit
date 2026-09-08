package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.separator
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.DivSize
import com.yandex.div2.DivTabs
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivTabsMeasurementTest {

    @get:Rule
    val rule = createComposeRule()

    private val createdPages = mutableListOf<Int>()
    private val heights = listOf(100, 200, 300)
    private val selectedTab = Variable.IntegerVariable("selected_tab", 0)

    private val configuration = DivConfiguration(
        customViewFactories = heights.indices.associate { index ->
            "page-$index" to object : DivCustomViewFactory {
                @Composable
                override fun Content(modifier: Modifier, environment: DivCustomEnvironment) {
                    DisposableEffect(Unit) {
                        createdPages.add(index)
                        onDispose { }
                    }
                    Box(modifier)
                }
            }
        },
        reporter = TestReporter(),
        variableController = DivVariableController().apply { declare(selectedTab) },
    )

    private var initialHeight = 0.dp
    private var touchSlop = 0f

    @Test
    fun `only two copies of the first page are created on the first tab`() {
        setContent()

        // One copy measures the page height; the other is displayed by HorizontalPager.
        assertEquals(listOf(0, 0), createdPages)
    }

    @Test
    fun `only two copies of the selected page are created for dynamic height`() {
        selectedTab.set(1)

        setContent(dynamicHeight = true)

        // One copy measures the page height; the other is displayed by HorizontalPager.
        assertEquals(listOf(1, 1), createdPages)
    }

    @Test
    fun `nonfirst tab uses maximum page height when height is not dynamic`() {
        setContent()

        selectedTab.set(1)
        rule.waitForIdle()

        assertEquals(initialHeight + 200.dp, tabsHeight())
    }

    @Test
    fun `dynamic height follows the selected page`() {
        setContent(dynamicHeight = true)

        selectedTab.set(1)
        rule.waitForIdle()

        assertEquals(initialHeight + 100.dp, tabsHeight())
    }

    @Test
    fun `returning to the first tab restores its height`() {
        setContent()


        selectedTab.set(1)
        rule.waitForIdle()

        selectedTab.set(0)
        rule.waitForIdle()

        assertEquals(initialHeight, tabsHeight())
    }

    @Test
    fun `swiping back to the first tab restores its height after settling`() {
        setContent()


        rule.onNodeWithTag("tabs").performTouchInput { swipeLeft() }
        rule.waitForIdle()

        assertEquals(initialHeight + 200.dp, tabsHeight())

        rule.onNodeWithTag("tabs").performTouchInput { swipeRight() }
        rule.waitForIdle()

        assertEquals(initialHeight, tabsHeight())
    }

    @Test
    fun `measurement page is reused after returning to the first tab`() {
        setContent()

        selectedTab.set(1)
        rule.waitForIdle()

        selectedTab.set(0)
        rule.waitForIdle()

        selectedTab.set(1)
        rule.waitForIdle()

        assertEquals(1, createdPages.count { it == 2 })
    }

    @Test
    fun `dynamic height interpolates between the first and second pages`() {
        setContent(dynamicHeight = true)

        dragQuarterPage()

        rule.onNodeWithTag("tabs").assertHeightIsEqualTo(initialHeight + 25.dp)
    }

    @Test
    fun `dynamic height interpolates between nonfirst pages`() {
        selectedTab.set(1)

        setContent(dynamicHeight = true)

        dragQuarterPage()

        rule.onNodeWithTag("tabs").assertHeightIsEqualTo(initialHeight + 25.dp)
    }

    @Test
    fun `nondynamic height interpolates from the first page to the maximum height`() {
        setContent()

        dragQuarterPage()

        rule.onNodeWithTag("tabs").assertHeightIsEqualTo(initialHeight + 50.dp)
    }

    @Test
    fun `nondynamic height remains maximal between nonfirst pages`() {
        selectedTab.set(1)

        setContent()

        dragQuarterPage()

        rule.onNodeWithTag("tabs").assertHeightIsEqualTo(initialHeight)
    }

    @Test
    fun `unbounded width uses the widest page even when it is not selected`() {
        setContent(
            items = heights.mapIndexed { index, size ->
                tabItem(
                    title = "$index",
                    div = separator(height = fixed(size), width = fixed(size)),
                )
            },
            width = wrapContent(),
        )

        rule.onNodeWithTag("tabs").assertWidthIsEqualTo(300.dp)
    }

    private fun dragQuarterPage() {
        rule.onNodeWithTag("tabs").performTouchInput {
            down(Offset(width * 0.75f, height - 10f))
            moveBy(Offset(-width / 4f - touchSlop, 0f))
        }
        rule.waitForIdle()
    }

    private fun tabsHeight(): Dp {
        return rule.onNodeWithTag("tabs")
            .getUnclippedBoundsInRoot()
            .let { it.bottom - it.top }
    }

    private fun setContent(
        dynamicHeight: Boolean = false,
        items: List<DivTabs.Item> = heights.mapIndexed { index, height ->
            tabItem(
                title = "Tab $index",
                div = custom(height = fixed(height), type = "page-$index"),
            )
        },
        width: DivSize = fixed(300),
    ) {
        rule.setContentWithDivContext(configuration) {
            touchSlop = LocalViewConfiguration.current.touchSlop
            DivView(
                data = data(
                    tabs(
                        dynamicHeight = constant(dynamicHeight),
                        id = "tabs",
                        items = items,
                        selectedTab = intExpression("@{selected_tab}"),
                        width = width,
                    )
                ),
                modifier = Modifier.wrapContentWidth(unbounded = true),
            )
        }
        rule.waitForIdle()
        initialHeight = tabsHeight()
    }
}
