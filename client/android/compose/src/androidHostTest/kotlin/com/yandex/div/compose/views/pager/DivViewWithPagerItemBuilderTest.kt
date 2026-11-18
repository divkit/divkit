package com.yandex.div.compose.views.pager

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.divConfiguration
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.render.common.pager.PagerItemWindow
import com.yandex.div.test.data.action
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.scrollToAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivData
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import com.yandex.div2.ItemIdDestination
import kotlin.test.Test
import kotlin.test.assertEquals
import org.json.JSONArray
import org.junit.Rule
import org.junit.runner.RunWith

private const val PAGE_WIDTH = 300

@RunWith(AndroidJUnit4::class)
class DivViewWithPagerItemBuilderTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()
    private val variableController = DivVariableController()
    private val divContext = DivContext(
        getApplicationContext(),
        divConfiguration {
            actionHandler = this@DivViewWithPagerItemBuilderTest.actionHandler
            reporter = this@DivViewWithPagerItemBuilderTest.reporter
            variableController = this@DivViewWithPagerItemBuilderTest.variableController
        }
    )

    @Test
    fun `pages are built from data with element and index constants`() {
        setContent(builtPager(
            data = constant(jsonArray(
                jsonObject("title" to "First"),
                jsonObject("title" to "Second")
            )),
            prototypes = listOf(prototype(
                div = page(text = expression("@{it.getString('title')} @{index}")),
                id = expression("page_@{index}")
            )),
            defaultItem = 1
        ))

        rule.onNodeWithTag("page_1").assertTextEquals("Second 1")
    }

    @Test
    fun `item_count_variable counts the visible built pages`() {
        val count = Variable.IntegerVariable("count", -1)
        variableController.declare(count)
        setContent(pager(
            itemBuilder = itemBuilder(
                data = jsonArray(jsonObject(), jsonObject(), jsonObject()),
                prototypes = listOf(prototype(div = page(
                    visibility = visibilityExpression("@{index != 0 ? 'visible' : 'gone'}")
                )))
            ),
            itemCountVariable = "count"
        ))

        rule.runOnIdle { assertEquals(2L, count.getValue()) }
    }

    @Test
    fun `selected_actions of a built page resolve its index`() {
        setContent(builtPager(
            data = constant(jsonArray(jsonObject(), jsonObject(), jsonObject())),
            prototypes = listOf(prototype(div = page(
                selectedActions = listOf(
                    action(url = uriExpression("divkit-test://select?page=@{index}"))
                )
            ))),
            defaultItem = 1
        ))

        rule.runOnIdle { assertEquals(listOf("1"), selections()) }
    }

    @Test
    fun `data change reruns selected_actions of the current page only without an id`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("v" to 1)))
        variableController.declare(elements)
        setContent(container(items = listOf(
            selectablePager(name = "anonymous", id = null),
            selectablePager(name = "identified", id = constant("page"))
        )))

        elements.set(jsonArray(jsonObject("v" to 2)))

        rule.runOnIdle {
            assertEquals(listOf("anonymous_1", "identified_1", "anonymous_2"), selections())
        }
    }

    @Test
    fun `page with an id keeps its state and stays current when a page is inserted before it`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("id" to "kept")))
        variableController.declare(elements)
        setContent(editablePager())
        rule.onNodeWithTag("kept").performClick()

        elements.set(jsonArray(jsonObject("id" to "new"), jsonObject("id" to "kept")))

        rule.onNodeWithTag("kept").assertIsDisplayed().assertTextEquals("changed kept 1")
    }

    @Test
    fun `page without an id gets fresh local state when its data changes`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("id" to "page")))
        variableController.declare(elements)
        setContent(editablePager(id = null))
        rule.onNodeWithText("initial page 0").performClick()
        rule.onNodeWithText("changed page 0").assertExists()

        elements.set(jsonArray(jsonObject("id" to "page", "version" to 2)))

        rule.onNodeWithText("initial page 0").assertIsDisplayed()
    }

    @Test
    fun `cycle copies of a built page share its local state in infinite scroll`() {
        variableController.declare(Variable.ArrayVariable("elements", elements(3)))
        setContent(editablePager(infiniteScroll = true))
        rule.onNodeWithTag("page0").performClick()
        val window = PagerItemWindow.virtuallyUnbounded(realItemCount = 3)

        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(window.rawIndex(0) + 3)

        rule.onNodeWithTag("page0").assertIsDisplayed().assertTextEquals("changed page0 0")
    }

    @Test
    fun `scroll to an item id reaches a built page`() {
        val card = data(builtPager(
            data = constant(elements(5)),
            id = "pager",
            prototypes = listOf(prototype(div = page(), id = expression("page_@{index}")))
        ))
        setContent(card)
        rule.onNodeWithTag("page_3").assertDoesNotExist()

        perform(card, scrollTo("page_3"))

        rule.onNodeWithTag("page_3").assertIsDisplayed()
    }

    @Test
    fun `scroll to an id shared by pages built from a prototype without an id is reported`() {
        reporter.failOnError = false
        val card = data(builtPager(
            data = constant(jsonArray(jsonObject(), jsonObject())),
            id = "pager",
            prototypes = listOf(prototype(div = page(id = "element")))
        ))
        setContent(card)

        perform(card, scrollTo("element"))

        assertEquals(
            listOf("Several items with id 'element' found in 'pager' for scroll_to action"),
            reporter.errors
        )
    }

    private fun setContent(content: Div) = setContent(data(content))

    private fun setContent(card: DivData) {
        rule.setContent {
            CompositionLocalProvider(LocalDivContext provides divContext) {
                DivView(card)
            }
        }
    }

    private fun perform(card: DivData, action: DivAction) {
        rule.runOnIdle { divContext.debugFeatures.performAction(card, action) }
    }

    private fun selections(): List<String?> = actionHandler.handledActions
        .filter { it.source == DivActionSource.SELECTION }
        .map { it.url?.getQueryParameter("page") }
}

