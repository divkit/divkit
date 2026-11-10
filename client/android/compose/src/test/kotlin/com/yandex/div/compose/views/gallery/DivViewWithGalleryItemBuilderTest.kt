package com.yandex.div.compose.views.gallery

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
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.action
import com.yandex.div.test.data.arrayExpression
import com.yandex.div.test.data.booleanExpression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.intExpression
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.scrollToAction
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionScrollDestination
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivData
import com.yandex.div2.DivGallery
import com.yandex.div2.DivPatch
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVisibility
import com.yandex.div2.ItemIdDestination
import com.yandex.div2.StrVariable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class DivViewWithGalleryItemBuilderTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val reporter = TestReporter()
    private val variableController = DivVariableController()
    private val divContext = DivContext(
        getApplicationContext(),
        DivConfiguration(
            actionHandler = actionHandler,
            reporter = reporter,
            variableController = variableController
        )
    )

    @Test
    fun `items are built from data with element and index constants`() {
        setContent(gallery(itemBuilder = titledItems()))

        rule.onNodeWithTag("item_0").assertTextEquals("First 0")
        rule.onNodeWithTag("item_1").assertTextEquals("Second 1")
    }

    @Test
    fun `prototype is reselected when a selector expression changes`() {
        val compact = Variable.BooleanVariable("compact", false)
        variableController.declare(compact)
        setContent(builtGallery(
            data = constant(jsonArray(jsonObject())),
            prototypes = listOf(
                prototype(
                    div = item(text = constant("compact")),
                    id = constant("item"),
                    selector = booleanExpression("@{compact}")
                ),
                prototype(div = item(text = constant("full")), id = constant("item"))
            )
        ))
        rule.onNodeWithTag("item").assertTextEquals("full")

        compact.set(true)

        rule.onNodeWithTag("item").assertTextEquals("compact")
    }

    @Test
    fun `item id follows its expression`() {
        val prefix = Variable.StringVariable("prefix", "before")
        variableController.declare(prefix)
        setContent(builtGallery(
            data = constant(jsonArray(jsonObject())),
            prototypes = listOf(prototype(div = item(), id = expression("@{prefix}_@{index}")))
        ))
        rule.onNodeWithTag("before_0").assertExists()

        prefix.set("after")

        rule.onNodeWithTag("after_0").assertExists()
        rule.onNodeWithTag("before_0").assertDoesNotExist()
    }

    @Test
    fun `item visibility is resolved in the item scope`() {
        setContent(builtGallery(
            data = constant(jsonArray(jsonObject("shown" to true), jsonObject("shown" to false))),
            prototypes = listOf(prototype(
                div = item(
                    visibility = visibilityExpression(
                        "@{it.getBoolean('shown') ? 'visible' : 'gone'}"
                    )
                ),
                id = expression("item_@{index}")
            ))
        ))

        rule.onNodeWithTag("item_0").assertIsDisplayed()
        rule.onNodeWithTag("item_1").assertDoesNotExist()
    }

    @Test
    fun `item keeps its local variable after it is scrolled out and back`() {
        variableController.declare(Variable.ArrayVariable("elements", elements(16)))
        setContent(editableGallery())
        rule.onNodeWithTag("item0").performClick()
        val gallery = rule.onNode(hasScrollToIndexAction())

        gallery.performScrollToIndex(15)
        rule.onNodeWithTag("item0").assertDoesNotExist()
        gallery.performScrollToIndex(0)

        rule.onNodeWithTag("item0").assertTextEquals("changed item0 0")
    }

    @Test
    fun `item with an id keeps its local state when an item is inserted before it`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("id" to "kept")))
        variableController.declare(elements)
        setContent(editableGallery())
        rule.onNodeWithTag("kept").performClick()
        rule.onNodeWithTag("kept").assertTextEquals("changed kept 0")

        elements.set(jsonArray(jsonObject("id" to "new"), jsonObject("id" to "kept")))

        rule.onNodeWithTag("kept").assertTextEquals("changed kept 1")
        rule.onNodeWithTag("new").assertTextEquals("initial new 0")
    }

    @Test
    fun `item without an id gets fresh local state when its data changes`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("id" to "item")))
        variableController.declare(elements)
        setContent(editableGallery(id = null))
        rule.onNodeWithText("initial item 0").performClick()
        rule.onNodeWithText("changed item 0").assertExists()

        elements.set(jsonArray(jsonObject("id" to "item", "version" to 2)))

        rule.onNodeWithText("initial item 0").assertExists()
    }

    @Test
    fun `item without an id gets fresh local state when its index changes`() {
        val elements = Variable.ArrayVariable("elements", jsonArray(jsonObject("id" to "item")))
        variableController.declare(elements)
        setContent(editableGallery(id = null))
        rule.onNodeWithText("initial item 0").performClick()
        rule.onNodeWithText("changed item 0").assertExists()

        elements.set(jsonArray(jsonObject("id" to "new"), jsonObject("id" to "item")))

        rule.onNodeWithText("initial item 1").assertExists()
    }

    @Test
    fun `nested builder shadows the outer data element`() {
        val inner = gallery(itemBuilder = itemBuilder(
            data = arrayExpression("@{getArrayFromDict(it, 'items')}"),
            prototypes = listOf(prototype(
                div = item(text = expression("@{it.getString('name')}")),
                id = expression("inner_@{index}")
            ))
        ))
        setContent(gallery(
            height = fixed(300),
            itemBuilder = itemBuilder(
                data = jsonArray(
                    jsonObject(
                        "name" to "outer",
                        "items" to jsonArray(jsonObject("name" to "inner"))
                    )
                ),
                prototypes = listOf(prototype(div = inner))
            ),
            orientation = constant(DivGallery.Orientation.VERTICAL),
            width = fixed(300)
        ))

        rule.onNodeWithTag("inner_0").assertTextEquals("inner")
    }

    @Test
    fun `root trigger of an offscreen item fires`() {
        val counter = Variable.IntegerVariable("counter", 0)
        val result = Variable.StringVariable("result", "initial")
        variableController.declare(counter, result)
        setContent(builtGallery(
            data = constant(JSONArray(List(16) { jsonObject("last" to (it == 15)) })),
            prototypes = listOf(
                prototype(
                    div = item(triggers = listOf(trigger(
                        action = action(url = "div-action://set_variable?name=result&value=fired"),
                        condition = "@{counter > 0}"
                    ))),
                    id = constant("last"),
                    selector = booleanExpression("@{it.getBoolean('last')}")
                ),
                prototype(div = item())
            )
        ))
        rule.onNodeWithTag("last").assertDoesNotExist()

        counter.set(1)

        rule.runOnIdle { assertEquals("fired", result.getValue()) }
    }

    @Test
    fun `nested trigger of an offscreen item fires`() {
        val counter = Variable.IntegerVariable("counter", 0)
        val result = Variable.StringVariable("result", "initial")
        variableController.declare(counter, result)
        setContent(builtGallery(
            data = constant(JSONArray(List(16) { jsonObject("last" to (it == 15)) })),
            prototypes = listOf(
                prototype(
                    div = nestedTrigger(trigger(
                        action = action(url = "div-action://set_variable?name=result&value=fired"),
                        condition = "@{counter > 0}"
                    )),
                    id = constant("last"),
                    selector = booleanExpression("@{it.getBoolean('last')}")
                ),
                prototype(div = item())
            )
        ))
        rule.onNodeWithTag("last").assertDoesNotExist()

        counter.set(1)

        rule.runOnIdle { assertEquals("fired", result.getValue()) }
    }

    @Test
    fun `nested trigger of a gone item fires`() {
        val counter = Variable.IntegerVariable("counter", 0)
        val result = Variable.StringVariable("result", "initial")
        variableController.declare(counter, result)
        setContent(builtGallery(
            data = constant(jsonArray(jsonObject())),
            prototypes = listOf(prototype(
                div = nestedTrigger(
                    trigger = trigger(
                        action = action(url = "div-action://set_variable?name=result&value=fired"),
                        condition = "@{counter > 0}"
                    ),
                    visibility = constant(DivVisibility.GONE)
                ),
                id = constant("gone")
            ))
        ))
        rule.onNodeWithTag("gone").assertDoesNotExist()

        counter.set(1)

        rule.runOnIdle { assertEquals("fired", result.getValue()) }
    }

    @Test
    fun `nested trigger of an offscreen item resolves the item data and its own variables`() {
        val counter = Variable.IntegerVariable("counter", 0)
        val result = Variable.StringVariable("result", "initial")
        variableController.declare(counter, result)
        setContent(builtGallery(
            data = constant(elements(16)),
            prototypes = listOf(
                prototype(
                    div = nestedTrigger(
                        trigger = trigger(
                            action = action(url = uriExpression(
                                "div-action://set_variable?name=result" +
                                    "&value=@{it.getString('id')}_@{index}_@{local}"
                            )),
                            condition = "@{counter > 0}"
                        ),
                        variables = listOf(variable("local", "nested"))
                    ),
                    id = constant("last"),
                    selector = booleanExpression("@{index == 15}")
                ),
                prototype(div = item())
            )
        ))
        rule.onNodeWithTag("last").assertDoesNotExist()

        counter.set(1)

        rule.runOnIdle { assertEquals("item15_15_nested", result.getValue()) }
    }

    @Test
    fun `nested trigger of an offscreen item resolves the variables of its scoped ancestors`() {
        val counter = Variable.IntegerVariable("counter", 0)
        val result = Variable.StringVariable("result", "initial")
        variableController.declare(counter, result)
        val child = item(
            triggers = listOf(trigger(
                action = action(url = uriExpression(
                    "div-action://set_variable?name=result&value=@{outer}_@{middle}_@{own}"
                )),
                condition = "@{counter > 0}"
            )),
            variables = listOf(DivVariable.Str(StrVariable(
                name = "own",
                value = expression("@{outer}@{middle}")
            )))
        )
        val middle = container(
            items = listOf(child),
            variables = listOf(variable("middle", "b")),
            width = wrapContent()
        )
        setContent(builtGallery(
            data = constant(elements(16)),
            prototypes = listOf(
                prototype(
                    div = container(
                        items = listOf(container(items = listOf(middle), width = wrapContent())),
                        variables = listOf(variable("outer", "a")),
                        width = wrapContent()
                    ),
                    id = constant("last"),
                    selector = booleanExpression("@{index == 15}")
                ),
                prototype(div = item())
            )
        ))
        rule.onNodeWithTag("last").assertDoesNotExist()

        counter.set(1)

        rule.runOnIdle { assertEquals("a_b_ab", result.getValue()) }
    }

    @Test
    fun `scoped child of an item without triggers takes its initial values when rendered`() {
        val seed = Variable.StringVariable("seed", "before")
        variableController.declare(seed)
        setContent(builtGallery(
            data = constant(elements(16)),
            prototypes = listOf(
                prototype(
                    div = container(
                        items = listOf(item(
                            id = "child",
                            text = expression("@{own}"),
                            variables = listOf(DivVariable.Str(StrVariable(
                                name = "own",
                                value = expression("@{seed}")
                            )))
                        )),
                        width = wrapContent()
                    ),
                    selector = booleanExpression("@{index == 15}")
                ),
                prototype(div = item())
            )
        ))
        rule.onNodeWithTag("child").assertDoesNotExist()
        seed.set("after")

        rule.onNode(hasScrollToIndexAction()).performScrollToIndex(15)

        rule.onNodeWithTag("child").assertTextEquals("after")
    }

    @Test
    fun `trigger of an ignored item of a nested item_builder does not fire`() {
        val counter = Variable.IntegerVariable("counter", 0)
        val result = Variable.StringVariable("result", "initial")
        variableController.declare(counter, result)
        val nested = container(
            itemBuilder = itemBuilder(
                data = jsonArray(),
                prototypes = listOf(prototype(div = item()))
            ),
            items = listOf(nestedTrigger(trigger(
                action = action(url = "div-action://set_variable?name=result&value=fired"),
                condition = "@{counter > 0}"
            )))
        )
        setContent(builtGallery(
            data = constant(jsonArray(jsonObject())),
            prototypes = listOf(prototype(div = nested))
        ))

        counter.set(1)

        rule.runOnIdle { assertEquals("initial", result.getValue()) }
    }

    @Test
    fun `nested trigger keeps firing while its item is scrolled out and back`() {
        val condition = Variable.BooleanVariable("condition", false)
        val firings = Variable.IntegerVariable("firings", 0)
        variableController.declare(condition, firings)
        setContent(builtGallery(
            data = constant(elements(16)),
            prototypes = listOf(
                prototype(
                    div = nestedTrigger(trigger(
                        action = action(typed = setVariableAction(
                            name = "firings",
                            value = typedValue(intExpression("@{firings + 1}"))
                        )),
                        condition = "@{condition}"
                    )),
                    id = constant("first"),
                    selector = booleanExpression("@{index == 0}")
                ),
                prototype(div = item())
            )
        ))
        val gallery = rule.onNode(hasScrollToIndexAction())

        gallery.performScrollToIndex(15)
        rule.onNodeWithTag("first").assertDoesNotExist()
        rule.runOnIdle { condition.set(true) }
        rule.runOnIdle { condition.set(false) }
        gallery.performScrollToIndex(0)
        rule.runOnIdle { condition.set(true) }

        rule.runOnIdle { assertEquals(2L, firings.getValue()) }
    }

    @Test
    fun `horizontal two-column gallery renders built items in their own scope`() {
        setContent(gallery(
            columnCount = constant(2L),
            height = fixed(200),
            itemBuilder = titledItems(),
            width = fixed(300)
        ))

        rule.onNodeWithTag("item_0").assertTextEquals("First 0")
        rule.onNodeWithTag("item_1").assertTextEquals("Second 1")
    }

    @Test
    fun `vertical two-column gallery renders built items in their own scope`() {
        setContent(gallery(
            columnCount = constant(2L),
            height = fixed(300),
            itemBuilder = titledItems(),
            orientation = constant(DivGallery.Orientation.VERTICAL),
            width = fixed(300)
        ))

        rule.onNodeWithTag("item_0").assertTextEquals("First 0")
        rule.onNodeWithTag("item_1").assertTextEquals("Second 1")
    }

    @Test
    fun `two-column gallery of content height renders built items in their own scope`() {
        setContent(gallery(
            columnCount = constant(2L),
            height = wrapContent(),
            itemBuilder = titledItems(),
            width = fixed(300)
        ))

        rule.onNodeWithTag("item_0").assertTextEquals("First 0")
        rule.onNodeWithTag("item_1").assertTextEquals("Second 1")
    }

    @Test
    fun `horizontal non-scrollable gallery renders built items in their own scope`() {
        setContent(gallery(itemBuilder = titledItems(), width = wrapContent()))

        rule.onNodeWithTag("item_0").assertTextEquals("First 0")
        rule.onNodeWithTag("item_1").assertTextEquals("Second 1")
    }

    @Test
    fun `vertical non-scrollable gallery renders built items in their own scope`() {
        setContent(gallery(
            height = wrapContent(),
            itemBuilder = titledItems(),
            orientation = constant(DivGallery.Orientation.VERTICAL)
        ))

        rule.onNodeWithTag("item_0").assertTextEquals("First 0")
        rule.onNodeWithTag("item_1").assertTextEquals("Second 1")
    }

    @Test
    fun `scroll to an item id reaches a built item`() {
        val card = data(builtGallery(
            data = constant(elements(16)),
            id = "gallery",
            prototypes = listOf(prototype(div = item(), id = expression("item_@{index}")))
        ))
        setContent(card)
        rule.onNodeWithTag("item_12").assertDoesNotExist()

        perform(card, action(typed = scrollToAction(
            id = "gallery",
            destination = DivActionScrollDestination.ItemId(ItemIdDestination(constant("item_12"))),
            animated = false
        )))

        rule.onNodeWithTag("item_12").assertIsDisplayed()
    }

    @Test
    fun `scroll to an id shared by items built from a prototype without an id is reported`() {
        reporter.failOnError = false
        val card = data(builtGallery(
            data = constant(jsonArray(jsonObject(), jsonObject())),
            id = "gallery",
            prototypes = listOf(prototype(div = item(id = "element")))
        ))
        setContent(card)

        perform(card, action(typed = scrollToAction(
            id = "gallery",
            destination = DivActionScrollDestination.ItemId(ItemIdDestination(constant("element"))),
            animated = false
        )))

        assertEquals(
            listOf("Several items with id 'element' found in 'gallery' for scroll_to action"),
            reporter.errors
        )
    }

    @Test
    fun `set_state on the root state of a built item updates its state_id_variable`() {
        val active = Variable.StringVariable("active", "first")
        variableController.declare(active)
        val card = data(builtGallery(
            data = constant(jsonArray(jsonObject())),
            prototypes = listOf(prototype(
                div = state(
                    stateIdVariable = "active",
                    states = listOf(
                        state(div = item(text = constant("first")), stateId = "first"),
                        state(div = item(text = constant("second")), stateId = "second")
                    ),
                    width = fixed(100)
                ),
                id = constant("switch")
            ))
        ))
        setContent(card)

        perform(card, action(url = "div-action://set_state?state_id=0/switch/second"))

        rule.onNodeWithText("second").assertExists()
        rule.runOnIdle { assertEquals("second", active.getValue()) }
    }

    @Test
    fun `item built from a prototype without an id keeps its own id`() {
        setContent(builtGallery(
            data = constant(jsonArray(jsonObject())),
            prototypes = listOf(prototype(div = item(id = "element", text = constant("built"))))
        ))

        rule.onNodeWithTag("element").assertTextEquals("built")
    }

    @Test
    fun `item child keeps its local state after a patch replaces a sibling of the gallery`() {
        variableController.declare(Variable.ArrayVariable("elements", elements(16)))
        val card = data(container(items = listOf(
            builtGallery(
                data = arrayExpression("@{elements}"),
                prototypes = listOf(prototype(
                    div = container(items = listOf(editableItem()), width = wrapContent()),
                    id = expression("@{it.getString('id')}")
                ))
            ),
            text(id = "target", text = "before")
        )))
        setContent(card)
        rule.onNodeWithText("initial item0 0").performClick()
        rule.onNodeWithText("changed item0 0").assertExists()
        val gallery = rule.onNode(hasScrollToIndexAction())

        applyPatch(card, DivPatch.Change("target", listOf(text(text = "after"))))
        gallery.performScrollToIndex(15)
        gallery.performScrollToIndex(0)

        rule.onNodeWithText("changed item0 0").assertExists()
    }

    @Test
    fun `items start with fresh local state when a patch replaces the gallery`() {
        variableController.declare(Variable.ArrayVariable("elements", elements(1)))
        val card = data(container(items = listOf(editableGallery(galleryId = "target"))))
        setContent(card)
        rule.onNodeWithTag("item0").performClick()
        rule.onNodeWithTag("item0").assertTextEquals("changed item0 0")

        applyPatch(card, DivPatch.Change("target", listOf(editableGallery(galleryId = "target"))))

        rule.onNodeWithTag("item0").assertTextEquals("initial item0 0")
    }

    @Test
    fun `items start with fresh local state when a patch replaces the gallery with the same builder`() {
        variableController.declare(Variable.ArrayVariable("elements", elements(1)))
        val items = itemBuilder(
            data = arrayExpression("@{elements}"),
            prototypes = listOf(prototype(
                div = editableItem(),
                id = expression("@{it.getString('id')}")
            ))
        )
        val target = {
            gallery(height = fixed(50), id = "target", itemBuilder = items, width = fixed(300))
        }
        val card = data(container(items = listOf(target())))
        setContent(card)
        rule.onNodeWithTag("item0").performClick()
        rule.onNodeWithTag("item0").assertTextEquals("changed item0 0")

        applyPatch(card, DivPatch.Change("target", listOf(target())))

        rule.onNodeWithTag("item0").assertTextEquals("initial item0 0")
    }

    @Test
    fun `item observers are released when the data is emptied`() {
        val elements = Variable.ArrayVariable("elements", jsonArray())
        variableController.declare(elements)
        val observers = declareObservedVariable("parent")
        setContent(builtGallery(
            data = arrayExpression("@{elements}"),
            prototypes = listOf(prototype(div = item(
                text = expression("@{local}:@{parent}"),
                variables = listOf(variable("local", "item"))
            )))
        ))
        rule.waitForIdle()
        val cardObservers = observers.toSet()
        elements.set(jsonArray(jsonObject("version" to 1)))
        rule.waitForIdle()
        assertTrue(observers.size > cardObservers.size)

        elements.set(jsonArray())
        rule.waitForIdle()

        assertEquals(cardObservers, observers)
    }

    @Test
    fun `nested trigger observers are released when the data is emptied`() {
        val elements = Variable.ArrayVariable("elements", jsonArray())
        variableController.declare(elements)
        val observers = declareObservedVariable("parent")
        setContent(builtGallery(
            data = arrayExpression("@{elements}"),
            prototypes = listOf(prototype(div = nestedTrigger(parentTrigger())))
        ))
        rule.waitForIdle()
        val cardObservers = observers.toSet()
        elements.set(jsonArray(jsonObject()))
        rule.waitForIdle()
        assertTrue(observers.size > cardObservers.size)

        elements.set(jsonArray())
        rule.waitForIdle()

        assertEquals(cardObservers, observers)
    }

    @Test
    fun `nested trigger observers are released when a patch removes the gallery`() {
        val elements = Variable.ArrayVariable("elements", jsonArray())
        variableController.declare(elements)
        val observers = declareObservedVariable("parent")
        val card = data(container(items = listOf(
            builtGallery(
                data = arrayExpression("@{elements}"),
                id = "target",
                prototypes = listOf(prototype(div = nestedTrigger(parentTrigger())))
            ),
            text(text = "sibling")
        )))
        setContent(card)
        rule.waitForIdle()
        val cardObservers = observers.toSet()
        elements.set(jsonArray(jsonObject()))
        rule.waitForIdle()
        assertTrue(observers.size > cardObservers.size)

        applyPatch(card, DivPatch.Change("target"))

        assertEquals(cardObservers, observers)
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

    private fun applyPatch(card: DivData, change: DivPatch.Change) {
        rule.runOnIdle {
            assertTrue(runBlocking {
                divContext.patcher.applyPatch(card, DivPatch(
                    changes = listOf(change),
                    onAppliedActions = listOf(action(id = "applied"))
                ))
            })
        }
        rule.waitForIdle()
        // onAppliedActions run right after the patch cleanup, so the cleanup has happened.
        assertEquals(
            listOf(actionData(id = "applied", source = DivActionSource.PATCH)),
            actionHandler.handledActions
        )
    }

    /**
     * Declares a card variable [name] and returns its live observers, so that a leaked scope stays
     * visible.
     */
    private fun declareObservedVariable(name: String): Set<(Variable) -> Unit> {
        val variable = spy(Variable.BooleanVariable(name, false))
        val observers = mutableSetOf<(Variable) -> Unit>()
        doAnswer {
            observers.add(it.getArgument(0))
            it.callRealMethod()
        }.whenever(variable).addObserver(any())
        doAnswer {
            observers.remove(it.getArgument<(Variable) -> Unit>(0))
            it.callRealMethod()
        }.whenever(variable).removeObserver(any())
        variableController.declare(variable)
        return observers
    }
}

