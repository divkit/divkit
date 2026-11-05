package com.yandex.div.compose.collection

import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.DivViewScope
import com.yandex.div.compose.patch.collectDivs
import com.yandex.div.internal.core.copy
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivCollectionItemBuilder
import dev.zacsweers.metro.Inject

/**
 * Keeps the item scopes of a collection owner — an element rendered through `observeItems()` —
 * while the owner belongs to the card. As in the View renderer, a resolved prototype id preserves
 * the item scope within a prototype; anonymous items use their data and index instead. Scopes
 * survive leaving and re-entering composition, and are released when data or a patch removes them.
 */
@DivViewScope
internal class CollectionItemScopeStorage @Inject constructor() {
    private val builders = HashMap<DivBase, MutableMap<DivLocalComponent, BuilderScopes>>()
    private val staticItems = HashMap<DivLocalComponent, MutableMap<DivBase, CollectionItems>>()

    fun getOrCreateStaticItems(
        owner: DivBase,
        items: List<Div>,
        parent: DivLocalComponent,
        viewContext: DivViewContext,
    ): CollectionItems {
        val parentItems = staticItems.getOrPut(parent) { HashMap() }
        return parentItems.getOrPut(owner) {
            var components: MutableMap<Div, DivLocalComponent>? = null
            var triggerComponents: MutableList<DivLocalComponent>? = null
            for (div in items) {
                val component = viewContext.getOwnComponent(div.value(), parent)
                if (component === parent) continue

                if (components == null) components = HashMap()
                components[div] = component
                if (!div.value().variableTriggers.isNullOrEmpty()) {
                    if (triggerComponents == null) triggerComponents = ArrayList()
                    triggerComponents.add(component)
                }
            }
            CollectionItems(
                items = items,
                components = components,
                triggerComponents = triggerComponents.orEmpty(),
            )
        }
    }

    fun getOrCreateItem(
        owner: DivBase,
        parent: DivLocalComponent,
        item: SelectedCollectionItem,
        duplicateIndex: Int?,
        viewContext: DivViewContext,
    ): CollectionItem {
        // Keep View's duplicate-item index separate from the raw id to avoid suffix collisions.
        val key = ItemKey(
            item.prototype,
            item.id,
            duplicateIndex,
            item.elementKey.takeIf { item.id == null },
        )
        val scopes = builderScopes(owner, parent)
        val scope = scopes.items.getOrPut(key) {
            val variables = ItemVariableProvider(parent.variableProvider, item.constants)
            val component = viewContext.createLocalComponent(parent, variables)
            val prototypeDiv = item.prototype.div
            val div = prototypeDiv.copy(item.id ?: prototypeDiv.value().id)
            val ownComponent = viewContext.getOwnComponent(div.value(), component)
            CollectionItem(
                div = div,
                component = ownComponent,
                itemComponent = component,
                triggerComponents = scopes.triggerComponents(
                    item.prototype,
                    div,
                    ownComponent,
                    viewContext,
                ),
                variables = variables,
                elementKey = item.elementKey,
            )
        }
        scope.update(item)
        return scope
    }

    fun retainItems(
        owner: DivBase,
        parent: DivLocalComponent,
        items: List<CollectionItem>,
    ): Set<DivBase> {
        val scopes = builders[owner]?.get(parent) ?: return emptySet()
        val retained = items.toSet()
        val removed = scopes.items.values.filter { it !in retained }
        if (removed.isEmpty()) return emptySet()
        scopes.items.values.removeAll(removed.toSet())
        return releaseItems(removed)
    }

    /**
     * Releases [items] with the items that the owners inside them have built.
     */
    private fun releaseItems(items: List<CollectionItem>): Set<DivBase> {
        val pending = ArrayDeque(items)
        val divs = mutableSetOf<DivBase>()
        while (pending.isNotEmpty()) {
            val scope = pending.removeFirst()
            scope.clearObservers()
            val children = mutableSetOf<DivBase>()
            scope.div.collectDivs(children)
            for (child in children) {
                if (!divs.add(child)) continue
                builders.remove(child)?.values?.forEach { pending.addAll(it.items.values) }
            }
        }
        staticItems.values.forEach { it.keys.removeAll(divs) }
        staticItems.values.removeAll { it.isEmpty() }
        return divs
    }

    fun alias(original: DivBase, updated: DivBase) {
        builders[original]?.let { builders[updated] = it }
    }

