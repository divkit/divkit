package com.yandex.div.compose.actions

import com.yandex.div.compose.collection.CollectionItem
import com.yandex.div.compose.collection.CollectionItemScopeStorage
import com.yandex.div.compose.context.DivLocalComponentStorage
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.compose.state.DivStateStorage
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.DivBlockLocator
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ComposeDivBlockSearchEnvironmentTest {
    private val resolver = mock<DivComposeExpressionResolver>()
    private val component = mock<DivLocalComponent> {
        on { expressionResolver } doReturn resolver
    }
    private val localComponents = DivLocalComponentStorage()
    private val collectionItems = mock<CollectionItemScopeStorage>()
    private val stateStorage = DivStateStorage(data = mock(), viewContextStorage = mock())

    @Test
    fun `active state paths distinguish repeated collection items by their tree position`() {
        // Arrange
        val environment = environmentWithRepeatedStateItems()
        val root = requireNotNull(environment.rootBlock())

        // Act
        environment.findDescendants(root)

        // Assert
        val paths = environment.activeStatesPaths().map { it.fullPath }.toSet()
        assertEquals(setOf("0/item#0/switch#0/active", "0/item#1/switch#0/active"), paths)
    }

    @Test
    fun `integer state variable leaves the default branch active`() {
        // Arrange
        whenever(resolver.getVariable("selected")).thenReturn(Variable.IntegerVariable("selected", 1L))
        val defaultTarget = text(id = "target", text = "Visible")
        val locator = DivBlockLocator(environment(stateWithDefaultTarget(defaultTarget)))

        // Act
        val result = locator.findTarget("target", null, resolver, { true })

        // Assert
        assertSame(defaultTarget, result.getOrThrow().div)
    }

    @Test
    fun `anonymous builder item selects nearest target despite a matching index in another branch`() {
        // Arrange
        val itemResolver = mock<DivComposeExpressionResolver>()
        val nearest = text(id = "target", text = "Near")
        val locator = DivBlockLocator(environmentWithTargetsInSeparateBranches(nearest, itemResolver))

        // Act
        val result = locator.findTarget("target", null, itemResolver, { true })

        // Assert
        assertSame(nearest, result.getOrThrow().div)
    }

    private fun environmentWithRepeatedStateItems(): ComposeDivBlockSearchEnvironment {
        val switch = state(
            id = "switch",
            states = listOf(state(stateId = "active", div = text(text = "Target"))),
        )
        val item = container(id = "item", items = listOf(switch))
        val root = container(
            itemBuilder = itemBuilder(
                data = jsonArray(jsonObject(), jsonObject()),
                prototypes = listOf(prototype(item)),
            ),
        )
        val repeatedItems = List(2) { collectionItem(item, component) }
        whenever(collectionItems.getItems(root.value(), component)).thenReturn(repeatedItems)
        return environment(root)
    }

    private fun stateWithDefaultTarget(defaultTarget: Div) = state(
        id = "switch",
        defaultStateId = constant("visible"),
        stateIdVariable = "selected",
        states = listOf(
            state(stateId = "1", div = text(id = "target", text = "Hidden")),
            state(stateId = "visible", div = defaultTarget),
        ),
    )

    private fun environmentWithTargetsInSeparateBranches(
        nearest: Div,
        itemResolver: DivComposeExpressionResolver,
    ): ComposeDivBlockSearchEnvironment {
        val source = text(text = "Source")
        val collection = container(
            itemBuilder = itemBuilder(
                data = jsonArray(jsonObject()),
                prototypes = listOf(prototype(source)),
            ),
        )
        val itemComponent = mock<DivLocalComponent> { on { expressionResolver } doReturn itemResolver }
        val item = collectionItem(source, itemComponent)
        whenever(collectionItems.getItems(collection.value(), component)).thenReturn(listOf(item))

        val foreign = text(id = "target", text = "Far")
        val nearBranch = container(id = "A", items = listOf(collection, nearest))
        val farBranch = container(id = "B", items = listOf(container(items = listOf(foreign))))
        return environment(container(items = listOf(nearBranch, farBranch)))
    }

    private fun environment(root: Div) = ComposeDivBlockSearchEnvironment(
        state = data(root).states.single(),
        rootComponent = lazyOf(component),
        localComponents = localComponents,
        collectionItems = collectionItems,
        stateStorage = stateStorage,
        tooltipStateStorage = mock(),
    )

    private fun collectionItem(item: Div, itemComponent: DivLocalComponent) = mock<CollectionItem> {
        on { div } doReturn item
        on { component } doReturn itemComponent
    }
}
