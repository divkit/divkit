package com.yandex.div.compose.views

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.insets
import com.yandex.div.test.data.switch
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibility.Mode
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivSize
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivSwitchViewTest {

    @get:Rule
    val rule = createComposeRule()

    private val isOn = Variable.BooleanVariable("is_on", false)
    private val variableController = DivVariableController().apply { declare(isOn) }
    private val configuration = DivConfiguration(
        reporter = TestReporter(),
        variableController = variableController
    )
    private val tag = "switch"

    @Test
    fun `tap on element corner toggles variable`() {
        setContent(width = fixed(constant(200L)), height = fixed(constant(200L)))

        rule.onNodeWithTag(tag).apply {
            assertCoversElement(200.dp)
            performTouchInput { click(topLeft) }
        }

        assertIsOnVariable(true)
        rule.onNodeWithTag(tag).assertIsOn()
    }

    @Test
    fun `tap on paddings toggles variable`() {
        setContent(
            width = fixed(constant(200L)),
            height = fixed(constant(200L)),
            paddings = insets(start = 40, end = 40, top = 40, bottom = 40),
        )

        rule.onNodeWithTag(tag).apply {
            assertCoversElement(200.dp)
            performTouchInput { click(topLeft) }
        }

        assertIsOnVariable(true)
        rule.onNodeWithTag(tag).assertIsOn()
    }

    @Test
    fun `tap on switch toggles variable`() {
        setContent()

        rule.onNodeWithTag(tag).performClick()

        assertIsOnVariable(true)
        rule.onNodeWithTag(tag).assertIsOn()
    }

    @Test
    fun `tap is ignored when is_enabled is false`() {
        setContent(isEnabled = constant(false))

        rule.onNodeWithTag(tag).apply {
            assertIsNotEnabled()
            performClick()
        }

        assertIsOnVariable(false)
        rule.onNodeWithTag(tag).assertIsOff()
    }

    @Test
    fun `is_enabled expression is observed`() {
        val isEnabled = Variable.BooleanVariable("is_enabled", false)
        variableController.declare(isEnabled)
        setContent(isEnabled = booleanExpression("@{is_enabled}"))

        rule.onNodeWithTag(tag).performClick()

        assertIsOnVariable(false)

        isEnabled.set(true)
        rule.waitForIdle()
        rule.onNodeWithTag(tag).performClick()

        assertIsOnVariable(true)
    }

    @Test
    fun `variable change updates checked state`() {
        setContent()

        rule.onNodeWithTag(tag).assertIsOff()

        isOn.set(true)
        rule.waitForIdle()

        rule.onNodeWithTag(tag).assertIsOn()
    }

    @Test
    fun `wrap_content element is sized by the switch alone`() {
        setContent()

        rule.onNodeWithTag(tag).assertSizeIsEqualTo(SWITCH_WIDTH, SWITCH_HEIGHT)
    }

    @Test
    fun `wrap_content element grows by paddings`() {
        setContent(paddings = insets(start = 8, end = 8, top = 4, bottom = 4))

        rule.onNodeWithTag(tag).assertSizeIsEqualTo(SWITCH_WIDTH + 16.dp, SWITCH_HEIGHT + 8.dp)
    }

    @Test
    fun `element is the single toggleable node with switch role`() {
        setContent()

        rule.onAllNodes(isToggleable()).assertCountEquals(1)
        rule.onNodeWithTag(tag).assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
    }

    @Test
    fun `element is the single accessible node`() {
        setContent(accessibility = accessibility(description = "Airplane mode"))

        rule.onAllNodes(isToggleable()).assertCountEquals(1)
        rule.onNodeWithTag(tag).assertIsSwitchNode("Airplane mode")
    }

    @Test
    fun `element stays the single accessible node with mode merge`() {
        setContent(accessibility = accessibility(description = "Airplane mode", mode = Mode.MERGE))

        rule.onAllNodes(isToggleable()).assertCountEquals(1)
        rule.onNodeWithTag(tag).assertIsSwitchNode("Airplane mode")
    }

    private fun SemanticsNodeInteraction.assertIsSwitchNode(description: String) {
        assert(hasContentDescription(description))
        assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
        assertIsOff()
        assertHasClickAction()
    }

    private fun SemanticsNodeInteraction.assertCoversElement(size: Dp) {
        assertPositionInRootIsEqualTo(0.dp, 0.dp)
        assertSizeIsEqualTo(size, size)
    }

    private fun SemanticsNodeInteraction.assertSizeIsEqualTo(width: Dp, height: Dp) {
        assertWidthIsEqualTo(width)
        assertHeightIsEqualTo(height)
    }

    private fun assertIsOnVariable(expected: Boolean) {
        assertEquals(expected, isOn.getValue(), "is_on variable")
    }

    private fun setContent(
        accessibility: DivAccessibility? = null,
        height: DivSize = wrapContent(),
        isEnabled: Expression<Boolean> = constant(true),
        paddings: DivEdgeInsets? = null,
        width: DivSize = wrapContent(),
    ) {
        rule.setContent(
            configuration = configuration,
            data = data(
                switch(
                    accessibility = accessibility,
                    height = height,
                    id = tag,
                    isEnabled = isEnabled,
                    isOnVariable = isOn.name,
                    paddings = paddings,
                    width = width,
                )
            )
        )
    }
}

/** Material3 `SwitchTokens` track size: with no handler of its own the switch adds no touch target. */
private val SWITCH_WIDTH = 52.dp
private val SWITCH_HEIGHT = 32.dp
