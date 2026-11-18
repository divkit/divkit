package com.yandex.div.compose.views.container

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContent
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.border
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div2.DivBorder
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivContainerClippingTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val radius = Variable.IntegerVariable("radius", 16)
    private val clip = Variable.BooleanVariable("clip", true)

    private val configuration = divConfiguration {
        actionHandler = this@DivContainerClippingTest.actionHandler
        reporter = TestReporter()
        variableController = DivVariableController().apply { declare(radius, clip) }
    }

    @Test
    fun `rounded container rejects clicks on overflowing child`() {
        setContent(border(cornerRadius = 16))

        clickAt(150f, 50f)

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `rounded container accepts clicks on visible child`() {
        setContent(border(cornerRadius = 16))

        clickAt(50f, 50f)

        assertEquals(listOf(actionData(id = "child")), actionHandler.handledActions)
    }

    @Test
    fun `child semantics bounds remain clipped by rounded container`() {
        setContent(border(cornerRadius = 16))

        val parentBounds = rule.onNodeWithTag("parent").fetchSemanticsNode().boundsInRoot
        val childBounds = rule.onNodeWithTag("child").fetchSemanticsNode().boundsInRoot

        assertEquals(parentBounds, childBounds)
    }

    @Test
    fun `zero radius expression keeps overflowing child unclickable`() {
        setContent(border(cornerRadius = intExpression("@{radius}")))

        radius.set(0)
        rule.waitForIdle()
        clickAt(150f, 50f)

        assertEquals(emptyList(), actionHandler.handledActions)
    }

    @Test
    fun `disabling rectangular bounds clipping makes overflowing child clickable`() {
        setContent(border())

        clip.set(false)
        rule.waitForIdle()
        clickAt(150f, 50f)

        assertEquals(listOf(actionData(id = "child")), actionHandler.handledActions)
    }

    private fun setContent(border: DivBorder) {
        rule.setContent(
            configuration = configuration,
            data = data(
                container(
                    id = "root",
                    width = fixed(300),
                    height = fixed(200),
                    items = listOf(
                        container(
                            id = "parent",
                            width = fixed(100),
                            height = fixed(100),
                            border = border,
                            clipToBounds = booleanExpression("@{clip}"),
                            items = listOf(
                                container(
                                    id = "child",
                                    width = fixed(200),
                                    height = fixed(100),
                                    action = action(id = "child"),
                                )
                            ),
                        )
                    ),
                )
            )
        )
    }

    private fun clickAt(x: Float, y: Float) {
        rule.onNodeWithTag("root").performTouchInput {
            click(Offset(x * width / 300f, y * height / 200f))
        }
    }
}
