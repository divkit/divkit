package com.yandex.div.compose

import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.performTouchInput
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.actions.DivActionEvent
import com.yandex.div.compose.actions.DivActionLogger
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.menuItem
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textRange
import com.yandex.div2.Div
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithActionBeaconsTest {
    @get:Rule
    val rule = createComposeRule()

    private val logger: DivActionLogger = mock()
    private val configuration = divConfiguration {
        actionLogger = logger
        reporter = TestReporter()
    }
    private val beaconUrl = "https://divkit.tech/log/tap"
    private val beaconAction = action(id = "beacon", logUrl = beaconUrl)

    @Test
    fun `tap exposes beacon URL`() {
        setContent(text(text = constant("Button"), actions = listOf(beaconAction)))

        rule.onNodeWithText("Button").performClick()

        verifyBeacon(DivActionSource.TAP, beaconUrl)
    }

    @Test
    fun `double tap exposes beacon URL`() {
        setContent(text(text = constant("Button"), doubleTapActions = listOf(beaconAction)))

        rule.onNodeWithText("Button").performTouchInput { doubleClick() }

        verifyBeacon(DivActionSource.DOUBLE_TAP, beaconUrl)
    }

    @Test
    fun `long tap exposes beacon URL`() {
        setContent(text(text = constant("Button"), longTapActions = listOf(beaconAction)))

        rule.onNodeWithText("Button").performTouchInput { longClick() }

        verifyBeacon(DivActionSource.LONG_TAP, beaconUrl)
    }

    @Test
    fun `menu item tap exposes beacon URL`() {
        setContent(
            text(
                text = constant("Button"),
                actions = listOf(action(menuItems = listOf(menuItem(text = "Item", action = beaconAction))))
            )
        )
        rule.onNodeWithText("Button").performClick()

        rule.onNodeWithText("Item").performClick()

        verifyBeacon(DivActionSource.TAP, beaconUrl)
    }

    @Test
    fun `tap with menus exposes only the first menu beacon URL`() {
        val menuItems = listOf(menuItem(text = "Item", action = action()))
        setContent(
            text(
                text = constant("Button"),
                actions = listOf(
                    action(id = "regular", logUrl = "$beaconUrl/regular"),
                    action(id = "first", logUrl = "$beaconUrl/first", menuItems = menuItems),
                    action(id = "second", logUrl = "$beaconUrl/second", menuItems = menuItems)
                )
            )
        )

        rule.onNodeWithText("Button").performClick()

        val events = argumentCaptor<DivActionEvent>()
        verify(logger, times(3)).logAction(any(), events.capture())
        assertEquals(
            listOf("regular" to null, "first" to "$beaconUrl/first".toUri(), "second" to null),
            events.allValues.map { it.id to it.logUrl }
        )
    }

    @Test
    fun `text range tap exposes beacon URL`() {
        setContent(
            text(
                id = "text",
                text = constant("Link"),
                ranges = listOf(textRange(start = 0, end = 4, actions = listOf(beaconAction)))
            )
        )

        rule.onNodeWithTag("text").performFirstLinkClick()

        verifyBeacon(DivActionSource.TAP, beaconUrl)
    }

    @Test
    fun `active tab title tap omits beacon URL`() {
        setContent(
            tabs(
                items = listOf(
                    tabItem(div = text(text = "First page"), title = "First", titleClickAction = beaconAction),
                    tabItem(div = text(text = "Second page"), title = "Second")
                )
            )
        )

        rule.onNodeWithText("First").performClick()

        verifyBeacon(DivActionSource.TAP, null)
    }

    @Test
    fun `tap omits beacon URL with unsupported scheme`() {
        setContent(text(text = constant("Button"), actions = listOf(action(id = "beacon", logUrl = "custom://log"))))

        rule.onNodeWithText("Button").performClick()

        verifyBeacon(DivActionSource.TAP, null)
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }

    private fun verifyBeacon(source: DivActionSource, logUrl: String?) {
        verify(logger).logAction(
            any(),
            eq(
                DivActionEvent(
                    id = "beacon",
                    payload = null,
                    source = source,
                    url = null,
                    typed = null,
                    logUrl = logUrl?.toUri(),
                    referer = null,
                )
            )
        )
    }
}
