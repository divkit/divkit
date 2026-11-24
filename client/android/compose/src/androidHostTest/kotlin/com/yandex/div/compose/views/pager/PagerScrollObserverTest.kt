package com.yandex.div.compose.views.pager

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.observer.DivComponentObserver
import com.yandex.div.compose.observer.events.DivPagerPageChangedEvent
import com.yandex.div.compose.observer.events.DivScrollDirection
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.text
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivPager
import com.yandex.div2.DivPatch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PagerScrollObserverTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val restoration = StateRestorationTester(composeRule)
    private val events = mutableListOf<DivPagerPageChangedEvent>()
    private val lastPageVisibility = Variable.StringVariable("last_page_visibility", "visible")
    private val configuration = divConfiguration {
        reporter = TestReporter()
        variableController = DivVariableController().apply { declare(lastPageVisibility) }
        componentObserver = object : DivComponentObserver {
            override fun onPagerPageChanged(event: DivPagerPageChangedEvent) {
                events.add(event)
            }
        }
    }
    private var card by mutableStateOf<DivData?>(null)
    private lateinit var context: DivContext

    @Test
    fun `reselecting the default page does not report a change`() {
        setContent(pagerCard(defaultItem = 1))

        perform("set_current_item?id=pager&item=1&animated=false")

        assertEquals(emptyList(), events)
    }

    @Test
    fun `swipe past the last page of an unnamed cyclic pager reports the first page going forward`() {
        setContent(pagerCard(pagerId = null, defaultItem = 2, infinite = true))

        composeRule.onNode(hasScrollToIndexAction()).performTouchInput { swipeLeft() }
        composeRule.waitForIdle()

        assertEquals(listOf(DivPagerPageChangedEvent("card", null, 0, DivScrollDirection.FORWARD)), events)
    }

    @Test
    fun `previous item before the first cyclic page reports the last page going back`() {
        setContent(pagerCard(infinite = true))

        perform("set_previous_item?id=pager&animated=false")

        assertEquals(listOf(DivPagerPageChangedEvent("card", "pager", 2, DivScrollDirection.BACKWARD)), events)
    }

    @Test
    fun `restoring the selected page does not report it again`() {
        setContent(pagerCard())
        perform("set_current_item?id=pager&item=2&animated=true")

        restoration.emulateSavedInstanceStateRestore()

        composeRule.onNodeWithText("2").assertIsDisplayed()
        assertEquals(listOf(DivPagerPageChangedEvent("card", "pager", 2, DivScrollDirection.FORWARD)), events)
    }

    @Test
    fun `replacement card reports only its own subsequent selection`() {
        setContent(pagerCard())
        composeRule.runOnIdle { card = pagerCard(cardId = "replacement", pagerId = "other", defaultItem = 1) }
        composeRule.waitForIdle()

        perform("set_current_item?id=other&item=2&animated=false")

        assertEquals(listOf(DivPagerPageChangedEvent("replacement", "other", 2, DivScrollDirection.FORWARD)), events)
    }

    @Test
    fun `hiding the selected last page reports the replacement selection`() {
        setContent(pagerCard(defaultItem = 2, items = pagesWithVariableLastItem()))

        composeRule.runOnIdle { lastPageVisibility.set("gone") }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1").assertIsDisplayed()
        assertEquals(listOf(DivPagerPageChangedEvent("card", "pager", 1, DivScrollDirection.BACKWARD)), events)
    }

    @Test
    fun `hiding the selected last cyclic page reports the replacement selection`() {
        setContent(pagerCard(defaultItem = 2, infinite = true, items = pagesWithVariableLastItem()))

        composeRule.runOnIdle { lastPageVisibility.set("gone") }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1").assertIsDisplayed()
        assertEquals(listOf(DivPagerPageChangedEvent("card", "pager", 1, DivScrollDirection.BACKWARD)), events)
    }

    @Test
    fun `remapping a cyclic window keeps the unchanged selection silent`() {
        setContent(pagerCard(defaultItem = 1, infinite = true, items = pagesWithVariableLastItem()))

        composeRule.runOnIdle { lastPageVisibility.set("gone") }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1").assertIsDisplayed()
        assertEquals(emptyList(), events)
    }

    @Test
    fun `previous item after a silent cyclic remapping reports backward direction`() {
        setContent(pagerCard(defaultItem = 1, infinite = true, items = pagesWithVariableLastItem()))
        composeRule.runOnIdle { lastPageVisibility.set("gone") }
        composeRule.waitForIdle()

        perform("set_previous_item?id=pager&animated=false")

        assertEquals(listOf(DivPagerPageChangedEvent("card", "pager", 0, DivScrollDirection.BACKWARD)), events)
    }

    @Test
    fun `rebinding a card with the same ids does not report its initial selection`() {
        setContent(pagerCard(defaultItem = 2))

        composeRule.runOnIdle {
            card = pagerCard(items = listOf(text(text = constant("replacement"), width = fixed(300))))
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("replacement").assertIsDisplayed()
        assertEquals(emptyList(), events)
    }

    @Test
    fun `removing the selected page by patch reports the replacement selection`() {
        val initial = pagerCard(
            defaultItem = 2,
            items = listOf(
                text(text = constant("0"), width = fixed(300)),
                text(text = constant("1"), width = fixed(300)),
                text(id = "last", text = constant("2"), width = fixed(300)),
            ),
        )
        setContent(initial)

        composeRule.runOnIdle {
            runBlocking {
                assertTrue(context.patcher.applyPatch(initial, DivPatch(listOf(DivPatch.Change("last")))))
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1").assertIsDisplayed()
        assertEquals(listOf(DivPagerPageChangedEvent("card", "pager", 1, DivScrollDirection.BACKWARD)), events)
    }

    private fun pagesWithVariableLastItem(): List<Div> = listOf(
        text(text = constant("0"), width = fixed(300)),
        text(text = constant("1"), width = fixed(300)),
        text(
            text = constant("2"),
            width = fixed(300),
            visibility = visibilityExpression("@{last_page_visibility}"),
        ),
    )

    private fun pagerCard(
        cardId: String = "card",
        pagerId: String? = "pager",
        defaultItem: Long = 0,
        infinite: Boolean = false,
        items: List<Div> = List(3) { text(text = constant(it.toString()), width = fixed(300)) },
    ): DivData = data(pager(
        id = pagerId,
        width = fixed(300),
        height = fixed(100),
        items = items,
        defaultItem = defaultItem,
        infiniteScroll = infinite,
        scrollAxisAlignment = DivPager.ItemAlignment.START,
    ), logId = cardId)

    private fun setContent(initial: DivData) {
        card = initial
        restoration.setContent {
            val baseContext = LocalContext.current
            context = remember { DivContext(baseContext, configuration) }
            CompositionLocalProvider(LocalContext provides context) {
                card?.let { DivView(it) }
            }
        }
        composeRule.waitForIdle()
    }

    private fun perform(uri: String) {
        composeRule.runOnIdle {
            context.debugFeatures.performAction(checkNotNull(card), action(url = "div-action://$uri"))
        }
        composeRule.waitForIdle()
    }
}
