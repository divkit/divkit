package com.yandex.div.compose

import androidx.compose.runtime.ReusableContentHost
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isFocused
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.text.TextRange
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.action
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.container
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.input
import com.yandex.div.test.data.slider
import com.yandex.div.test.data.text
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivFocus
import com.yandex.div2.DivVisibility
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivViewFocusTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val visibility = Variable.StringVariable("visibility", "visible")
    private val enabled = Variable.BooleanVariable("enabled", true)
    private val captureFocus = Variable.BooleanVariable("capture_focus", true)
    private val reporter = TestReporter()

    private val variables = DivVariableController().apply {
        declare(visibility, enabled, captureFocus, Variable.StringVariable("input", ""))
    }

    private val configuration = DivConfiguration(
        actionHandler = actionHandler,
        reporter = reporter,
        variableController = variables,
    )

    private val focus = DivFocus(
        onFocus = listOf(action(id = "focus")),
        onBlur = listOf(action(id = "blur")),
    )

    @Test
    fun `initial unfocused state does not dispatch blur`() {
        setContent(
            text(id = "text", text = "Text", focus = focus)
        )

        rule.waitForIdle()

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `unsupported next focus ids are reported once`() {
        reporter.failOnError = false

        setContent(
            text(
                id = "text",
                text = "Text",
                focus = DivFocus(nextFocusIds = DivFocus.NextFocusIds(forward = constant("next"))),
            )
        )

        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)

        assertEquals(listOf("focus.next_focus_ids not supported"), reporter.errors)
    }

    @Test
    fun `input focus dispatches focus action`() {
        setContent(
            input(textVariable = "input", focus = focus)
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        assertEquals(
            actionData(id = "focus", source = DivActionSource.FOCUS),
            actionHandler.handledAction
        )
    }

    @Test
    fun `slider focus dispatches focus action`() {
        setContent(
            slider(id = "slider", focus = focus)
        )

        rule.onNodeWithTag("slider").performSemanticsAction(SemanticsActions.RequestFocus)

        assertEquals(actionData(id = "focus", source = DivActionSource.FOCUS), actionHandler.handledAction)
    }

    @Test
    fun `merged slider retains focus semantics`() {
        setContent(
            slider(
                id = "slider",
                focus = focus,
                accessibility = accessibility(mode = DivAccessibility.Mode.MERGE),
            )
        )

        rule.onNodeWithTag("slider").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("slider").assertIsFocused()

        assertEquals(
            listOf(actionData(id = "focus", source = DivActionSource.FOCUS)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `excluded slider has no focus semantics`() {
        setContent(
            slider(
                id = "slider",
                focus = focus,
                accessibility = accessibility(mode = DivAccessibility.Mode.EXCLUDE),
            )
        )

        rule.onNodeWithTag("slider")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.RequestFocus))

        rule.onNodeWithTag("slider")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Focused))
    }

    @Test
    fun `touch transfers input focus to slider`() {
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    slider(id = "slider", focus = focus),
                )
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("slider").performTouchInput { click() }

        rule.onNodeWithTag("slider").assertIsFocused()

        assertEquals(
            listOf(
                actionData(id = "blur", source = DivActionSource.BLUR),
                actionData(id = "focus", source = DivActionSource.FOCUS),
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `slider drag continues after gaining focus`() {
        val value = Variable.IntegerVariable("value", 0)
        variables.declare(value)
        setContent(
            slider(
                id = "slider",
                focus = focus,
                thumbValueVariable = "value",
            )
        )
        val slider = rule.onNodeWithTag("slider")

        slider.performTouchInput { down(centerLeft) }
        rule.waitForIdle()
        slider.performTouchInput {
            moveTo(centerRight)
            up()
        }

        assertEquals(
            listOf(actionData(id = "focus", source = DivActionSource.FOCUS)),
            actionHandler.handledActions
        )

        assertEquals(100L, value.getValue())
    }

    @Test
    fun `touching slider without focus settings preserves input focus`() {
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    slider(id = "slider"),
                )
            )
        )
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("slider").performTouchInput { click() }

        rule.onNode(hasSetTextAction()).assertIsFocused()
        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `touching disabled slider preserves input focus`() {
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    slider(id = "slider", focus = focus, isEnabled = constant(false)),
                )
            )
        )
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("slider").performTouchInput { click() }

        rule.onNode(hasSetTextAction()).assertIsFocused()
        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `touch focuses reenabled slider`() {
        enabled.set(false)
        setContent(
            slider(
                id = "slider",
                focus = focus,
                isEnabled = booleanExpression("@{enabled}"),
            )
        )
        enabled.set(true)

        rule.onNodeWithTag("slider").performTouchInput { click() }

        rule.onNodeWithTag("slider").assertIsFocused()

        assertEquals(
            listOf(actionData(id = "focus", source = DivActionSource.FOCUS)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `touching slider under invisible parent preserves input focus`() {
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    container(
                        visibility = constant(DivVisibility.INVISIBLE),
                        items = listOf(
                            slider(id = "slider", focus = focus),
                        )
                    ),
                )
            )
        )
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("slider").performTouchInput { click() }

        rule.onNode(hasSetTextAction()).assertIsFocused()
        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `disabling slider clears focus`() {
        setContent(
            slider(
                id = "slider",
                focus = focus,
                isEnabled = booleanExpression("@{enabled}"),
            )
        )
        rule.onNodeWithTag("slider").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        enabled.set(false)

        rule.onNodeWithTag("slider").assert(isFocused().not())

        assertEquals(
            listOf(actionData(id = "blur", source = DivActionSource.BLUR)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `disabling action preserves element focus`() {
        setContent(
            container(
                items = listOf(
                    text(id = "other", text = "Other", focus = DivFocus()),
                    text(
                        id = "text",
                        text = "Text",
                        focus = focus,
                        action = action(id = "tap").copy(isEnabled = booleanExpression("@{enabled}"))
                    ),
                )
            )
        )
        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        enabled.set(false)

        rule.onNodeWithTag("text").assertIsFocused()
        rule.onNodeWithTag("text").performKeyInput { pressKey(Key.Enter) }
        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `enabling action preserves element focus`() {
        enabled.set(false)
        setContent(
            container(
                items = listOf(
                    text(id = "other", text = "Other", focus = DivFocus()),
                    text(
                        id = "text",
                        text = "Text",
                        focus = focus,
                        action = action(id = "tap").copy(isEnabled = booleanExpression("@{enabled}"))
                    ),
                )
            )
        )
        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        enabled.set(true)

        rule.onNodeWithTag("text").assertIsFocused()

        assertEquals(emptyList(), actionHandler.handledActions)

        rule.onNodeWithTag("text").performKeyInput { pressKey(Key.Enter) }

        assertEquals(
            listOf(actionData(id = "tap", source = DivActionSource.TAP)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `disabled action consumes tap without invoking parent action`() {
        setContent(
            container(
                action = action(id = "parent"),
                items = listOf(
                    text(
                        id = "text",
                        text = "Text",
                        focus = DivFocus(),
                        action = action(id = "tap").copy(isEnabled = constant(false)),
                    )
                ),
            )
        )

        rule.onNodeWithTag("text").assertHasClickAction().assertIsEnabled()
        rule.onNodeWithTag("text").performTouchInput { click() }

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `requesting current focus does not repeat callbacks`() {
        setContent(
            text(id = "text", text = "Text", focus = focus)
        )

        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `keyboard activates focused element action`() {
        setContent(
            text(
                id = "text",
                text = "Text",
                focus = focus,
                action = action(id = "tap"),
            )
        )

        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("text").performKeyInput { pressKey(Key.Enter) }

        assertEquals(
            listOf(actionData(id = "tap", source = DivActionSource.TAP)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `touch focuses actionable element before tap action`() {
        setContent(
            container(
                items = listOf(
                    text(id = "other", text = "Other", focus = DivFocus()),
                    text(id = "text", text = "Text", focus = focus, action = action(id = "tap")),
                )
            )
        )
        rule.onNodeWithTag("other").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("text").performTouchInput { click() }

        rule.onNodeWithTag("text").assertIsFocused()

        assertEquals(
            listOf(
                actionData(id = "focus", source = DivActionSource.FOCUS),
                actionData(id = "tap", source = DivActionSource.TAP),
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `tap preserves input focus when capture focus on action is false`() {
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    text(
                        id = "text",
                        text = "Text",
                        focus = focus,
                        action = action(id = "tap"),
                        captureFocusOnAction = constant(false)
                    ),
                )
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("text").performTouchInput { click() }

        rule.onNode(hasSetTextAction()).assertIsFocused()

        assertEquals(
            listOf(actionData(id = "tap", source = DivActionSource.TAP)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `tap preserves input focus when capture focus expression becomes false`() {
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    text(
                        id = "text",
                        text = "Text",
                        focus = focus,
                        action = action(id = "tap"),
                        captureFocusOnAction = booleanExpression("@{capture_focus}")
                    ),
                )
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()
        captureFocus.set(false)

        rule.onNodeWithTag("text").performTouchInput { click() }

        rule.onNode(hasSetTextAction()).assertIsFocused()

        assertEquals(
            listOf(actionData(id = "tap", source = DivActionSource.TAP)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `tap moves focus before action when capture focus expression becomes true`() {
        captureFocus.set(false)
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    text(
                        id = "text",
                        text = "Text",
                        focus = focus,
                        action = action(id = "tap"),
                        captureFocusOnAction = booleanExpression("@{capture_focus}")
                    ),
                )
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()
        captureFocus.set(true)

        rule.onNodeWithTag("text").performTouchInput { click() }

        rule.onNodeWithTag("text").assertIsFocused()
        assertEquals(
            listOf(
                actionData(id = "blur", source = DivActionSource.BLUR),
                actionData(id = "focus", source = DivActionSource.FOCUS),
                actionData(id = "tap", source = DivActionSource.TAP),
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `long tap preserves input focus when capture focus on action is false`() {
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    text(
                        id = "text",
                        text = "Text",
                        focus = focus,
                        longTapActions = listOf(action(id = "long_tap")),
                        captureFocusOnAction = constant(false)
                    ),
                )
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("text").performTouchInput { longClick() }

        rule.onNode(hasSetTextAction()).assertIsFocused()

        assertEquals(
            listOf(actionData(id = "long_tap", source = DivActionSource.LONG_TAP)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `double tap preserves input focus when capture focus on action is false`() {
        setContent(
            container(
                items = listOf(
                    input(textVariable = "input", focus = focus),
                    text(
                        id = "text",
                        text = "Text",
                        focus = focus,
                        doubleTapActions = listOf(action(id = "double_tap")),
                        captureFocusOnAction = constant(false)
                    ),
                )
            )
        )
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("text").performTouchInput { doubleClick() }

        rule.onNode(hasSetTextAction()).assertIsFocused()

        assertEquals(
            listOf(actionData(id = "double_tap", source = DivActionSource.DOUBLE_TAP)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `touch focuses element before long tap action`() {
        setContent(
            text(
                id = "text",
                text = "Text",
                focus = focus,
                longTapActions = listOf(action(id = "long_tap")),
            )
        )

        rule.onNodeWithTag("text").performTouchInput { longClick() }

        rule.onNodeWithTag("text").assertIsFocused()

        assertEquals(
            listOf(
                actionData(id = "focus", source = DivActionSource.FOCUS),
                actionData(id = "long_tap", source = DivActionSource.LONG_TAP),
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `touch focuses element before double tap action`() {
        setContent(
            text(
                id = "text",
                text = "Text",
                focus = focus,
                action = action(id = "tap"),
                doubleTapActions = listOf(action(id = "double_tap")),
            )
        )

        rule.onNodeWithTag("text").performTouchInput { doubleClick() }

        rule.onNodeWithTag("text").assertIsFocused()

        assertEquals(
            listOf(
                actionData(id = "focus", source = DivActionSource.FOCUS),
                actionData(id = "double_tap", source = DivActionSource.DOUBLE_TAP),
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `single tap with only double tap actions preserves current focus`() {
        setContent(
            container(
                items = listOf(
                    text(id = "other", text = "Other", focus = DivFocus()),
                    text(
                        id = "text",
                        text = "Text",
                        focus = focus,
                        doubleTapActions = listOf(action(id = "double_tap"))
                    ),
                )
            )
        )
        rule.onNodeWithTag("other").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("text").performTouchInput { click() }
        rule.mainClock.advanceTimeBy(500)

        rule.onNodeWithTag("other").assertIsFocused()
        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `focus callback can enable current tap action`() {
        enabled.set(false)

        setContent(
            text(
                id = "text",
                text = "Text",
                focus = DivFocus(onFocus = listOf(action(url = "div-action://set_variable?name=enabled&value=true"))),
                action = action(id = "tap").copy(isEnabled = booleanExpression("@{enabled}")),
            )
        )

        rule.onNodeWithTag("text").performTouchInput { click() }

        assertEquals(
            listOf(actionData(id = "tap", source = DivActionSource.TAP)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `focus callback can disable current tap action`() {
        setContent(
            text(
                id = "text",
                text = "Text",
                focus = DivFocus(onFocus = listOf(action(url = "div-action://set_variable?name=enabled&value=false"))),
                action = action(id = "tap").copy(isEnabled = booleanExpression("@{enabled}")),
            )
        )

        rule.onNodeWithTag("text").performTouchInput { click() }

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `losing focus cancels pending keyboard click`() {
        setContent(
            container(
                items = listOf(
                    text(id = "first", text = "First", focus = DivFocus(), action = action(id = "tap")),
                    text(id = "second", text = "Second", focus = DivFocus()),
                )
            )
        )

        rule.onNodeWithTag("first").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("first").performKeyInput { keyDown(Key.Enter) }
        rule.onNodeWithTag("second").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("first").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("first").performKeyInput { keyUp(Key.Enter) }

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `losing focus cancels pending keyboard long click`() {
        setContent(
            container(
                items = listOf(
                    text(
                        id = "first",
                        text = "First",
                        focus = DivFocus(),
                        longTapActions = listOf(action(id = "long_tap"))
                    ),
                    text(id = "second", text = "Second", focus = DivFocus()),
                )
            )
        )

        rule.onNodeWithTag("first").performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNodeWithTag("first").performKeyInput { keyDown(Key.Enter) }
        rule.onNodeWithTag("second").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.mainClock.advanceTimeBy(1000)
        rule.onNodeWithTag("second").performKeyInput { keyUp(Key.Enter) }

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `tab visits actionable element once`() {
        setContent(
            container(
                items = listOf(
                    text(id = "first", text = "First", focus = focus, action = action(id = "tap")),
                    text(id = "second", text = "Second", focus = focus),
                )
            )
        )

        rule.onNodeWithTag("first").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("first").performKeyInput { pressKey(Key.Tab) }

        rule.onNodeWithTag("second").assertIsFocused()

        assertEquals(
            listOf(
                actionData(id = "blur", source = DivActionSource.BLUR),
                actionData(id = "focus", source = DivActionSource.FOCUS),
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `moving focus dispatches blur before next focus`() {
        setContent(
            container(
                items = listOf(
                    text(id = "first", text = "First", focus = focus),
                    text(id = "second", text = "Second", focus = focus),
                )
            )
        )

        rule.onNodeWithTag("first").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.onNodeWithTag("second").performSemanticsAction(SemanticsActions.RequestFocus)

        assertEquals(
            listOf(
                actionData(id = "blur", source = DivActionSource.BLUR),
                actionData(id = "focus", source = DivActionSource.FOCUS),
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `focusing child does not focus its parent`() {
        setContent(
            container(
                focus = focus,
                items = listOf(input(textVariable = "input")),
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `invisible element loses focus once`() {
        setContent(
            text(
                id = "text",
                text = "Text",
                focus = focus,
                visibility = visibilityExpression("@{visibility}"),
            )
        )

        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        visibility.set("invisible")
        rule.waitForIdle()

        assertEquals(
            listOf(actionData(id = "blur", source = DivActionSource.BLUR)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `removed element dispatches blur once`() {
        setContent(
            text(
                id = "text",
                text = "Text",
                focus = focus,
                visibility = visibilityExpression("@{visibility}"),
            )
        )

        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        visibility.set("gone")
        rule.waitForIdle()

        assertEquals(
            listOf(actionData(id = "blur", source = DivActionSource.BLUR)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `input loses focus when focusable parent becomes invisible`() {
        setContent(
            container(
                focus = DivFocus(),
                visibility = visibilityExpression("@{visibility}"),
                items = listOf(input(textVariable = "input")),
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        visibility.set("invisible")

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun `enter skips input inside invisible ancestor with focusable child`() {
        setContent(
            container(
                id = "parent",
                focus = DivFocus(),
                items = listOf(
                    container(
                        visibility = constant(DivVisibility.INVISIBLE),
                        items = listOf(
                            container(focus = DivFocus(), items = listOf(input(textVariable = "input"))),
                        )
                    ),
                    text(id = "after", text = "After", focus = DivFocus()),
                ),
            )
        )

        rule.onNodeWithTag("parent").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("parent").performKeyInput { pressKey(Key.DirectionCenter) }

        rule.onNodeWithTag("after").assertIsFocused()
    }

    @Test
    fun `tab skips input inside invisible ancestor with focusable child`() {
        setContent(
            container(
                items = listOf(
                    text(id = "before", text = "Before", focus = DivFocus()),
                    container(
                        visibility = constant(DivVisibility.INVISIBLE),
                        items = listOf(
                            container(focus = DivFocus(), items = listOf(input(textVariable = "input"))),
                        )
                    ),
                    text(id = "after", text = "After", focus = DivFocus()),
                )
            )
        )

        rule.onNodeWithTag("before").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("before").performKeyInput { pressKey(Key.Tab) }

        rule.onNodeWithTag("after").assertIsFocused()
    }

    @Test
    fun `nested input cannot gain focus under invisible ancestor`() {
        setContent(
            container(
                visibility = constant(DivVisibility.INVISIBLE),
                items = listOf(
                    container(
                        focus = DivFocus(),
                        items = listOf(
                            input(textVariable = "input"),
                        )
                    )
                ),
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun `nested input dispatches blur once when ancestor becomes invisible`() {
        setContent(
            container(
                visibility = visibilityExpression("@{visibility}"),
                items = listOf(
                    container(
                        focus = DivFocus(),
                        items = listOf(
                            input(textVariable = "input", focus = focus),
                        )
                    )
                ),
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        rule.runOnIdle { visibility.set("invisible") }
        rule.waitForIdle()

        assertEquals(
            listOf(actionData(id = "blur", source = DivActionSource.BLUR)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `hiding sibling preserves current focus`() {
        setContent(
            container(
                items = listOf(
                    text(id = "outside", text = "Outside", focus = DivFocus()),
                    container(
                        visibility = visibilityExpression("@{visibility}"),
                        items = listOf(
                            input(textVariable = "input"),
                        )
                    ),
                )
            )
        )

        rule.onNodeWithTag("outside").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.runOnIdle { visibility.set("invisible") }

        rule.onNodeWithTag("outside").assertIsFocused()
    }

    @Test
    fun `shift tab skips input inside invisible ancestor with focusable child`() {
        setContent(
            container(
                items = listOf(
                    text(id = "before", text = "Before", focus = DivFocus()),
                    container(
                        visibility = constant(DivVisibility.INVISIBLE),
                        items = listOf(
                            container(
                                focus = DivFocus(),
                                items = listOf(input(textVariable = "input")),
                            ),
                        )
                    ),
                    text(id = "after", text = "After", focus = DivFocus()),
                )
            )
        )

        rule.onNodeWithTag("after").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("after").performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
        }

        rule.onNodeWithTag("before").assertIsFocused()
    }

    @Test
    fun `revealed nested input can gain focus`() {
        visibility.set("invisible")
        setContent(
            container(
                visibility = visibilityExpression("@{visibility}"),
                items = listOf(
                    container(
                        focus = DivFocus(),
                        items = listOf(
                            input(textVariable = "input"),
                        )
                    )
                ),
            )
        )

        rule.runOnIdle { visibility.set("visible") }

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNode(hasSetTextAction()).assertIsFocused()
    }

    @Test
    fun `reactivated input respects visibility changed while inactive`() {
        val active = mutableStateOf(true)
        val divData = data(
            container(
                visibility = visibilityExpression("@{visibility}"),
                items = listOf(input(textVariable = "input")),
            )
        )
        rule.setContentWithDivContext(configuration) {
            ReusableContentHost(active.value) {
                DivView(divData)
            }
        }
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        rule.onNode(hasSetTextAction()).assertIsFocused()
        rule.runOnIdle { active.value = false }
        rule.waitForIdle()
        visibility.set("invisible")
        rule.runOnIdle { active.value = true }

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun `captured input loses focus when parent becomes invisible`() {
        val requester = FocusRequester()
        rule.setContentWithDivContext(configuration) {
            DivView(
                data = data(
                    container(
                        visibility = visibilityExpression("@{visibility}"),
                        items = listOf(input(textVariable = "input", focus = focus)),
                    )
                ),
                modifier = Modifier.focusRequester(requester),
            )
        }
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        rule.runOnIdle { assertTrue(requester.captureFocus()) }
        actionHandler.reset()

        visibility.set("invisible")

        rule.onNode(hasSetTextAction()).assertIsNotFocused()

        assertEquals(
            listOf(actionData(id = "blur", source = DivActionSource.BLUR)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `revealing nested parent keeps invisible ancestor focus restriction`() {
        visibility.set("invisible")
        setContent(
            container(
                visibility = constant(DivVisibility.INVISIBLE),
                items = listOf(
                    container(
                        focus = DivFocus(),
                        visibility = visibilityExpression("@{visibility}"),
                        items = listOf(input(textVariable = "input")),
                    )
                ),
            )
        )
        visibility.set("visible")

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun `input loses focus when revealed ancestor becomes invisible again`() {
        visibility.set("invisible")
        setContent(
            container(
                visibility = visibilityExpression("@{visibility}"),
                items = listOf(
                    container(
                        focus = DivFocus(),
                        items = listOf(
                            input(textVariable = "input", focus = focus),
                        )
                    )
                ),
            )
        )
        visibility.set("visible")
        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)
        actionHandler.reset()

        visibility.set("invisible")

        rule.onNode(hasSetTextAction()).assertIsNotFocused()

        assertEquals(
            listOf(actionData(id = "blur", source = DivActionSource.BLUR)),
            actionHandler.handledActions
        )
    }

    @Test
    fun `input cannot gain focus under constant invisible parent`() {
        setContent(
            container(
                visibility = constant(DivVisibility.INVISIBLE),
                items = listOf(input(textVariable = "input")),
            )
        )

        rule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNode(hasSetTextAction()).assertIsNotFocused()
    }

    @Test
    fun `input cursor position is preserved when parent becomes invisible`() {
        variables.putOrUpdate(Variable.StringVariable("input", "Text"))
        setContent(
            container(
                visibility = visibilityExpression("@{visibility}"),
                items = listOf(input(textVariable = "input")),
            )
        )
        val field = rule.onNode(hasSetTextAction())
        field.performSemanticsAction(SemanticsActions.RequestFocus)
        field.performTextInputSelection(TextRange(1))

        visibility.set("invisible")

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1)))
    }

    @Test
    fun `input cursor position is preserved when constant visibility becomes expression`() {
        variables.putOrUpdate(Variable.StringVariable("input", "Text"))
        val child = input(textVariable = "input")
        val model = mutableStateOf(data(container(items = listOf(child))))
        rule.setContentWithDivContext(configuration) { DivView(model.value) }
        val field = rule.onNode(hasSetTextAction())
        field.performSemanticsAction(SemanticsActions.RequestFocus)
        field.performTextInputSelection(TextRange(1))

        rule.runOnIdle {
            model.value = data(
                container(
                    visibility = visibilityExpression("@{visibility}"),
                    items = listOf(child),
                )
            )
        }

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1)))
    }

    @Test
    fun `input cursor position is preserved when visibility expression becomes constant`() {
        variables.putOrUpdate(Variable.StringVariable("input", "Text"))
        val child = input(textVariable = "input")
        val model = mutableStateOf(
            data(
                container(
                    visibility = visibilityExpression("@{visibility}"),
                    items = listOf(child),
                )
            )
        )
        rule.setContentWithDivContext(configuration) { DivView(model.value) }
        val field = rule.onNode(hasSetTextAction())
        field.performSemanticsAction(SemanticsActions.RequestFocus)
        field.performTextInputSelection(TextRange(1))

        rule.runOnIdle { model.value = data(container(items = listOf(child))) }

        field.assert(SemanticsMatcher.expectValue(SemanticsProperties.TextSelectionRange, TextRange(1)))
    }

    @Test
    fun `focus actions resolve local variables`() {
        setContent(
            text(
                id = "text",
                text = expression("@{value}"),
                variables = listOf(variable("value", "Before")),
                focus = DivFocus(onFocus = listOf(action(url = "div-action://set_variable?name=value&value=After"))),
            )
        )

        rule.onNodeWithTag("text").performSemanticsAction(SemanticsActions.RequestFocus)

        rule.onNodeWithTag("text").assertTextEquals("After")
    }

    private fun setContent(content: Div) {
        rule.setContent(
            configuration = configuration,
            data = data(content),
        )
    }
}