/**
 * A container without its own scope around an item that declares [trigger].
 */
private fun nestedTrigger(
    trigger: DivTrigger,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
): Div = container(
    items = listOf(item(triggers = listOf(trigger), variables = variables)),
    visibility = visibility,
    width = wrapContent()
)

private fun parentTrigger(): DivTrigger = trigger(
    action = action(url = "div-action://set_variable?name=parent&value=false"),
    condition = "@{parent}"
)

/**
 * Elements `{"id": "item<index>"}`.
 */
private fun elements(count: Int) = JSONArray(List(count) { jsonObject("id" to "item$it") })

/**
 * Items `First 0` and `Second 1` with ids `item_0` and `item_1`.
 */
private fun titledItems(): DivCollectionItemBuilder = itemBuilder(
    data = jsonArray(jsonObject("title" to "First"), jsonObject("title" to "Second")),
    prototypes = listOf(prototype(
        div = item(text = expression("@{it.getString('title')} @{index}")),
        id = expression("item_@{index}")
    ))
)

/**
 * A 300 dp wide gallery of 100 dp items, so only the first items are composed.
 */
private fun builtGallery(
    data: Expression<JSONArray>,
    prototypes: List<DivCollectionItemBuilder.Prototype>,
    id: String? = null,
): Div = gallery(
    height = fixed(50),
    id = id,
    itemBuilder = itemBuilder(data = data, prototypes = prototypes),
    width = fixed(300)
)

/**
 * A gallery of [editableItem]s built from `@{elements}`.
 */
private fun editableGallery(
    id: Expression<String>? = expression("@{it.getString('id')}"),
    galleryId: String? = null,
): Div = builtGallery(
    data = arrayExpression("@{elements}"),
    id = galleryId,
    prototypes = listOf(prototype(div = editableItem(), id = id))
)

/**
 * An item showing `<local> <id> <index>`; a click sets its local variable to `changed`.
 */
private fun editableItem(): Div = item(
    action = action(url = "div-action://set_variable?name=local&value=changed"),
    text = expression("@{local} @{it.getString('id')} @{index}"),
    variables = listOf(variable("local", "initial"))
)

private fun item(
    action: DivAction? = null,
    id: String? = null,
    text: Expression<String> = constant("item"),
    triggers: List<DivTrigger>? = null,
    variables: List<DivVariable>? = null,
    visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
): Div = text(
    action = action,
    height = fixed(50),
    id = id,
    text = text,
    triggers = triggers,
    variables = variables,
    visibility = visibility,
    width = fixed(100)
)
