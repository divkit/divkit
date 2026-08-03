package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.color
import com.yandex.div.test.data.colorExpression
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.solidBackground
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivExtension
import org.json.JSONObject
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewRecompositionCountTest {

    @get:Rule
    val rule = createComposeRule()

    private val background = Variable.ColorVariable(name = "background", defaultValue = 0)
    private val counter = Variable.IntegerVariable(name = "counter", defaultValue = 0)

    private val variableController = DivVariableController().apply {
        putOrUpdate(background, counter)
    }

    private val compositionCounterExtensionHandler = CompositionCounterExtensionHandler()

    private val configuration = DivComposeConfiguration(
        extensionHandlers = mapOf("composition_counter" to compositionCounterExtensionHandler),
        reporter = TestReporter(),
        variableController = variableController
    )

    @Test
    fun `every node is composed once on initial composition`() {
        setContent(
            container(
                extensions = compositionCounter("container"),
                items = listOf(
                    text(
                        extensions = compositionCounter("item1"),
                        text = expression("@{counter}")
                    ),
                    text(
                        extensions = compositionCounter("item2"),
                        text = "Item 2"
                    )
                )
            )
        )

        assertCompositions(
            "container" to 1,
            "item1" to 1,
            "item2" to 1
        )
    }

    @Test
    fun `only affected item is recomposed when background color is changed`() {
        setContent(
            container(
                extensions = compositionCounter("container"),
                items = listOf(
                    text(
                        extensions = compositionCounter("item1"),
                        backgrounds = listOf(solidBackground(colorExpression("@{background}"))),
                        text = "Item 1"
                    ),
                    container(
                        extensions = compositionCounter("inner_container"),
                        items = listOf(
                            text(
                                extensions = compositionCounter("item2"),
                                text = "Item 2"
                            )
                        )
                    )
                )
            )
        )

        background.set(color(0xFFAABBCC))
        rule.waitForIdle()

        assertCompositions(
            "container" to 1,
            "item1" to 2,
            "inner_container" to 1,
            "item2" to 1
        )
    }

    @Test
    fun `whole text node is not recomposed when text is changed`() {
        setContent(
            container(
                extensions = compositionCounter("container"),
                items = listOf(
                    text(
                        extensions = compositionCounter("item1"),
                        text = expression("@{counter}")
                    ),
                    text(
                        extensions = compositionCounter("item2"),
                        text = "Item 2"
                    )
                )
            )
        )

        counter.set(1)
        rule.waitForIdle()

        // The text is read in the composition of the text view, so the element node itself is left
        // alone.
        assertCompositions(
            "container" to 1,
            "item1" to 1,
            "item2" to 1
        )
    }

    @Test
    fun `nothing is recomposed when unused variable is changed`() {
        val unused = Variable.IntegerVariable(name = "unused", defaultValue = 0)
        variableController.putOrUpdate(unused)

        setContent(
            container(
                extensions = compositionCounter("container"),
                items = listOf(
                    text(
                        extensions = compositionCounter("item"),
                        backgrounds = listOf(solidBackground(colorExpression("@{background}"))),
                        text = "Item"
                    )
                )
            )
        )

        unused.set(1)
        rule.waitForIdle()

        assertCompositions(
            "container" to 1,
            "item" to 1
        )
    }

    @Test
    fun `items are not recomposed when container margins are changed`() {
        val margin = Variable.IntegerVariable(name = "margin", defaultValue = 0)
        variableController.putOrUpdate(margin)

        setContent(
            container(
                extensions = compositionCounter("container"),
                margins = DivEdgeInsets(
                    start = intExpression("@{margin}"),
                    end = intExpression("@{margin}")
                ),
                items = listOf(
                    text(
                        extensions = compositionCounter("item"),
                        text = "Item"
                    )
                )
            )
        )

        margin.set(10)
        rule.waitForIdle()

        assertCompositions(
            "container" to 2,
            "item" to 1
        )
    }

    @Test
    fun `items are not recomposed when container background is changed`() {
        setContent(
            container(
                extensions = compositionCounter("container"),
                backgrounds = listOf(solidBackground(colorExpression("@{background}"))),
                items = listOf(
                    text(
                        extensions = compositionCounter("item"),
                        text = "Item"
                    )
                )
            )
        )

        background.set(color(0xFFAABBCC))
        rule.waitForIdle()

        assertCompositions(
            "container" to 2,
            "item" to 1
        )
    }

    @Test
    fun `element with action with animation is recomposed once on touch input`() {
        setContent(
            text(
                action = action(id = "button"),
                extensions = compositionCounter("button"),
                text = "Button"
            )
        )

        assertCompositions("button" to 1)

        val button = rule.onNodeWithText("Button")
        button.performTouchInput { down(position = center) }
        rule.waitForIdle()

        assertCompositions("button" to 2)

        button.performTouchInput { up() }
        rule.waitForIdle()

        assertCompositions("button" to 3)
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }

    private fun assertCompositions(vararg expected: Pair<String, Int>) {
        assertEquals(expected.toMap(), compositionCounterExtensionHandler.compositions)
    }
}

private class CompositionCounterExtensionHandler : DivExtensionHandler {
    private val _compositions = mutableMapOf<String, Int>()
    val compositions: Map<String, Int> = _compositions

    // Skipping is disabled so that the counter reflects the composition of the div node it is
    // attached to and not the stability of its own parameters.
    @Composable
    @NonSkippableComposable
    override fun Content(
        modifier: Modifier,
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    ) {
        val name = environment.extension.params?.getString("name")
            ?: throw DivException("Extension parameter expected: name")
        SideEffect {
            _compositions[name] = (_compositions[name] ?: 0) + 1
        }
        content(modifier)
    }
}

private fun compositionCounter(name: String): List<DivExtension> {
    return listOf(
        DivExtension(
            id = "composition_counter",
            params = JSONObject(mapOf("name" to name))
        )
    )
}