    /**
     * Keeps the builders of the owners in [divs] and adds the divs they generated to [divs],
     * so that the other storages keep them as well; releases the builders of other owners.
     */
    fun retain(divs: MutableSet<DivBase>) {
        val owners = ArrayDeque(builders.keys.filter { it in divs })
        while (owners.isNotEmpty()) {
            val owner = owners.removeFirst()
            val generatedDivs = mutableSetOf<DivBase>()
            builders.getValue(owner).values.forEach { scopes ->
                scopes.items.values.forEach { it.div.collectDivs(generatedDivs) }
            }
            generatedDivs.forEach { div ->
                if (divs.add(div) && div in builders) {
                    owners.addLast(div)
                }
            }
        }
        val removed = builders.values.toMutableSet()
        builders.keys.retainAll(divs)
        removed.removeAll(builders.values.toSet())
        removed.forEach { parents ->
            parents.values.forEach { scopes ->
                scopes.items.values.forEach { it.clearObservers() }
            }
        }
        staticItems.values.forEach { it.keys.retainAll(divs) }
        staticItems.values.removeAll { it.isEmpty() }
    }

    private fun builderScopes(owner: DivBase, parent: DivLocalComponent): BuilderScopes =
        builders.getOrPut(owner) { HashMap() }.getOrPut(parent) { BuilderScopes() }

    /**
     * The scopes that declare triggers among [div], built from [prototype], and its descendants.
     * The scopes of the descendants are created here only when [prototype] declares triggers below
     * its root.
     */
    private fun BuilderScopes.triggerComponents(
        prototype: DivCollectionItemBuilder.Prototype,
        div: Div,
        component: DivLocalComponent,
        viewContext: DivViewContext,
    ): List<DivLocalComponent> = when {
        nestedTriggers.getOrPut(prototype) { prototype.div.hasNestedTriggers() } ->
            buildList { viewContext.collectTriggerComponents(div, component, this) }
        div.value().variableTriggers.isNullOrEmpty() -> emptyList()
        else -> listOf(component)
    }

    private data class ItemKey(
        val prototype: DivCollectionItemBuilder.Prototype,
        val id: String?,
        val duplicateIndex: Int?,
        val elementKey: String?,
    )

    private class BuilderScopes {
        val items = HashMap<ItemKey, CollectionItem>()
        val nestedTriggers = HashMap<DivCollectionItemBuilder.Prototype, Boolean>()
    }
}

/**
 * A generated child and its retained item bindings, with a separate scope for its own
 * declarations. [triggerComponents] are the scopes of the child and of its descendants that
 * declare triggers.
 */
internal class CollectionItem(
    val div: Div,
    val component: DivLocalComponent,
    private val itemComponent: DivLocalComponent,
    val triggerComponents: List<DivLocalComponent>,
    private val variables: ItemVariableProvider,
    private var elementKey: String,
) {
    fun notifyChanges() {
        variables.notifyChanges()
    }

    fun clearObservers() {
        itemComponent.expressionResolver.clearObservers()
    }

    fun update(item: SelectedCollectionItem) {
        if (elementKey != item.elementKey) {
            elementKey = item.elementKey
            variables.update(item.constants)
        }
    }
}

/**
 * Adds the scopes that declare triggers among [div] and the descendants reached through
 * [persistentChildren], creating the scopes of those descendants.
 */
private fun DivViewContext.collectTriggerComponents(
    div: Div,
    component: DivLocalComponent,
    result: MutableList<DivLocalComponent>,
) {
    if (!div.value().variableTriggers.isNullOrEmpty()) result += component
    div.persistentChildren().forEach { child ->
        collectTriggerComponents(child, getOwnComponent(child.value(), component), result)
    }
}

/**
 * Whether a descendant reached through [persistentChildren] declares triggers.
 */
private fun Div.hasNestedTriggers(): Boolean = persistentChildren().any { child ->
    !child.value().variableTriggers.isNullOrEmpty() || child.hasNestedTriggers()
}

/**
 * The static items of a collection owner without `item_builder`; other elements have none.
 */
private fun Div.persistentChildren(): List<Div> = when (this) {
    is Div.Container -> value.items.takeIf { value.itemBuilder == null }.orEmpty()
    is Div.Gallery -> value.items.takeIf { value.itemBuilder == null }.orEmpty()
    is Div.Grid -> value.items.orEmpty()
    is Div.Pager -> value.items.takeIf { value.itemBuilder == null }.orEmpty()
    is Div.Custom, is Div.GifImage, is Div.Image, is Div.Indicator, is Div.Input,
    is Div.Select, is Div.Separator, is Div.Slider, is Div.State, is Div.Switch,
    is Div.Tabs, is Div.Text, is Div.Video -> emptyList()
}
