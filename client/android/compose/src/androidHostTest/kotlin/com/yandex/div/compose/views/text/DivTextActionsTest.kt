package com.yandex.div.compose.views.text

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getFirstLinkBounds
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performFirstLinkClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.setContent
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textRange
import com.yandex.div.test.data.uriExpression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivText
import org.json.JSONArray
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
                width = fixed(100)
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
    fun `tap below the text height does not trigger a hidden range action`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                maxLines = 3,
                height = fixed(30),
                ranges = listOf(
                    textRange(start = 13, end = 18, actions = listOf(action(url = "test://hidden")))
                ),
            )
        )

        rule.onNodeWithTag("text").performTouchInput { click(Offset(5f, 60f)) }

        assertTrue(actionHandler.handledActions.isEmpty())
    }

    @Test
    fun `tap on a visible range triggers its action when later lines are clipped`() {
        setContent(
            text(
                id = "text",
                text = constant("first\nsecond\nthird"),
                fontSize = 20,
                maxLines = 3,
                height = fixed(30),
                ranges = listOf(
                    textRange(start = 0, end = 5, actions = listOf(action(url = "test://visible")))
                ),
            )
        )

        clickFirstLink()

        assertEquals(actionData(url = "test://visible"), actionHandler.handledAction)
    }

    @Test
    fun `tap on a range without bounds triggers range actions`() {
        setContent(
            text(
                id = "text",
                text = constant("first second"),
                ranges = listOf(textRange(actions = listOf(action(url = "test://range"))))
            )
        )

        rule.onNodeWithTag("text").performFirstLinkClick()

        assertEquals(actionData(url = "test://range"), actionHandler.handledAction)
    }

    @Test
    fun `tap on a range with end beyond the integer range triggers range actions`() {
        setContent(
            text(
                id = "text",
                text = constant("first second"),
                ranges = listOf(
                    textRange(start = 6, end = 2_147_483_648, actions = listOf(action(url = "test://range")))
                )
            )
        )

        rule.onNodeWithTag("text").performFirstLinkClick()

        assertEquals(actionData(url = "test://range"), actionHandler.handledAction)
    }

    @Test
    fun `range with start beyond the integer range does not create a link`() {
        setContent(
            text(
                id = "text",
                text = constant("first second"),
                ranges = listOf(
                    textRange(start = 4_294_967_296, actions = listOf(action(url = "test://range")))
                )
            )
        )

        assertNull(rule.onNodeWithTag("text").getFirstLinkBounds())
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
    fun `tap next to a range triggers element actions`() {
        setContent(
            text(
                id = "text",
                action = action(url = "test://element"),
                text = constant("first second"),
                ranges = listOf(
                    textRange(start = 0, end = 1, actions = listOf(action(url = "test://range")))
                )
            )
        )
        val rangeBounds = requireNotNull(rule.onNodeWithTag("text").getFirstLinkBounds())

        rule.onNodeWithTag("text").performTouchInput {
            click(Offset(rangeBounds.right + 10f, rangeBounds.center.y))
        }

        assertEquals(actionData(url = "test://element"), actionHandler.handledAction)
    }

    @Test
    fun `tap on the ellipsis does not trigger actions of a range hidden by truncation`() {
        setContent(
            text(
                id = "text",
                action = action(url = "test://element"),
                text = constant("A very long text that does not fit into a single line"),
                maxLines = 1,
                ranges = listOf(
                    textRange(start = 40, end = 50, actions = listOf(action(url = "test://range")))
                ),
                width = fixed(100)
            )
        )

        rule.onNodeWithTag("text").performTouchInput { click(Offset(right - 3f, centerY)) }

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
                            end = "… more".length.toLong(),
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

    @Test
    fun `tap on a built ellipsis range triggers its actions with its data element`() {
        setContent(
            truncatedText(
                ellipsis = DivText.Ellipsis(
                    rangeBuilder = rangeBuilder(
                        data = constant(jsonArray(jsonObject("start" to 2, "end" to 6, "id" to "more"))),
                        actions = listOf(action(url = uriExpression("test://@{it.getString('id')}"))),
                    ),
                    text = constant("… more")
                )
            )
        )

        rule.onNodeWithTag("text").performFirstLinkClick()

        assertEquals(actionData(url = "test://more"), actionHandler.handledAction)
    }

    @Test
    fun `tap next to a built ellipsis range triggers element actions`() {
        setContent(
            truncatedText(
                action = action(url = "test://element"),
                ellipsis = DivText.Ellipsis(
                    rangeBuilder = rangeBuilder(
                        data = constant(jsonArray(jsonObject("start" to 5, "end" to 6))),
                        actions = listOf(action(url = "test://range")),
                    ),
                    text = constant("… more")
                )
            )
        )
        val rangeBounds = requireNotNull(rule.onNodeWithTag("text").getFirstLinkBounds())

        rule.onNodeWithTag("text").performTouchInput {
            click(Offset(rangeBounds.left - 10f, rangeBounds.center.y))
        }

        assertEquals(actionData(url = "test://element"), actionHandler.handledAction)
    }

    private fun rangeBuilder(data: Expression<JSONArray>, actions: List<DivAction>) = DivText.RangeBuilder(
        data = data,
        prototypes = listOf(
            DivText.RangeBuilder.Prototype(
                range = DivText.Range(
                    actions = actions,
                    end = intExpression("@{it.getInteger('end')}"),
                    start = intExpression("@{it.getInteger('start')}"),
                ),
            )
        ),
    )

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
        width = fixed(100)
    )

    private fun clickFirstLink() {
        val bounds = requireNotNull(rule.onNodeWithTag("text").getFirstLinkBounds())
        rule.onNodeWithTag("text").performTouchInput { click(bounds.center) }
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }
}
