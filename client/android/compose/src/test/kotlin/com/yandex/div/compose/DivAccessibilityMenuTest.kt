package com.yandex.div.compose

import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeProvider
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.core.net.toUri
import androidx.core.view.descendants
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.accessibilityMenuItem
import com.yandex.div.test.data.accessibilityModeExpression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.input
import com.yandex.div.test.data.menuItem
import com.yandex.div.test.data.select
import com.yandex.div.test.data.selectOption
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.slider
import com.yandex.div.test.data.switch
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibilityMenuItem
import com.yandex.div2.DivAction
import com.yandex.div2.DivFocus
import com.yandex.div2.DivTypedValue
import com.yandex.div2.StrValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivAccessibilityMenuTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val variableController = DivVariableController()
    private val configuration = DivConfiguration(
        reporter = TestReporter(), variableController = variableController,
    )
    private val content = mutableStateOf(text(text = ""))

    @Test
    fun `menu preserves order and repeated labels while hiding unavailable commands`() {
        setContent(text(
            id = "target",
            text = "Card",
            accessibility = accessibility(menu = listOf(
                accessibilityMenuItem("First", listOf(action(url = "test://first"))),
                accessibilityMenuItem("Empty", emptyList()),
                accessibilityMenuItem("Disabled", listOf(action(isEnabled = false))),
                accessibilityMenuItem("First", listOf(action(url = "test://second"))),
                accessibilityMenuItem("First (2)", listOf(action(url = "test://numbered"))),
                accessibilityMenuItem("Last", listOf(action(url = "test://last"))),
            )),
        ))

        val commands = rule.onNodeWithTag("target").fetchSemanticsNode()
            .config[SemanticsActions.CustomActions]

        assertEquals(listOf("First", "First (3)", "First (2)", "Last"), commands.map { it.label })
    }

    @Test
    fun `second repeated label performs its own action through Android accessibility`() {
        setContent(text(id = "target", text = "Card", accessibility = accessibility(menu = listOf(
            accessibilityMenuItem("Open", listOf(action(
                url = "div-action://set_variable?name=last_action&value=first"))),
            accessibilityMenuItem("Open", listOf(action(
                url = "div-action://set_variable?name=last_action&value=second"))),
        ))))
        val nodeId = rule.onNodeWithTag("target").fetchSemanticsNode().id
        val provider = accessibilityProvider()
        val node = assertNotNull(provider.createAccessibilityNodeInfo(nodeId))
        val actions = node.actionList.filter { it.label != null }

        assertEquals(listOf("Open", "Open (2)"), actions.map { it.label.toString() })
        rule.runOnIdle { assertTrue(provider.performAction(nodeId, actions[1].id, null)) }
        rule.onNodeWithTag("result").assertTextEquals("second")
    }

    @Test
    fun `disabled input does not publish a command`() {
        variableController.declare(Variable.StringVariable("input", ""))
        setContent(input(
            id = "target", textVariable = "input", isEnabled = constant(false),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ))

        assertEquals(emptyList(), commands())
    }

    @Test
    fun `menu limits platform actions after filtering unavailable commands`() {
        val reporter = configuration.reporter as TestReporter
        setContent(text(id = "target", text = "Card", accessibility = accessibility(menu =
            listOf(accessibilityMenuItem("Disabled", listOf(action(isEnabled = false)))) +
                List(32) { index -> accessibilityMenuItem("Command $index", listOf(action())) }
        )))

        val nodeId = rule.onNodeWithTag("target").fetchSemanticsNode().id
        val node = assertNotNull(accessibilityProvider().createAccessibilityNodeInfo(nodeId))

        assertEquals(List(31) { index -> "Command $index" },
            node.actionList.mapNotNull { it.label?.toString() })
        assertTrue(reporter.warnings.any { it.contains("31") })
    }

    @Test
    fun `named command changes its variable without invoking the primary action`() {
        setContent(text(
            id = "target",
            text = "Card",
            action = action(url = "div-action://set_variable?name=last_action&value=tap"),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem(
                "Run",
                listOf(action(url = "div-action://set_variable?name=last_action&value=command")),
            ))),
        ))
        val commands = rule.onNodeWithTag("target").fetchSemanticsNode()
            .config[SemanticsActions.CustomActions]

        rule.runOnIdle { assertTrue(commands.single { it.label == "Run" }.action()) }

        rule.onNodeWithTag("result").assertTextEquals("command")
    }

    @Test
    fun `primary accessibility activation keeps the regular tap action`() {
        setContent(text(
            id = "target", text = "Card",
            action = action(url = "div-action://set_variable?name=last_action&value=tap"),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem(
                "Run", listOf(action(url = "div-action://set_variable?name=last_action&value=command")),
            ))),
        ))

        rule.onNodeWithTag("target").performSemanticsAction(SemanticsActions.OnClick)

        rule.onNodeWithTag("result").assertTextEquals("tap")
    }

    @Test
    fun `primary accessibility activation executes actions in order when menus are disabled`() {
        setContent(text(id = "target", text = constant("Card"), actions = listOf(
            action(isEnabled = false, menuItems = listOf(menuItem(text = "Disabled", action = action()))),
            action(typed = setVariableAction("last_action", typedValue("first"))),
            action(typed = setVariableAction("last_action", DivTypedValue.Str(
                StrValue(value = expression("@{last_action + '-second'}")),
            ))),
            action(isEnabled = false, typed = setVariableAction("last_action", typedValue("disabled"))),
        )))

        rule.onNodeWithTag("target").performSemanticsAction(SemanticsActions.OnClick)

        rule.onNodeWithTag("result").assertTextEquals("first-second")
    }

    @Test
    fun `label expression changes the published command`() {
        val label = Variable.StringVariable("menu_label", "Before")
        variableController.declare(label)
        setContent(text(id = "target", text = "Card", accessibility = DivAccessibility(
            menu = listOf(DivAccessibilityMenuItem(
                label = expression("@{menu_label}"), actions = listOf(action()),
            )),
        )))

        rule.runOnIdle { label.setValueDirectly("After") }

        assertEquals(listOf("After"), commands().map { it.label })
    }

    @Test
    fun `disabling actions removes the command and rejects its retained callback`() {
        val enabled = Variable.BooleanVariable("enabled", true)
        variableController.declare(enabled)
        setContent(text(id = "target", text = "Card", accessibility = accessibility(
            menu = listOf(accessibilityMenuItem("Run", listOf(DivAction(
                isEnabled = booleanExpression("@{enabled}"),
                url = constant("div-action://set_variable?name=last_action&value=command".toUri()),
            )))),
        )))
        val command = commands().single()

        rule.runOnIdle { enabled.setValueDirectly(false) }

        assertEquals(emptyList(), commands())
        rule.runOnIdle { assertFalse(command.action()) }
        rule.onNodeWithTag("result").assertTextEquals("none")
    }

    @Test
    fun `replacement data publishes new commands and rejects the retained callback`() {
        setContent(commandTarget("Old", "old"))
        val oldCommand = commands().single()

        rule.runOnIdle { content.value = commandTarget("New", "new") }

        assertEquals(listOf("New"), commands().map { it.label })
        rule.runOnIdle { assertFalse(oldCommand.action()) }
    }

    @Test
    fun `removing accessibility clears commands`() {
        setContent(commandTarget("Old", "old"))

        rule.runOnIdle { content.value = text(id = "target", text = "Card") }

        assertEquals(emptyList(), commands())
    }

    @Test
    fun `merged container publishes only its own menu`() {
        setContent(container(
            id = "target",
            items = listOf(commandTarget("Child", "child")),
            accessibility = accessibility(mode = DivAccessibility.Mode.MERGE, menu = listOf(
                accessibilityMenuItem("Parent", listOf(action())),
            )),
        ))

        assertEquals(listOf("Parent"), commands().map { it.label })
    }

    @Test
    fun `excluded ancestor publishes no descendant commands`() {
        setContent(container(
            accessibility = accessibility(mode = DivAccessibility.Mode.EXCLUDE),
            items = listOf(commandTarget("Run", "command")),
        ))

        val node = rule.onNodeWithTag("target", useUnmergedTree = true).fetchSemanticsNode()

        assertEquals(null, node.config.getOrElseNullable(SemanticsActions.CustomActions) { null })
    }

    @Test
    fun `slider thumbs expose separate menus alongside standard progress actions`() {
        val start = Variable.IntegerVariable("start", 30L)
        val end = Variable.IntegerVariable("end", 70L)
        variableController.declare(start, end)
        setContent(slider(
            id = "target", thumbValueVariable = "start", thumbSecondaryValueVariable = "end",
            accessibility = accessibility(description = "Price", menu = listOf(accessibilityMenuItem("Start", listOf(action())))),
            secondaryValueAccessibility = accessibility(description = "Maximum", menu = listOf(accessibilityMenuItem("End", listOf(action())))),
        ))
        val main = rule.onNode(hasContentDescription("Price,Range start"))
        val secondary = rule.onNode(hasContentDescription("Maximum"))

        main.performSemanticsAction(SemanticsActions.SetProgress) { assertTrue(it(35f)) }

        assertEquals(35L, start.getValue())
        assertEquals(listOf("Start"), main.fetchSemanticsNode().config[SemanticsActions.CustomActions].map { it.label })
        assertEquals(listOf("End"), secondary.fetchSemanticsNode().config[SemanticsActions.CustomActions].map { it.label })
    }

    @Test
    fun `secondary slider mode changes preserve the primary command callback`() {
        val mode = Variable.StringVariable("secondary_mode", "default")
        variableController.declare(mode, Variable.IntegerVariable("start", 30L),
            Variable.IntegerVariable("end", 70L))
        setContent(slider(
            thumbValueVariable = "start", thumbSecondaryValueVariable = "end",
            accessibility = accessibility(description = "Price", menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
            secondaryValueAccessibility = DivAccessibility(
                mode = accessibilityModeExpression("@{secondary_mode}"),
            ),
        ))
        val command = rule.onNode(hasContentDescription("Price,Range start"))
            .fetchSemanticsNode().config[SemanticsActions.CustomActions].single()

        rule.runOnIdle { mode.setValueDirectly("exclude") }

        rule.runOnIdle { assertTrue(command.action()) }
    }

    @Test
    fun `switch keeps its standard click when it has a named command`() {
        val toggle = Variable.BooleanVariable("toggle", false)
        variableController.declare(toggle)
        setContent(switch(
            id = "target", isOnVariable = "toggle",
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ))

        rule.onNodeWithTag("target").performSemanticsAction(SemanticsActions.OnClick)

        assertTrue(toggle.getValue() as Boolean)
        assertEquals(listOf("Run"), commands().map { it.label })
    }

    @Test
    fun `select keeps its standard popup when it has a named command`() {
        variableController.declare(Variable.StringVariable("selection", "first"))
        setContent(select(
            id = "target", valueVariable = "selection",
            options = listOf(selectOption("first", "First"), selectOption("second", "Second")),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ))

        rule.onNodeWithTag("target").performSemanticsAction(SemanticsActions.OnClick)

        rule.onNodeWithText("Second").assertIsDisplayed()
        assertEquals(listOf("Run"), commands().map { it.label })
    }

    @Test
    fun `named command does not capture configured focus`() {
        setContent(text(
            id = "target", text = "Card", focus = DivFocus(),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ))

        performCommand("Run")

        rule.onNodeWithTag("target").assertIsNotFocused()
    }

    @Test
    fun `command uses element local variables`() {
        variableController.declare(Variable.StringVariable("message", "global"))
        setContent(text(
            id = "target", text = expression("@{message}"),
            variables = listOf(variable("message", "local")),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(
                action(url = "div-action://set_variable?name=message&value=updated"),
            )))),
        ))

        performCommand("Run")

        rule.onNodeWithTag("target").assertTextEquals("updated")
    }

    @Test
    fun `mode expression hides commands and rejects a retained callback`() {
        val mode = Variable.StringVariable("menu_mode", "default")
        variableController.declare(mode)
        setContent(text(id = "target", text = "Card", accessibility = DivAccessibility(
            mode = accessibilityModeExpression("@{menu_mode}"),
            menu = listOf(accessibilityMenuItem("Run", listOf(action()))),
        )))
        val command = commands().single()

        rule.runOnIdle { mode.setValueDirectly("exclude") }

        assertEquals(emptyList(), commands())
        rule.runOnIdle { assertFalse(command.action()) }
    }

    @Test
    fun `named command executes actions in order even when an action has menu items`() {
        setContent(text(id = "target", text = "Card", accessibility = accessibility(menu = listOf(
            accessibilityMenuItem("Run", listOf(
                action(
                    typed = setVariableAction("last_action", typedValue("first")),
                    menuItems = listOf(menuItem(text = "Choose", action = action())),
                ),
                action(typed = setVariableAction("last_action", DivTypedValue.Str(
                    StrValue(value = expression("@{last_action + '-second'}")),
                ))),
            )),
        ))))

        performCommand("Run")

        rule.onNodeWithTag("result").assertTextEquals("first-second")
    }

    @Test
    fun `invisible element removes commands and rejects a retained callback`() {
        val visibility = Variable.StringVariable("visibility", "visible")
        variableController.declare(visibility)
        setContent(text(
            id = "target", text = "Card", visibility = visibilityExpression("@{visibility}"),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(
                action(url = "div-action://set_variable?name=last_action&value=command"),
            )))),
        ))
        val command = commands().single()

        rule.runOnIdle { visibility.setValueDirectly("invisible") }

        assertEquals(emptyList(), commands())
        rule.runOnIdle { assertFalse(command.action()) }
        rule.onNodeWithTag("result").assertTextEquals("none")
    }

    @Test
    fun `command executes enabled typed actions in order`() {
        setContent(text(id = "target", text = "Card", accessibility = accessibility(menu = listOf(
            accessibilityMenuItem("Run", listOf(
                action(typed = setVariableAction("last_action", typedValue("first"))),
                action(typed = setVariableAction("last_action", DivTypedValue.Str(
                    StrValue(value = expression("@{last_action + '-second'}")),
                ))),
                action(isEnabled = false, typed = setVariableAction("last_action", typedValue("disabled"))),
            )),
        ))))

        performCommand("Run")

        rule.onNodeWithTag("result").assertTextEquals("first-second")
    }

    private fun commandTarget(label: String, value: String): Div = text(
        id = "target", text = "Card",
        accessibility = accessibility(menu = listOf(accessibilityMenuItem(
            label, listOf(action(url = "div-action://set_variable?name=last_action&value=$value")),
        ))),
    )

    private fun commands() = rule.onNodeWithTag("target").fetchSemanticsNode()
        .config.getOrElse(SemanticsActions.CustomActions) { emptyList() }

    private fun accessibilityProvider(): AccessibilityNodeProvider {
        val composeView = (rule.activity.window.decorView as ViewGroup).descendants
            .first { it.javaClass.simpleName == "AndroidComposeView" }
        return assertNotNull(composeView.accessibilityNodeProvider)
    }

    private fun performCommand(label: String) {
        val command = commands().single { it.label == label }
        rule.runOnIdle { assertTrue(command.action()) }
    }

    private fun setContent(content: Div) {
        this.content.value = content
        rule.setContentWithDivContext(configuration) {
            DivView(data = data(
                container(items = listOf(this.content.value, text(id = "result", text = expression("@{last_action}")))),
                variables = listOf(variable("last_action", "none")),
            ))
        }
    }
}
