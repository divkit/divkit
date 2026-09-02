package com.yandex.div.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getFirstLinkBounds
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textRange
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivText
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Ellipsis actions are attached to the ellipsis appended by truncation, which is driven by real
// text measurement: without NATIVE the text always reports as fitting and nothing is truncated.
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AndroidJUnit4::class)
class DivTextActionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()

    private val configuration = DivConfiguration(
        actionHandler = actionHandler,
        reporter = reporter
    )

    @Test
    fun `tap on the ellipsis triggers ellipsis actions`() {
        setContent(
            truncatedText(
                ellipsis = DivText.Ellipsis(
                    actions = listOf(action(url = "test://ellipsis")),
                    text = constant("… more")
                )
            )
        )

        rule.onNodeWithTag("text").performFirstLinkClick()

        assertEquals(actionData(url = "test://ellipsis"), actionHandler.handledAction)
    }

    @Test
    fun `ellipsis actions are not attached when the text fits`() {
        setContent(
            text(
                id = "text",
                text = constant("Short"),
                maxLines = 1,
                ellipsis = DivText.Ellipsis(
                    actions = listOf(action(url = "test://ellipsis")),
                    text = constant("… more")
                ),
                width = fixed(constant(100))
            )
        )

        assertNull(rule.onNodeWithTag("text").getFirstLinkBounds())
    }

    @Test
    fun `disabled ellipsis actions do not create a link`() {
        setContent(
            truncatedText(
                ellipsis = DivText.Ellipsis(
                    actions = listOf(action(isEnabled = false, url = "test://ellipsis")),
                    text = constant("… more")
                )
            )
        )

        assertNull(rule.onNodeWithTag("text").getFirstLinkBounds())
    }

    @Test
    fun `tap on the ellipsis does not trigger element actions`() {
        setContent(
            truncatedText(
                action = action(url = "test://element"),
                ellipsis = DivText.Ellipsis(
                    actions = listOf(action(url = "test://ellipsis")),
                    text = constant("… more")
                )
            )
        )

        clickFirstLink()

        assertEquals(actionData(url = "test://ellipsis"), actionHandler.handledAction)
    }

    @Test
    fun `tap outside the ellipsis triggers element actions`() {
        setContent(
            truncatedText(
                action = action(url = "test://element"),
                ellipsis = DivText.Ellipsis(
                    actions = listOf(action(url = "test://ellipsis")),
                    text = constant("… more")
                )
            )
        )

        rule.onNodeWithTag("text").performTouchInput { click(Offset(1f, centerY)) }

        assertEquals(actionData(url = "test://element"), actionHandler.handledAction)
    }

    @Test
    fun `tap on the ellipsis triggers ellipsis actions in selectable text`() {
        setContent(
            truncatedText(
                ellipsis = DivText.Ellipsis(
                    actions = listOf(action(url = "test://ellipsis")),
                    text = constant("… more")
                ),
                selectable = true
            )
        )

        clickFirstLink()

        assertEquals(actionData(url = "test://ellipsis"), actionHandler.handledAction)
    }

    @Test
    fun `tap on a range triggers range actions`() {
        setContent(
            text(
                id = "text",
                text = constant("first second"),
                ranges = listOf(
                    textRange(start = 0, end = 5, actions = listOf(action(url = "test://range")))
                )
            )
        )

        rule.onNodeWithTag("text").performFirstLinkClick()

        assertEquals(actionData(url = "test://range"), actionHandler.handledAction)
    }

    @Test
    fun `tap on a range with disabled actions triggers element actions`() {
        setContent(
            text(
                id = "text",
                action = action(url = "test://element"),
                text = constant("first second"),
                ranges = listOf(
                    textRange(
                        start = 0,
                        end = 5,
                        actions = listOf(action(isEnabled = false, url = "test://range"))
                    )
                )
            )
        )

        rule.onNodeWithTag("text").performTouchInput { click(Offset(1f, centerY)) }

        assertEquals(actionData(url = "test://element"), actionHandler.handledAction)
    }

    @Test
    fun `tap on a range inside the ellipsis triggers range actions instead of ellipsis actions`() {
        setContent(
            truncatedText(
                ellipsis = DivText.Ellipsis(
                    actions = listOf(action(url = "test://ellipsis")),
                    ranges = listOf(
                        textRange(
                            start = 0,
                            end = "… more".length,
                            actions = listOf(action(url = "test://ellipsis_range"))
                        )
                    ),
                    text = constant("… more")
                )
            )
        )

        clickFirstLink()

        assertEquals(actionData(url = "test://ellipsis_range"), actionHandler.handledAction)
    }

    private fun truncatedText(
        action: DivAction? = null,
        ellipsis: DivText.Ellipsis,
        selectable: Boolean = false
    ) = text(
        id = "text",
        action = action,
        text = constant("A very long text that does not fit into a single line"),
        maxLines = 1,
        ellipsis = ellipsis,
        selectable = selectable,
        width = fixed(constant(100))
    )

    private fun clickFirstLink() {
        val bounds = requireNotNull(rule.onNodeWithTag("text").getFirstLinkBounds())
        rule.onNodeWithTag("text").performTouchInput { click(bounds.center) }
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }
}