private fun scrollTo(itemId: String): DivAction = action(typed = scrollToAction(
    id = "pager",
    destination = DivActionScrollDestination.ItemId(ItemIdDestination(constant(itemId))),
    animated = false
))

private fun elements(count: Int) = JSONArray(List(count) { jsonObject("id" to "page$it") })

private fun builtPager(
    data: Expression<JSONArray>,
    prototypes: List<DivCollectionItemBuilder.Prototype>,
    defaultItem: Long = 0,
    id: String? = null,
    infiniteScroll: Boolean = false,
): Div = pager(
    defaultItem = defaultItem,
    id = id,
    infiniteScroll = infiniteScroll,
    itemBuilder = itemBuilder(data = data, prototypes = prototypes),
    width = fixed(PAGE_WIDTH)
)

private fun editablePager(
    id: Expression<String>? = expression("@{it.getString('id')}"),
    infiniteScroll: Boolean = false,
): Div = builtPager(
    data = arrayExpression("@{elements}"),
    infiniteScroll = infiniteScroll,
    prototypes = listOf(prototype(
        div = page(
            action = action(url = "div-action://set_variable?name=local&value=changed"),
            text = expression("@{local} @{it.getString('id')} @{index}"),
            variables = listOf(variable("local", "initial"))
        ),
        id = id
    ))
)

private fun selectablePager(name: String, id: Expression<String>?): Div = builtPager(
    data = arrayExpression("@{elements}"),
    prototypes = listOf(prototype(
        div = page(selectedActions = listOf(
            action(url = uriExpression("divkit-test://select?page=${name}_@{it.getInteger('v')}"))
        )),
        id = id
    ))
)

private fun page(
    action: DivAction? = null,
    id: String? = null,
    selectedActions: List<DivAction>? = null,
    text: Expression<String> = constant("page"),
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
): Div = text(
    action = action,
    id = id,
    selectedActions = selectedActions,
    text = text,
    variables = variables,
    visibility = visibility,
    width = fixed(PAGE_WIDTH)
)
