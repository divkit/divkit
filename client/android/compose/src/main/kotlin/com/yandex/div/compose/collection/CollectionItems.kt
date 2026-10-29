package com.yandex.div.compose.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.declaresLocalScope
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.triggers.observe
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.reportWarning
import com.yandex.div.internal.core.INDEX_VARIABLE_NAME
import com.yandex.div.json.typeMismatch
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivVisibility
import org.json.JSONObject

private const val INDEX_NAME_RESERVED_MESSAGE =
    "Item builder data element name 'index' is reserved for the item index"

internal class CollectionItems(
    val items: List<Div>,
    val components: Map<Div, DivLocalComponent>?,
    val triggerComponents: List<DivLocalComponent>,
)

@Composable
internal fun DivContainer.observeItems(): CollectionItems =
    observeCollectionItems(this, items, itemBuilder)

/**
 * The children of [owner]: the items its [itemBuilder] builds, or the static [items] otherwise,
 * each with the scope it is rendered in.
 */
@Composable
private fun observeCollectionItems(
    owner: DivBase,
    items: List<Div>?,
    itemBuilder: DivCollectionItemBuilder?,
): CollectionItems {
    if (itemBuilder == null) {
        val staticItems = items.orEmpty()
        if (staticItems.none { it.value().declaresLocalScope }) {
            return CollectionItems(staticItems, components = null, triggerComponents = emptyList())
        }
    }
    val component = LocalComponent.current
    val collectionItems = if (itemBuilder == null) {
        val viewContext = LocalDivViewContext.current
        viewContext.collectionItemScopeStorage
            .getOrCreateStaticItems(owner, items.orEmpty(), component, viewContext)
    } else {
        itemBuilder.observeBuiltItems(owner, component)
    }
    collectionItems.triggerComponents.forEach { triggerComponent ->
        key(triggerComponent) {
            triggerComponent.triggerStorage.observe()
        }
    }
    return collectionItems
}

/**
 * Filters out the items that are `gone`. The visibility of an item is resolved in the scope of the
 * item itself — the same scope [com.yandex.div.compose.views.DivBlockView] renders it in — so
 * that an expression over the local variables of the item, or over the data of an `item_builder`
 * element, is read here exactly as it is read on render.
 */
@Composable
internal inline fun CollectionItems.observeVisible(): List<Div> {
    return if (components == null) {
        items.filter { it.value().visibility.observedValue() != DivVisibility.GONE }
    } else {
        val component = LocalComponent.current
        items.filter {
            val itemComponent = components[it] ?: component
            it.value().visibility.observedValue(component = itemComponent) != DivVisibility.GONE
        }
    }
}

/**
 * Mirrors the View renderer's `DivCollectionItemBuilder.build`: every element of `data` gets a
 * scope with the element and its index exposed as constants, the first prototype whose
 * `selector` is true is selected, elements without a matching prototype and elements that are
 * not dictionaries (reported) are skipped.
 */
@Composable
private fun DivCollectionItemBuilder.observeBuiltItems(
    owner: DivBase,
    parent: DivLocalComponent,
): CollectionItems {
    if (isDataElementShadowedByIndex) {
        reportWarning(INDEX_NAME_RESERVED_MESSAGE)
    }
    val elements = data.observedValue()
    val elementKeys = remember(elements) {
        List(elements.length()) { index -> "${elements.get(index)}:$index" }
    }
    val selectedItems = ArrayList<SelectedCollectionItem>(elements.length())
    for (index in 0 until elements.length()) {
        val elementKey = elementKeys[index]
        val item = key(elementKey) {
            observeSelectedItem(parent, elements.get(index), index, elementKey)
        }
        if (item != null) {
            selectedItems += item
        }
    }
    val viewContext = LocalDivViewContext.current
    val idCounts = selectedItems.mapNotNull { it.id }.groupingBy { it }.eachCount()
    val items = selectedItems.mapIndexed { index, item ->
        viewContext.collectionItemScopeStorage.getOrCreateItem(
            owner = owner,
            parent = parent,
            item = item,
            duplicateIndex = index.takeIf { (idCounts[item.id] ?: 0) > 1 },
            viewContext = viewContext,
        )
    }
    SideEffect {
        val removedDivs =
            viewContext.collectionItemScopeStorage.retainItems(owner, parent, items)
        if (removedDivs.isNotEmpty()) viewContext.releaseComponents(removedDivs)
        items.forEach { it.notifyChanges() }
    }
    return remember(items) {
        CollectionItems(
            items = items.map { it.div },
            components = items.associate { it.div to it.component },
            triggerComponents = items
                .filter { !it.div.value().variableTriggers.isNullOrEmpty() }
                .map { it.component },
        )
    }
}

@Composable
private fun DivCollectionItemBuilder.observeSelectedItem(
    parent: DivLocalComponent,
    element: Any,
    index: Int,
    elementKey: String,
): SelectedCollectionItem? {
    if (element !is JSONObject) {
        reportError(remember(index, element) { typeMismatch(index, element) })
        return null
    }
    val viewContext = LocalDivViewContext.current
    val constants = remember(elementKey) { itemConstants(element, index) }
    val component = remember(viewContext, parent, elementKey) {
        viewContext.createLocalComponent(
            parent,
            ItemVariableProvider(parent.variableProvider, constants),
        )
    }
    DisposableEffect(component) {
        onDispose { component.expressionResolver.clearObservers() }
    }
    val prototype = observeSelectedPrototype(component) ?: return null
    val id = prototype.id?.observedValue(component = component)
    return remember(elementKey, prototype, id) {
        SelectedCollectionItem(prototype, id, elementKey, constants)
    }
}

@Composable
private fun DivCollectionItemBuilder.observeSelectedPrototype(
    component: DivLocalComponent
): DivCollectionItemBuilder.Prototype? {
    for (prototype in prototypes) {
        if (prototype.selector.observedValue(component = component)) {
            return prototype
        }
    }
    return null
}

/**
 * The item index shadows a data element named `index`, as in the View renderer.
 */
private val DivCollectionItemBuilder.isDataElementShadowedByIndex: Boolean
    get() = dataElementName == INDEX_VARIABLE_NAME

private fun DivCollectionItemBuilder.itemConstants(
    element: JSONObject,
    index: Int,
): Map<String, Any> = mapOf(dataElementName to element, INDEX_VARIABLE_NAME to index.toLong())

internal data class SelectedCollectionItem(
    val prototype: DivCollectionItemBuilder.Prototype,
    val id: String?,
    val elementKey: String,
    val constants: Map<String, Any>,
)
