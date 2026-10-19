package com.yandex.div.compose.actions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.actionData
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivGallery
import com.yandex.div2.DivFocus
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithPressActionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()

    private val handledActions: List<DivActionData>
        get() = actionHandler.handledActions

    val configuration = DivConfiguration(
        actionHandler = actionHandler,
    )

    @Test
    fun `pressStartActions run on down`() {
        setContent(
            text(
                id = "button",
                text = constant("Button"),
                pressStartActions = listOf(action(url = "test://start")),
            )
        )

        pressButton()

        assertEquals(
            listOf(actionData(source = DivActionSource.GESTURE, url = "test://start")),
            handledActions
        )
    }

    @Test
    fun `pressEndActions run on up`() {
        setContent(
            text(
                id = "button",
                text = constant("Button"),
                pressEndActions = listOf(action(url = "test://end"))
            )
        )

        pressButton()
        releaseButton()

        assertEquals(
            listOf(actionData(source = DivActionSource.GESTURE, url = "test://end")),
            handledActions
        )
    }

    @Test
    fun `pressEndActions run on cancel`() {
        setContent(
            text(
                id = "button",
                text = constant("Button"),
                pressStartActions = listOf(action(url = "test://start")),
                pressEndActions = listOf(action(url = "test://end"))
            )
        )

        pressButton()
        rule.onNodeWithText("Button").performTouchInput { cancel() }

        assertEquals(
            listOf(
                actionData(source = DivActionSource.GESTURE, url = "test://start"),
                actionData(source = DivActionSource.GESTURE, url = "test://end"),
            ),
            handledActions
        )
    }


    @Test
    fun `pressEndActions restore text after press recomposition`() {
        setContent(
            text(
                id = "button",
                text = expression("@{is_pressed ? 'Pressed' : 'Not pressed'}"),
                variables = listOf(variable("is_pressed", false)),
                pressStartActions = listOf(
                    action(url = "div-action://set_variable?name=is_pressed&value=true")
                ),
                pressEndActions = listOf(
                    action(url = "div-action://set_variable?name=is_pressed&value=false")
                )
            )
        )

        pressButton()
        rule.onNodeWithTag("button").assertTextEquals("Pressed")
        releaseButton()
        rule.onNodeWithTag("button").assertTextEquals("Not pressed")
    }

    @Test
    fun `press actions run before tap actions`() {
        setContent(
            button(action = action(url = "test://tap")),
        )

        rule.onNodeWithTag("button").performTouchInput { click() }

        assertEquals(
            listOf(
                actionData(source = DivActionSource.GESTURE, url = "test://start"),
                actionData(source = DivActionSource.GESTURE, url = "test://end"),
                actionData(url = "test://tap"),
            ),
            handledActions
        )
    }

    @Test
    fun `element with press actions has no accessibility click`() {
        setContent(
            button()
        )

        rule.onNodeWithTag("button").assertHasNoClickAction()
    }

    @Test
    fun `press actions blocks parent tap actions`() {
        setContent(
            container(
                action = action(url = "test://parent"),
                items = listOf(button()),
            )
        )

        pressButton()
        releaseButton()

        assertEquals(
            listOf(
                actionData(source = DivActionSource.GESTURE, url = "test://start"),
                actionData(source = DivActionSource.GESTURE, url = "test://end"),
            ),
            handledActions
        )
    }

    @Test
    fun `pressStartActions are handled for element in gallery`() {
        setContent(
            gallery(
                height = fixed(50),
                orientation = constant(DivGallery.Orientation.VERTICAL),
                items = listOf(button(height = 200))
            )
        )

        rule.onNodeWithTag("button").performTouchInput {
            down(Offset(20f, 20f))
        }

        assertEquals(
            listOf(actionData(source = DivActionSource.GESTURE, url = "test://start")),
            handledActions
        )
    }

    @Test
    fun `longTapActions run with press actions`() {
        setContent(
            button(longTapActions = listOf(action(url = "test://long")))
        )

        rule.onNodeWithTag("button").performTouchInput {
            down(center)
            advanceEventTime(1000)
            up()
        }

        assertEquals(
            listOf(
                actionData(source = DivActionSource.GESTURE, url = "test://start"),
                actionData(source = DivActionSource.LONG_TAP, url = "test://long"),
                actionData(source = DivActionSource.GESTURE, url = "test://end"),
            ),
            handledActions
        )
    }

    @Test
    fun `doubleTapActions run after second press end`() {
        setContent(
            button(doubleTapActions = listOf(action(url = "test://double_tap")))
        )

        rule.onNodeWithTag("button").performTouchInput { doubleClick() }

        assertEquals(
            listOf(
                actionData(source = DivActionSource.GESTURE, url = "test://start"),
                actionData(source = DivActionSource.GESTURE, url = "test://end"),
                actionData(source = DivActionSource.GESTURE, url = "test://start"),
                actionData(source = DivActionSource.GESTURE, url = "test://end"),
                actionData(source = DivActionSource.DOUBLE_TAP, url = "test://double_tap"),
            ),
            handledActions
        )
    }

    @Test
    fun `accessibility click runs tap actions without touch press actions`() {
        setContent(
            button(action = action(url = "test://tap"))
        )

        rule.onNodeWithTag("button").performSemanticsAction(SemanticsActions.OnClick)

        assertEquals(
            listOf(actionData(url = "test://tap")),
            handledActions
        )
    }

    @Test
    fun `keyboard enter runs tap actions without touch press actions`() {
        setContent(
            button(action = action(url = "test://tap"), focus = DivFocus())
        )

        rule.onNodeWithTag("button").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("button").performKeyInput { pressKey(Key.Enter) }

        assertEquals(
            listOf(actionData(url = "test://tap")),
            handledActions
        )
    }

    @Test
    fun `element with press actions captures focus`() {
        setContent(button(focus = DivFocus()))

        pressButton()

        rule.onNodeWithTag("button").assertIsFocused()
    }

    private fun button(
        action: DivAction? = null,
        doubleTapActions: List<DivAction>? = null,
        longTapActions: List<DivAction>? = null,
        height: Int = 40,
        focus: DivFocus? = null,
    ): Div = text(
        id = "button",
        text = constant("Button"),
        height = fixed(height),
        focus = focus,
        action = action,
        doubleTapActions = doubleTapActions,
        longTapActions = longTapActions,
        pressStartActions = listOf(action(url = "test://start")),
        pressEndActions = listOf(action(url = "test://end"))
    )

    private fun setContent(content: Div) {
        rule.setContent(
            configuration = configuration,
            data = data(content),
        )
    }

    private fun pressButton() {
        rule.onNodeWithTag("button").performTouchInput { down(center) }
    }

    private fun releaseButton() {
        rule.onNodeWithTag("button").performTouchInput { up() }
    }
}
