package com.yandex.div.compose.views.slider

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.data
import com.yandex.div.test.data.slider
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivSliderViewTest {
    @get:Rule
    val rule = createComposeRule()

    private val value = Variable.IntegerVariable(name = "value", defaultValue = 0)
    private val variableController = DivVariableController().apply { declare(value) }
    private val configuration =
        DivConfiguration(
            reporter = TestReporter(),
            variableController = variableController,
        )

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

    private fun setContent(hasTickMarks: Boolean = false) {
        rule.setContent(
            configuration = configuration,
            data =
                data(
                    slider(
                        id = "slider",
                        thumbValueVariable = value.name,
                        hasTickMarks = hasTickMarks,
                    ),
                ),
        )
    }
}
