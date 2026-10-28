package com.yandex.div.compose.container

import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
import com.yandex.div.test.data.action
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityAction
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivMatchParentSize
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue
import com.yandex.div2.DivVariable
import com.yandex.div2.IntegerVariable
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivViewWithItemBuilderTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()
    private val variableController = DivVariableController()

    private val configuration = DivConfiguration(
        actionHandler = actionHandler,
        reporter = reporter,
        variableController = variableController
    )

    @Test
    fun `items are built from data with element and index variables`() {
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(
                        jsonObject("title" to "First"),
                        jsonObject("title" to "Second")
                    ),
                    dataElementName = "item",
                    prototypes = listOf(
                        prototype(
                            div = text(text = expression("@{item.getString('title')} #@{index}")),
                            id = expression("item_@{index}")
                        )
                    )
                )
            )
        )

        rule.onNodeWithTag("item_0").assertTextEquals("First #0")
        rule.onNodeWithTag("item_1").assertTextEquals("Second #1")
    }

    @Test
    fun `data element named index is shadowed by the item index with a warning`() {
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject("title" to "First")),
                    dataElementName = "index",
                    prototypes = listOf(
                        prototype(
                            div = text(text = expression("index = @{index}")),
                            id = constant("item")
                        )
                    )
                )
            )
        )

        rule.onNodeWithTag("item").assertTextEquals("index = 0")
        assertTrue(reporter.warnings.any { "'index'" in it }, reporter.warnings.toString())
    }

    @Test
    fun `first matching prototype is selected and elements without match are skipped`() {
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(
                        jsonObject("type" to "a"),
                        jsonObject("type" to "b"),
                        jsonObject("type" to "c")
                    ),
                    prototypes = listOf(
                        prototype(
                            div = text(text = expression("@{it.getString('type')}")),
                            id = expression("first_@{index}"),
                            selector = booleanExpression("@{it.getString('type') == 'a'}")
                        ),
                        prototype(
                            div = text(text = expression("@{it.getString('type')}")),
                            id = expression("second_@{index}"),
                            selector = booleanExpression("@{it.getString('type') == 'a'}")
                        ),
                        prototype(
                            div = text(text = expression("@{it.getString('type')}")),
                            id = expression("third_@{index}"),
                            selector = booleanExpression("@{it.getString('type') == 'b'}")
                        )
                    )
                )
            )
        )

        rule.onNodeWithTag("first_0").assertTextEquals("a")
        rule.onNodeWithTag("second_0").assertDoesNotExist()
        rule.onNodeWithTag("third_1").assertTextEquals("b")
        rule.onNodeWithText("c").assertDoesNotExist()
    }

    @Test
    fun `prototype is reselected when a selector expression changes`() {
        val compact = Variable.BooleanVariable("compact", false)
        variableController.declare(compact)

        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject("title" to "First")),
                    prototypes = listOf(
                        prototype(
                            div = text(text = expression("@{it.getString('title')}")),
                            id = expression("compact_@{index}"),
                            selector = booleanExpression("@{compact}")
                        ),
                        prototype(
                            div = text(text = expression("@{it.getString('title')} (full)")),
                            id = expression("full_@{index}")
                        )
                    )
                )
            )
        )

        rule.onNodeWithTag("full_0").assertTextEquals("First (full)")
        rule.onNodeWithTag("compact_0").assertDoesNotExist()

        compact.set(true)

        rule.onNodeWithTag("compact_0").assertTextEquals("First")
        rule.onNodeWithTag("full_0").assertDoesNotExist()
    }

    @Test
    fun `item_builder has priority over items`() {
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject()),
                    prototypes = listOf(prototype(div = text(text = "built")))
                ),
                items = listOf(text(text = "static"))
            )
        )

        rule.onNodeWithText("built").assertExists()
        rule.onNodeWithText("static").assertDoesNotExist()
    }

    @Test
    fun `prototype without id drops the element id`() {
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject()),
                    prototypes = listOf(prototype(div = text(id = "element", text = "built")))
                )
            )
        )

        rule.onNodeWithText("built").assertExists()
        rule.onNodeWithTag("element").assertDoesNotExist()
    }

    @Test
    fun `element that is not a dictionary is reported and skipped`() {
        reporter.failOnError = false

        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray("oops", jsonObject("title" to "ok")),
                    prototypes = listOf(
                        prototype(div = text(text = expression("@{it.getString('title')}")))
                    )
                )
            )
        )

        // Characterization of the shared typeMismatch() message, checked by hand: one error that
        // names the position 0 and the actual type of the element.
        assertEquals(
            listOf("Item builder data at 0 position has wrong type: java.lang.String"),
            reporter.errors
        )
        rule.onNodeWithText("ok").assertExists()
    }

    @Test
    fun `nested builder shadows the outer data element`() {
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(
                        jsonObject(
                            "name" to "outer",
                            "items" to jsonArray(jsonObject("name" to "inner"))
                        )
                    ),
                    prototypes = listOf(
                        prototype(
                            div = container(
                                itemBuilder = itemBuilder(
                                    data = arrayExpression("@{getArrayFromDict(it, 'items')}"),
                                    prototypes = listOf(
                                        prototype(
                                            div = text(
                                                text = expression("@{it.getString('name')}")
                                            ),
                                            id = expression("inner_@{index}")
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        rule.onNodeWithTag("inner_0").assertTextEquals("inner")
    }

    @Test
    fun `overlap builder child resolves measurement bounds and margins in its own scope`() {
        setContent(
            container(
                width = wrapContent(),
                orientation = DivContainer.Orientation.OVERLAP,
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject("margin" to 16, "minimum_width" to 80)),
                    prototypes = listOf(
                        prototype(
                            div = text(
                                text = constant("child"),
                                height = fixed(20),
                                width = DivSize.MatchParent(
                                    DivMatchParentSize(
                                        minSize = DivSizeUnitValue(
                                            value = intExpression("@{minimum_width}")
                                        )
                                    )
                                ),
                                margins = DivEdgeInsets(
                                    start = intExpression("@{it.getInteger('margin')}")
                                ),
                                variables = listOf(
                                    DivVariable.Integer(
                                        IntegerVariable(
                                            name = "minimum_width",
                                            value = intExpression(
                                                "@{it.getInteger('minimum_width')}"
                                            )
                                        )
                                    )
                                )
                            ),
                            id = constant("item")
                        )
                    )
                )
            )
        )

        rule.onNodeWithTag("item")
            .assertLeftPositionInRootIsEqualTo(16.dp)
            .assertWidthIsEqualTo(80.dp)
    }

    @Test
    fun `setting the index variable preserves the item index and updates the parent variable`() {
        val index = Variable.IntegerVariable("index", 7)
        variableController.declare(index)
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject()),
                    prototypes = listOf(
                        prototype(
                            div = text(
                                text = expression("index = @{index}"),
                                variables = listOf(variable("unrelated", 1L)),
                                action = action(
                                    url = "div-action://set_variable?name=index&value=99"
                                )
                            ),
                            id = constant("item")
                        )
                    )
                )
            )
        )

        rule.onNodeWithTag("item").performClick()

        rule.onNodeWithTag("item").assertTextEquals("index = 0")
        assertEquals(99L, index.getValue())
    }

    @Test
    fun `hidden item becomes visible when its local trigger fires`() {
        val step = Variable.IntegerVariable("step", 0)
        variableController.declare(step)
        val hide = action(url = "div-action://set_variable?name=hidden&value=true")
        val show = action(url = "div-action://set_variable?name=hidden&value=false")
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject()),
                    prototypes = listOf(
                        prototype(
                            div = text(
                                text = "item",
                                variables = listOf(variable("hidden", false)),
                                visibility = visibilityExpression("@{hidden ? 'gone' : 'visible'}"),
                                triggers = listOf(
                                    trigger(
                                        action = hide,
                                        condition = "@{step == 1}"
                                    ),
                                    trigger(
                                        action = show,
                                        condition = "@{step == 2}"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        step.set(1)
        rule.onNodeWithText("item").assertDoesNotExist()

        step.set(2)

        rule.onNodeWithText("item").assertExists()
    }

    @Test
    fun `initially gone item stops triggering after removal from data`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject()))
        val condition = Variable.BooleanVariable("condition", false)
        val result = Variable.StringVariable("result", "initial")
        variableController.declare(elements, condition, result)
        val change = action(url = "div-action://set_variable?name=result&value=changed")
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = arrayExpression("@{elements}"),
                    prototypes = listOf(
                        prototype(
                            div = text(
                                text = "item",
                                variables = listOf(variable("hidden", true)),
                                visibility = visibilityExpression("@{hidden ? 'gone' : 'visible'}"),
                                triggers = listOf(
                                    trigger(
                                        action = change,
                                        condition = "@{condition}"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        elements.set(jsonArray())
        rule.waitForIdle()

        condition.set(true)

        assertEquals("initial", result.getValue())
    }

    @Test
    fun `wrap container renders built items in their own scope`() {
        variableController.declare(Variable.DictVariable("it", jsonObject("title" to "card")))
        setContent(
            container(
                items = listOf(
                    wrapBuilder(DivContainer.Orientation.HORIZONTAL),
                    wrapBuilder(DivContainer.Orientation.VERTICAL)
                )
            )
        )

        rule.onNodeWithTag("horizontal").assertTextEquals("horizontal")
        rule.onNodeWithTag("vertical").assertTextEquals("vertical")
    }

    @Test
    fun `items built from one prototype log their own visibility actions`() {
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject(), jsonObject()),
                    prototypes = listOf(
                        prototype(
                            div = text(
                                text = "item",
                                visibilityActions = listOf(
                                    visibilityAction(delayMs = 0)
                                        .copy(logId = expression("seen_@{index}"))
                                )
                            ),
                            id = expression("item_@{index}")
                        )
                    )
                )
            )
        )

        assertEquals(
            listOf(
                actionData(id = "seen_0", source = DivActionSource.VISIBILITY),
                actionData(id = "seen_1", source = DivActionSource.VISIBILITY)
            ),
            actionHandler.handledActions
        )
    }

    @Test
    fun `items built from one prototype count the log limit separately`() {
        // Compose counts log_limit per element, so a log_id shared by the items logs once each.
        setContent(
            container(
                itemBuilder = itemBuilder(
                    data = jsonArray(jsonObject(), jsonObject()),
                    prototypes = listOf(
                        prototype(
                            div = text(
                                text = "item",
                                visibilityActions = listOf(
                                    visibilityAction(delayMs = 0, id = "seen")
                                )
                            ),
                            id = expression("item_@{index}")
                        )
                    )
                )
            )
        )

        assertEquals(
            List(2) { actionData(id = "seen", source = DivActionSource.VISIBILITY) },
            actionHandler.handledActions
        )
    }

    private fun wrapBuilder(orientation: DivContainer.Orientation): Div {
        val name = orientation.name.lowercase()
        return container(
            layoutMode = DivContainer.LayoutMode.WRAP,
            orientation = orientation,
            itemBuilder = itemBuilder(
                data = jsonArray(jsonObject("title" to name)),
                prototypes = listOf(
                    prototype(
                        div = text(text = expression("@{it.getString('title')}")),
                        id = constant(name)
                    )
                )
            )
        )
    }

    private fun setContent(content: Div) {
        rule.setContent(
            configuration = configuration,
            data = data(content)
        )
    }
}
