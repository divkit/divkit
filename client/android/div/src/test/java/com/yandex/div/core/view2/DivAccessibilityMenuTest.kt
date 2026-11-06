package com.yandex.div.core.view2

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
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
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.slider
import com.yandex.div.test.data.switch
import com.yandex.div.test.data.text
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibilityMenuItem
import com.yandex.div2.DivAction
import com.yandex.div2.DivFocus
import com.yandex.div2.DivTypedValue
import com.yandex.div2.DivVariable
import com.yandex.div2.StrValue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class DivAccessibilityMenuTest {
    private val configuration = DivConfiguration.Builder(mock())
        .enableBindOnAttach(false)
        .forceAccessibilityEnable(true)
        .build()
    private val divView = Div2View(Div2Context(testContextThemeWrapper(), configuration))

    private val activity = Robolectric.buildActivity(Activity::class.java).setup().visible()

    @BeforeTest
    fun attachView() {
        activity.get().setContentView(divView)
    }

    @AfterTest
    fun tearDown() {
        divView.cleanup()
        activity.pause().stop().destroy()
    }

    @Test
    fun `menu preserves repeated labels while hiding unavailable commands`() {
        bind(text(
            id = "target",
            text = "Card",
            accessibility = accessibility(menu = listOf(
                accessibilityMenuItem("First", listOf(action(
                    url = "div-action://set_variable?name=last_action&value=first",
                ))),
                accessibilityMenuItem("Empty", emptyList()),
                accessibilityMenuItem("Disabled", listOf(action(isEnabled = false))),
                accessibilityMenuItem("First", listOf(action(
                    url = "div-action://set_variable?name=last_action&value=second",
                ))),
                accessibilityMenuItem("Last", listOf(action(url = "test://last"))),
            )),
        ))

        val command = target().createAccessibilityNodeInfo().actionList.first { it.label == "First" }
        assertTrue(target().performAccessibilityAction(command.id, null))

        assertEquals(listOf("First", "First", "Last"), labels())
        assertEquals("first", result())
    }

    @Test
    fun `menu publishes more than 32 commands`() {
        bind(text(id = "target", text = "Card", accessibility = accessibility(menu = List(33) { index ->
            accessibilityMenuItem("Command $index", listOf(action()))
        })))

        assertEquals(List(33) { index -> "Command $index" }, labels())
    }

    @Test
    fun `disabled input does not publish a command`() {
        bind(input(
            id = "target", textVariable = "input", isEnabled = constant(false),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ), variables = listOf(variable("input", "")))

        assertEquals(emptyList(), labels())
    }

    @Test
    fun `showing the host restores commands bound while it was gone`() {
        divView.visibility = View.GONE
        bind(commandTarget("Run", "command"))

        divView.visibility = View.VISIBLE

        assertEquals(listOf("Run"), labels())
    }

    @Test
    fun `ancestor accessibility changes are reflected when reading commands`() {
        bind(commandTarget("Run", "command"))

        divView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS

        assertEquals(emptyList(), labels())
    }

    @Test
    fun `showing the host restores slider commands bound while it was gone`() {
        divView.visibility = View.GONE
        bind(slider(
            id = "target", thumbValueVariable = "start",
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ), variables = listOf(variable("start", 30L)))

        divView.visibility = View.VISIBLE

        val provider = assertNotNull(target().accessibilityNodeProvider)
        assertEquals(listOf("Run"), assertNotNull(provider.createAccessibilityNodeInfo(0)).actionList.mapNotNull {
            it.label?.toString()
        })
    }

    @Test
    fun `cleanup removes all commands after label changes`() {
        bind(text(id = "target", text = "Card", accessibility = accessibility(
            menu = List(32) { index ->
                accessibilityMenuItem("Command $index", listOf(action()))
            } + DivAccessibilityMenuItem(
                label = expression("@{menu_label}"), actions = listOf(action()),
            ),
        )), variables = listOf(variable("menu_label", "Last")))
        val original: View = target()
        divView.setVariable("menu_label", "Changed")

        divView.cleanup()

        assertEquals(emptyList(), original.createAccessibilityNodeInfo().actionList.mapNotNull {
            it.label?.toString()
        })
    }

    @Test
    fun `named command changes its variable without invoking the primary action`() {
        bind(text(
            id = "target",
            text = "Card",
            action = action(url = "div-action://set_variable?name=last_action&value=tap"),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem(
                "Run",
                listOf(action(url = "div-action://set_variable?name=last_action&value=command")),
            ))),
        ))
        val command = target().createAccessibilityNodeInfo().actionList.single { it.label == "Run" }

        assertTrue(target().performAccessibilityAction(command.id, null))

        assertEquals("command", divView.findViewWithTag<TextView>("result").text.toString())
    }

    @Test
    fun `primary accessibility activation keeps the regular tap action`() {
        bind(text(
            id = "target", text = "Card",
            action = action(url = "div-action://set_variable?name=last_action&value=tap"),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem(
                "Run", listOf(action(url = "div-action://set_variable?name=last_action&value=command")),
            ))),
        ))

        assertTrue(target().performAccessibilityAction(AccessibilityNodeInfo.ACTION_CLICK, null))

        assertEquals("tap", result())
    }

    @Test
    fun `label expression changes the published command`() {
        bind(text(id = "target", text = "Card", accessibility = DivAccessibility(
            menu = listOf(DivAccessibilityMenuItem(
                label = expression("@{menu_label}"), actions = listOf(action()),
            )),
        )), variables = listOf(variable("menu_label", "Before")))

        divView.setVariable("menu_label", "After")

        assertEquals(listOf("After"), labels())
    }

    @Test
    fun `disabling actions removes the command and rejects its old id`() {
        bind(text(id = "target", text = "Card", accessibility = accessibility(
            menu = listOf(accessibilityMenuItem("Run", listOf(DivAction(
                isEnabled = booleanExpression("@{enabled}"),
                url = constant("div-action://set_variable?name=last_action&value=command".toUri()),
            )))),
        )), variables = listOf(variable("enabled", true)))
        val command = target().createAccessibilityNodeInfo().actionList.single { it.label == "Run" }

        divView.setVariable("enabled", "false")

        assertEquals(emptyList(), labels())
        assertFalse(target().performAccessibilityAction(command.id, null))
        assertEquals("none", result())
    }

    @Test
    fun `rebind replaces commands on the reused view`() {
        bind(commandTarget("Old", "old"))
        val original: View = target()

        bind(commandTarget("New", "new"))
        performCommand("New")

        assertSame(original, target())
        assertEquals(listOf("New"), labels())
        assertEquals("new", result())
    }

    @Test
    fun `rebind rejects the previous command id`() {
        bind(commandTarget("Old", "old"))
        val oldCommand = target().createAccessibilityNodeInfo().actionList.single { it.label == "Old" }

        bind(commandTarget("New", "new"))

        assertFalse(target().performAccessibilityAction(oldCommand.id, null))
        assertEquals("none", result())
    }

    @Test
    fun `menu preserves radio checked state updates`() {
        bind(text(id = "target", text = "Radio", accessibility = DivAccessibility(
            type = DivAccessibility.Type.RADIO,
            isChecked = booleanExpression("@{checked}"),
            menu = listOf(accessibilityMenuItem("Run", listOf(action()))),
        )), variables = listOf(variable("checked", false)))

        divView.setVariable("checked", "true")

        assertTrue(target().createAccessibilityNodeInfo().isChecked)
        assertEquals(listOf("Run"), labels())
    }

    @Test
    fun `removing accessibility clears commands on the reused view`() {
        bind(commandTarget("Old", "old"))
        val original = target()

        bind(text(id = "target", text = "Card"))

        assertSame(original, target())
        assertEquals(emptyList(), labels())
    }

    @Test
    fun `cleanup rejects commands retained by a detached view`() {
        bind(commandTarget("Run", "command"))
        val original = target()
        val command = original.createAccessibilityNodeInfo().actionList.single { it.label == "Run" }

        divView.cleanup()

        assertFalse(original.performAccessibilityAction(command.id, null))
    }

    @Test
    fun `merged container publishes only its own menu`() {
        bind(container(
            id = "target",
            items = listOf(commandTarget("Child", "child")),
            accessibility = accessibility(mode = DivAccessibility.Mode.MERGE, menu = listOf(
                accessibilityMenuItem("Parent", listOf(action())),
            )),
        ))

        assertEquals(listOf("Parent"), labels())
    }

    @Test
    fun `excluded ancestor removes descendant commands and rejects their old id`() {
        bind(container(
            accessibility = DivAccessibility(mode = accessibilityModeExpression("@{parent_mode}")),
            items = listOf(commandTarget("Run", "command")),
        ), variables = listOf(variable("parent_mode", "default")))
        val command = target().createAccessibilityNodeInfo().actionList.single { it.label == "Run" }

        divView.setVariable("parent_mode", "exclude")

        assertFalse(target().performAccessibilityAction(command.id, null))
        assertEquals(emptyList(), labels())
        assertEquals("none", result())
    }

    @Test
    fun `merging an ancestor removes descendant commands`() {
        bind(container(
            accessibility = DivAccessibility(mode = accessibilityModeExpression("@{parent_mode}")),
            items = listOf(commandTarget("Run", "command")),
        ), variables = listOf(variable("parent_mode", "default")))

        divView.setVariable("parent_mode", "merge")

        assertEquals(emptyList(), labels())
    }

    @Test
    fun `slider thumbs expose separate menus alongside standard progress actions`() {
        bind(slider(
            id = "target", thumbValueVariable = "start", thumbSecondaryValueVariable = "end",
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Start", listOf(action())))),
            secondaryValueAccessibility = accessibility(menu = listOf(accessibilityMenuItem("End", listOf(action())))),
        ), variables = listOf(variable("start", 30L), variable("end", 70L)))
        val provider = assertNotNull(target().accessibilityNodeProvider)
        val main = assertNotNull(provider.createAccessibilityNodeInfo(0))
        val secondary = assertNotNull(provider.createAccessibilityNodeInfo(1))

        assertEquals(listOf("Start"), main.actionList.mapNotNull { it.label?.toString() })
        assertEquals(listOf("End"), secondary.actionList.mapNotNull { it.label?.toString() })
        assertTrue(provider.performAction(0, AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, null))
        assertEquals(35f, assertNotNull(provider.createAccessibilityNodeInfo(0)).rangeInfo.current)
    }

    @Test
    fun `slider supports secondary menu without a main accessibility block`() {
        bind(slider(
            id = "target", thumbValueVariable = "start", thumbSecondaryValueVariable = "end",
            secondaryValueAccessibility = accessibility(menu = listOf(accessibilityMenuItem("End", listOf(action())))),
        ), variables = listOf(variable("start", 30L), variable("end", 70L)))

        val provider = assertNotNull(target().accessibilityNodeProvider)

        assertEquals(listOf("End"), assertNotNull(provider.createAccessibilityNodeInfo(1)).actionList.mapNotNull {
            it.label?.toString()
        })
    }

    @Test
    fun `switch keeps its standard click when it has a named command`() {
        bind(switch(
            id = "target", isOnVariable = "toggle",
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ), variables = listOf(variable("toggle", false)))

        assertTrue(target().performAccessibilityAction(AccessibilityNodeInfo.ACTION_CLICK, null))

        assertTrue(target().createAccessibilityNodeInfo().isChecked)
        assertEquals(listOf("Run"), labels())
    }

    @Test
    fun `switch honors explicit checkbox state with a named command`() {
        bind(switch(
            id = "target", isOnVariable = "toggle",
            accessibility = accessibility(type = DivAccessibility.Type.CHECKBOX, isChecked = true,
                menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ), variables = listOf(variable("toggle", false)))

        val node = target().createAccessibilityNodeInfo()

        assertEquals("android.widget.CheckBox", node.className.toString())
        assertTrue(node.isChecked)
    }

    @Test
    fun `switch honors explicit button type without a menu`() {
        bind(switch(
            id = "target", isOnVariable = "toggle",
            accessibility = accessibility(type = DivAccessibility.Type.BUTTON),
        ), variables = listOf(variable("toggle", false)))

        val node = target().createAccessibilityNodeInfo()

        assertEquals("android.widget.Button", node.className.toString())
        assertFalse(node.isCheckable)
    }

    @Test
    fun `switch exposes unchecked state when menu is bound before attach`() {
        (divView.parent as ViewGroup).removeView(divView)
        bind(switch(
            id = "target", isOnVariable = "toggle",
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ), variables = listOf(variable("toggle", false)))

        activity.get().setContentView(divView)

        val node = target().createAccessibilityNodeInfo()
        assertSwitchAccessibility(node, menuLabels = listOf("Run"))
        assertFalse(node.isChecked)
    }

    @Test
    fun `switch exposes checked state when menu is added after attach`() {
        bind(
            switch(id = "target", isOnVariable = "toggle"),
            variables = listOf(variable("toggle", true)),
        )
        val original = target()

        bind(switch(
            id = "target", isOnVariable = "toggle",
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ), variables = listOf(variable("toggle", true)))

        assertSame(original, target())
        val node = target().createAccessibilityNodeInfo()
        assertSwitchAccessibility(node, menuLabels = listOf("Run"))
        assertTrue(node.isChecked)
    }

    @Test
    fun `updating switch menu label preserves checked state`() {
        bind(switch(
            id = "target", isOnVariable = "toggle",
            accessibility = DivAccessibility(menu = listOf(DivAccessibilityMenuItem(
                label = expression("@{menu_label}"), actions = listOf(action()),
            ))),
        ), variables = listOf(variable("toggle", true), variable("menu_label", "Before")))

        divView.setVariable("menu_label", "After")

        val node = target().createAccessibilityNodeInfo()
        assertSwitchAccessibility(node, menuLabels = listOf("After"))
        assertTrue(node.isChecked)
    }

    @Test
    fun `removing switch menu preserves checked state`() {
        bind(switch(
            id = "target", isOnVariable = "toggle",
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ), variables = listOf(variable("toggle", true)))
        val original = target()

        bind(
            switch(id = "target", isOnVariable = "toggle"),
            variables = listOf(variable("toggle", true)),
        )

        assertSame(original, target())
        val node = target().createAccessibilityNodeInfo()
        assertSwitchAccessibility(node, menuLabels = emptyList())
        assertTrue(node.isChecked)
    }

    @Test
    fun `named command does not capture configured focus`() {
        bind(text(
            id = "target", text = "Card", focus = DivFocus(),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(action())))),
        ))

        performCommand("Run")

        assertFalse(target().isFocused)
    }

    @Test
    fun `command uses element local variables`() {
        bind(text(
            id = "target", text = expression("@{message}"),
            variables = listOf(variable("message", "local")),
            accessibility = accessibility(menu = listOf(accessibilityMenuItem("Run", listOf(
                action(url = "div-action://set_variable?name=message&value=updated"),
            )))),
        ), variables = listOf(variable("message", "global")))

        performCommand("Run")

        assertEquals("updated", (target() as TextView).text.toString())
    }

    @Test
    fun `mode expression hides published commands`() {
        bind(text(id = "target", text = "Card", accessibility = DivAccessibility(
            mode = accessibilityModeExpression("@{menu_mode}"),
            menu = listOf(accessibilityMenuItem("Run", listOf(action()))),
        )), variables = listOf(variable("menu_mode", "default")))

        divView.setVariable("menu_mode", "exclude")

        assertEquals(emptyList(), labels())
    }

    @Test
    fun `named command executes actions in order even when an action has menu items`() {
        bind(text(id = "target", text = "Card", accessibility = accessibility(menu = listOf(
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

        assertEquals("first-second", result())
    }

    @Test
    fun `command executes enabled typed actions in order`() {
        bind(text(id = "target", text = "Card", accessibility = accessibility(menu = listOf(
            accessibilityMenuItem("Run", listOf(
                action(typed = setVariableAction("last_action", typedValue("first"))),
                action(typed = setVariableAction("last_action", DivTypedValue.Str(
                    StrValue(value = expression("@{last_action + '-second'}")),
                ))),
                action(isEnabled = false, typed = setVariableAction("last_action", typedValue("disabled"))),
            )),
        ))))

        performCommand("Run")

        assertEquals("first-second", result())
    }

    private fun commandTarget(label: String, value: String): Div = text(
        id = "target", text = "Card",
        accessibility = accessibility(menu = listOf(accessibilityMenuItem(
            label, listOf(action(url = "div-action://set_variable?name=last_action&value=$value")),
        ))),
    )

    private fun performCommand(label: String) {
        val command = target().createAccessibilityNodeInfo().actionList.single { it.label == label }
        assertTrue(target().performAccessibilityAction(command.id, null))
    }

    private fun result(): String = divView.findViewWithTag<TextView>("result").text.toString()

    private fun assertSwitchAccessibility(node: AccessibilityNodeInfo, menuLabels: List<String>) {
        assertEquals("android.widget.Switch", node.className.toString())
        assertTrue(node.isCheckable)
        assertEquals(menuLabels, node.actionList.mapNotNull { it.label?.toString() })
    }

    private fun labels(): List<String> = target().createAccessibilityNodeInfo().actionList
        .mapNotNull { it.label?.toString() }

    private fun bind(content: Div, variables: List<DivVariable> = emptyList()) {
        divView.setData(data(
            container(items = listOf(content, text(id = "result", text = expression("@{last_action}")))),
            variables = listOf(variable("last_action", "none")) + variables,
        ), DivDataTag("accessibility-menu"))
    }

    private fun target(): View = divView.findViewWithTag("target")
}
