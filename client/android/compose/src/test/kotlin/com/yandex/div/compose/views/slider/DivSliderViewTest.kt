package com.yandex.div.compose.views.slider

import android.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.slider
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivGallery
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivSliderViewTest {
    @get:Rule
    val rule = createComposeRule()

    private val value = Variable.IntegerVariable(name = "value", defaultValue = 0)
    private val variableController = DivVariableController().apply { declare(value) }
    private val actionHandler = TestExternalActionHandler()
    private val configuration =
        DivConfiguration(
            actionHandler = actionHandler,
            reporter = TestReporter(),
            variableController = variableController,
        )

    private val pressStartActions = listOf(action(id = "press_start"))
    private val pressEndActions = listOf(action(id = "press_end"))
    private val pressStart = actionData(id = "press_start", source = DivActionSource.GESTURE)
    private val pressEnd = actionData(id = "press_end", source = DivActionSource.GESTURE)

    @Test
    fun `first drag updates variable to move position after recomposition`() {
        setContent()
        val slider = rule.onNodeWithTag("slider")

        slider.performTouchInput { down(position = Offset(center.x / 2f, center.y)) }
        rule.waitForIdle()
        slider.performTouchInput {
            moveTo(position = centerRight)
            up()
        }
        rule.waitForIdle()

        assertEquals(100L, value.getValue())
    }

    @Test
    fun `second drag updates same variable with tick marks`() {
        setContent(hasTickMarks = true)
        val slider = rule.onNodeWithTag("slider")
        slider.performTouchInput {
            down(position = centerLeft)
            moveTo(position = centerRight)
            up()
        }
        rule.waitForIdle()
        assertEquals(100L, value.getValue())

        slider.performTouchInput {
            down(position = centerRight)
            moveTo(position = centerLeft)
            up()
        }
        rule.waitForIdle()

        assertEquals(0L, value.getValue())
    }

    @Test
    fun `press start actions are performed on down and press end actions on up`() {
        setContent(pressStartActions = pressStartActions, pressEndActions = pressEndActions)
        val slider = rule.onNodeWithTag("slider")

        slider.performTouchInput { down(center) }

        assertEquals(listOf(pressStart), actionHandler.handledActions)

        slider.performTouchInput { up() }

        assertEquals(listOf(pressStart, pressEnd), actionHandler.handledActions)
    }

    @Test
    fun `press end actions are performed when the gesture is cancelled`() {
        setContent(pressStartActions = pressStartActions, pressEndActions = pressEndActions)

        rule.onNodeWithTag("slider").performTouchInput {
            down(center)
            cancel()
        }

        assertEquals(listOf(pressStart, pressEnd), actionHandler.handledActions)
    }

    @Test
    fun `press actions are not performed when is_enabled is false`() {
        setContent(
            isEnabled = constant(false),
            pressStartActions = pressStartActions,
            pressEndActions = pressEndActions,
        )

        rule.onNodeWithTag("slider").performTouchInput { click() }

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `tap on slider does not perform actions of the enclosing container`() {
        setContent(
            container(
                action = action(id = "container"),
                items = listOf(slider(id = "slider", thumbValueVariable = value.name)),
            )
        )

        rule.onNodeWithTag("slider", useUnmergedTree = true).performTouchInput { click() }

        assertEquals(50L, value.getValue())
        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `vertical drag on slider scrolls the vertical gallery and releases the slider`() {
        setContent(
            gallery(
                id = "gallery",
                orientation = constant(DivGallery.Orientation.VERTICAL),
                height = fixed(400),
                items = listOf(
                    text(id = "above", text = constant("Above the slider"), height = fixed(300)),
                    slider(
                        id = "slider",
                        thumbValueVariable = value.name,
                        pressStartActions = pressStartActions,
                        pressEndActions = pressEndActions,
                    ),
                    text(id = "text", text = constant("Below the slider"), height = fixed(300)),
                ),
            )
        )
        val text = rule.onNodeWithTag("text")
        val textTop = text.getUnclippedBoundsInRoot().top

        rule.onNodeWithTag("slider").performTouchInput {
            down(center)
            moveBy(Offset(0f, -100f))
            moveBy(Offset(0f, -100f))
            moveBy(Offset(100f, 0f))
        }

        assertTrue(text.getUnclippedBoundsInRoot().top < textTop, "gallery is scrolled")
        assertEquals(50L, value.getValue())
        assertEquals(listOf(pressStart, pressEnd), actionHandler.handledActions)

        rule.onNodeWithTag("gallery").performTouchInput { up() }

        assertEquals(listOf(pressStart, pressEnd), actionHandler.handledActions)
    }

    @Test
    fun `drag turning vertical after horizontal slop is handed over to the vertical gallery`() {
        setContent(
            gallery(
                id = "gallery",
                orientation = constant(DivGallery.Orientation.VERTICAL),
                height = fixed(100),
                items = listOf(
                    slider(
                        id = "slider",
                        thumbValueVariable = value.name,
                        pressStartActions = pressStartActions,
                        pressEndActions = pressEndActions,
                    ),
                    text(id = "text", text = constant("Below the slider"), height = fixed(300)),
                ),
            )
        )
        val text = rule.onNodeWithTag("text")
        val textTop = text.getUnclippedBoundsInRoot().top

        rule.onNodeWithTag("slider").performTouchInput {
            down(centerLeft)
            moveBy(Offset(200f, 0f))
            moveBy(Offset(0f, -100f))
            moveBy(Offset(0f, -100f))
            moveBy(Offset(-200f, 0f))
        }

        assertTrue(text.getUnclippedBoundsInRoot().top < textTop, "gallery is scrolled")
        assertEquals(100L, value.getValue())
        assertEquals(listOf(pressStart, pressEnd), actionHandler.handledActions)

        rule.onNodeWithTag("gallery").performTouchInput { up() }
    }

    @Test
    fun `steep drag without a scrolling parent keeps moving the thumb`() {
        setContent(pressStartActions = pressStartActions, pressEndActions = pressEndActions)
        val slider = rule.onNodeWithTag("slider")

        slider.performTouchInput {
            down(center)
            moveBy(Offset(0f, 60f))
            moveBy(Offset(100f, 0f))
        }

        assertEquals(100L, value.getValue())
        assertEquals(listOf(pressStart), actionHandler.handledActions)
    }

    @Test
    fun `horizontal drag on slider keeps the horizontal gallery still`() {
        setContent(
            gallery(
                id = "gallery",
                width = fixed(200),
                items = listOf(
                    slider(id = "slider", thumbValueVariable = value.name),
                    text(id = "text", text = constant("Next to the slider"), width = fixed(300)),
                ),
            )
        )
        val slider = rule.onNodeWithTag("slider")

        slider.performTouchInput {
            down(centerRight)
            moveBy(Offset(-100f, 0f))
            moveBy(Offset(-100f, 0f))
            up()
        }

        assertEquals(0.dp, slider.getUnclippedBoundsInRoot().left)
        assertEquals(0L, value.getValue())
    }

    @Test
    fun `long press on slider does not perform long tap actions of the enclosing container`() {
        setContent(
            container(
                longTapActions = listOf(action(id = "container")),
                items = listOf(slider(id = "slider", thumbValueVariable = value.name)),
            )
        )

        rule.onNodeWithTag("slider", useUnmergedTree = true).performTouchInput { longClick() }

        assertEquals(50L, value.getValue())
        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `thumb value outside the range is clamped in the variable`() {
        value.set(150L)

        setContent(slider(id = "slider", thumbValueVariable = value.name))

        assertEquals(100L, value.getValue())
    }

    @Test
    fun `thumb value is clamped in the variable when the range shrinks`() {
        val maxValue = Variable.IntegerVariable(name = "max_value", defaultValue = 100)
        variableController.declare(maxValue)
        value.set(80L)
        setContent(slider(id = "slider", thumbValueVariable = value.name, maxValue = intExpression("@{max_value}")))

        maxValue.set(60L)
        rule.waitForIdle()

        assertEquals(60L, value.getValue())
    }

    @Test
    fun `secondary thumb value below the range is clamped in the variable`() {
        val secondaryValue = Variable.IntegerVariable(name = "secondary_value", defaultValue = 5)
        variableController.declare(secondaryValue)
        value.set(50L)

        setContent(
            slider(
                id = "slider",
                minValue = constant(10L),
                thumbValueVariable = value.name,
                thumbSecondaryValueVariable = secondaryValue.name,
            )
        )

        assertEquals(10L, secondaryValue.getValue())
    }

    @Test
    fun `sliders with disjoint ranges sharing a variable settle on one clamped value`() {
        value.set(50L)

        setContent(
            container(
                items = listOf(
                    slider(maxValue = constant(10L), thumbValueVariable = value.name),
                    slider(minValue = constant(20L), maxValue = constant(30L), thumbValueVariable = value.name),
                )
            )
        )
        rule.waitForIdle()

        assertEquals(20L, value.getValue())
    }

    @Test
    fun `wrap_content height includes tick marks taller than the thumb`() {
        setContent(
            slider(
                id = "slider",
                tickMarkStyle = DivDrawable.Shape(
                    DivShapeDrawable(
                        color = constant(Color.BLACK),
                        shape = DivShape.RoundedRectangle(
                            DivRoundedRectangleShape(
                                itemHeight = DivFixedSize(value = constant(40L)),
                                itemWidth = DivFixedSize(value = constant(6L)),
                            )
                        ),
                    )
                ),
            )
        )

        rule.onNodeWithTag("slider").assertHeightIsEqualTo(40.dp)
    }

    private fun setContent(
        hasTickMarks: Boolean = false,
        isEnabled: Expression<Boolean> = constant(true),
        pressStartActions: List<DivAction>? = null,
        pressEndActions: List<DivAction>? = null,
    ) {
        setContent(
            slider(
                id = "slider",
                thumbValueVariable = value.name,
                hasTickMarks = hasTickMarks,
                isEnabled = isEnabled,
                pressStartActions = pressStartActions,
                pressEndActions = pressEndActions,
            )
        )
    }

    private fun setContent(div: Div) {
        rule.setContent(configuration = configuration, data = data(div))
    }
}
