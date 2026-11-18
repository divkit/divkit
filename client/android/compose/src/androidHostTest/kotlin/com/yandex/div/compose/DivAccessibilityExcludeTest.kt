package com.yandex.div.compose

import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeProvider
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.core.view.descendants
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.accessibilityModeExpression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.text
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibility.Mode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivAccessibilityExcludeTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val actionHandler = TestExternalActionHandler()
    private val variableController = DivVariableController()
    private val configuration = DivConfiguration(
        actionHandler = actionHandler,
        reporter = TestReporter(),
        variableController = variableController,
    )

    @Test
    fun `excluded clickable element is hidden from accessibility while accepting touch`() {
        rule.setContent(configuration, data = data(text(
            id = "button",
            text = "Button",
            accessibility = accessibility(mode = Mode.EXCLUDE),
            action = action(url = "test://tap"),
        )))
        val button = rule.onNodeWithTag("button")
        val id = button.fetchSemanticsNode().id
        val provider = accessibilityProvider()
        val node = assertNotNull(provider.createAccessibilityNodeInfo(id))

        val clicked = rule.runOnIdle {
            provider.performAction(id, AccessibilityNodeInfo.ACTION_CLICK, null)
        }
        button.performTouchInput { click() }

        assertFalse(clicked)
        assertFalse(node.isImportantForAccessibility)
        assertFalse(node.isVisibleToUser)
        assertFalse(node.isScreenReaderFocusable)
        assertEquals(listOf(actionData(url = "test://tap")), actionHandler.handledActions)
    }

    @Test
    fun `excluded clickable container hides its actionable subtree while accepting touch`() {
        rule.setContent(configuration, data = data(container(
            id = "container",
            accessibility = accessibility(mode = Mode.EXCLUDE),
            action = action(url = "test://parent"),
            items = listOf(text(
                id = "child",
                text = "Child",
                action = action(url = "test://child"),
            )),
        )))
        val child = rule.onNodeWithTag("child", useUnmergedTree = true)
        val provider = accessibilityProvider()
        val parent = assertNotNull(provider.createAccessibilityNodeInfo(
            rule.onNodeWithTag("container").fetchSemanticsNode().id,
        ))

        child.performTouchInput { click() }

        assertFalse(parent.isImportantForAccessibility)
        assertFalse(parent.isVisibleToUser)
        assertEquals(0, parent.childCount)
        assertEquals(listOf(actionData(url = "test://child")), actionHandler.handledActions)
    }

    @Test
    fun `excluded element becomes available when mode changes to default`() {
        val mode = Variable.StringVariable("mode", "exclude")
        variableController.declare(mode)
        rule.setContent(configuration, data = data(text(
            id = "button",
            text = "Button",
            accessibility = DivAccessibility(mode = accessibilityModeExpression("@{mode}")),
            action = action(url = "test://tap"),
        )))

        rule.runOnIdle { mode.setValueDirectly("default") }

        val provider = accessibilityProvider()
        val id = rule.onNodeWithTag("button").fetchSemanticsNode().id
        val node = assertNotNull(provider.createAccessibilityNodeInfo(id))
        val clicked = rule.runOnIdle {
            provider.performAction(id, AccessibilityNodeInfo.ACTION_CLICK, null)
        }

        assertTrue(clicked)
        assertEquals(listOf(actionData(url = "test://tap")), actionHandler.handledActions)
        assertTrue(node.isImportantForAccessibility)
        assertTrue(node.isVisibleToUser)
        assertTrue(node.isScreenReaderFocusable)
    }

    private fun accessibilityProvider(): AccessibilityNodeProvider {
        val composeView = (rule.activity.window.decorView as ViewGroup).descendants
            .first { it.javaClass.simpleName == "AndroidComposeView" }
        return assertNotNull(composeView.accessibilityNodeProvider)
    }
}
