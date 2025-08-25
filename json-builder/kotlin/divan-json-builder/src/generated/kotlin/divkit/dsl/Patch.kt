@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Edits the element.
 * 
 * Can be created using the method [patch].
 * 
 * Required parameters: `changes`.
 */
@Generated
@ExposedCopyVisibility
data class Patch internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Element changes.
         */
        val changes: Property<List<Change>>?,
        /**
         * Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
         * Default value: `partial`.
         */
        val mode: Property<Mode>?,
        /**
         * Actions to perform after changes are applied.
         */
        val onAppliedActions: Property<List<Action>>?,
        /**
         * Actions to perform if there’s an error when applying changes in transaction mode.
         */
        val onFailedActions: Property<List<Action>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("changes", changes)
            result.tryPutProperty("mode", mode)
            result.tryPutProperty("on_applied_actions", onAppliedActions)
            result.tryPutProperty("on_failed_actions", onFailedActions)
            return result
        }
    }

    /**
     * Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
     * 
     * Possible values: [transactional], [partial].
     */
    @Generated
    sealed interface Mode

    /**
     * Can be created using the method [patchChange].
     * 
     * Required parameters: `id`.
     */
    @Generated
    @ExposedCopyVisibility
    data class Change internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Change = Change(
            Properties(
                id = additive.id ?: properties.id,
                items = additive.items ?: properties.items,
            )
        )

        @ExposedCopyVisibility
        data class Properties internal constructor(
            /**
             * ID of an element to be replaced or removed.
             */
            val id: Property<String>?,
            /**
             * Elements to be inserted. If the parameter isn't specified, the element will be removed.
             */
            val items: Property<List<Div>>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("id", id)
                result.tryPutProperty("items", items)
                return result
            }
        }
    }

}

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 * @param onAppliedActions Actions to perform after changes are applied.
 * @param onFailedActions Actions to perform if there’s an error when applying changes in transaction mode.
 */
@Generated
fun DivScope.patch(
    `use named arguments`: Guard = Guard.instance,
    changes: List<Patch.Change>,
    mode: Patch.Mode? = null,
    onAppliedActions: List<Action>? = null,
    onFailedActions: List<Action>? = null,
): Patch = Patch(
    Patch.Properties(
        changes = valueOrNull(changes),
        mode = valueOrNull(mode),
        onAppliedActions = valueOrNull(onAppliedActions),
        onFailedActions = valueOrNull(onFailedActions),
    )
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 * @param onAppliedActions Actions to perform after changes are applied.
 * @param onFailedActions Actions to perform if there’s an error when applying changes in transaction mode.
 */
@Generated
fun DivScope.patchProps(
    `use named arguments`: Guard = Guard.instance,
    changes: List<Patch.Change>? = null,
    mode: Patch.Mode? = null,
    onAppliedActions: List<Action>? = null,
    onFailedActions: List<Action>? = null,
) = Patch.Properties(
    changes = valueOrNull(changes),
    mode = valueOrNull(mode),
    onAppliedActions = valueOrNull(onAppliedActions),
    onFailedActions = valueOrNull(onFailedActions),
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 * @param onAppliedActions Actions to perform after changes are applied.
 * @param onFailedActions Actions to perform if there’s an error when applying changes in transaction mode.
 */
@Generated
fun TemplateScope.patchRefs(
    `use named arguments`: Guard = Guard.instance,
    changes: ReferenceProperty<List<Patch.Change>>? = null,
    mode: ReferenceProperty<Patch.Mode>? = null,
    onAppliedActions: ReferenceProperty<List<Action>>? = null,
    onFailedActions: ReferenceProperty<List<Action>>? = null,
) = Patch.Properties(
    changes = changes,
    mode = mode,
    onAppliedActions = onAppliedActions,
    onFailedActions = onFailedActions,
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 * @param onAppliedActions Actions to perform after changes are applied.
 * @param onFailedActions Actions to perform if there’s an error when applying changes in transaction mode.
 */
@Generated
fun Patch.override(
    `use named arguments`: Guard = Guard.instance,
    changes: List<Patch.Change>? = null,
    mode: Patch.Mode? = null,
    onAppliedActions: List<Action>? = null,
    onFailedActions: List<Action>? = null,
): Patch = Patch(
    Patch.Properties(
        changes = valueOrNull(changes) ?: properties.changes,
        mode = valueOrNull(mode) ?: properties.mode,
        onAppliedActions = valueOrNull(onAppliedActions) ?: properties.onAppliedActions,
        onFailedActions = valueOrNull(onFailedActions) ?: properties.onFailedActions,
    )
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 * @param onAppliedActions Actions to perform after changes are applied.
 * @param onFailedActions Actions to perform if there’s an error when applying changes in transaction mode.
 */
@Generated
fun Patch.defer(
    `use named arguments`: Guard = Guard.instance,
    changes: ReferenceProperty<List<Patch.Change>>? = null,
    mode: ReferenceProperty<Patch.Mode>? = null,
    onAppliedActions: ReferenceProperty<List<Action>>? = null,
    onFailedActions: ReferenceProperty<List<Action>>? = null,
): Patch = Patch(
    Patch.Properties(
        changes = changes ?: properties.changes,
        mode = mode ?: properties.mode,
        onAppliedActions = onAppliedActions ?: properties.onAppliedActions,
        onFailedActions = onFailedActions ?: properties.onFailedActions,
    )
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 * @param onAppliedActions Actions to perform after changes are applied.
 * @param onFailedActions Actions to perform if there’s an error when applying changes in transaction mode.
 */
@Generated
fun Patch.modify(
    `use named arguments`: Guard = Guard.instance,
    changes: Property<List<Patch.Change>>? = null,
    mode: Property<Patch.Mode>? = null,
    onAppliedActions: Property<List<Action>>? = null,
    onFailedActions: Property<List<Action>>? = null,
): Patch = Patch(
    Patch.Properties(
        changes = changes ?: properties.changes,
        mode = mode ?: properties.mode,
        onAppliedActions = onAppliedActions ?: properties.onAppliedActions,
        onFailedActions = onFailedActions ?: properties.onFailedActions,
    )
)

/**
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 */
@Generated
fun Patch.evaluate(
    `use named arguments`: Guard = Guard.instance,
    mode: ExpressionProperty<Patch.Mode>? = null,
): Patch = Patch(
    Patch.Properties(
        changes = properties.changes,
        mode = mode ?: properties.mode,
        onAppliedActions = properties.onAppliedActions,
        onFailedActions = properties.onFailedActions,
    )
)

@Generated
fun Patch.asList() = listOf(this)

/**
 * @param id ID of an element to be replaced or removed.
 * @param items Elements to be inserted. If the parameter isn't specified, the element will be removed.
 */
@Generated
fun DivScope.patchChange(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    items: List<Div>? = null,
): Patch.Change = Patch.Change(
    Patch.Change.Properties(
        id = valueOrNull(id),
        items = valueOrNull(items),
    )
)

/**
 * @param id ID of an element to be replaced or removed.
 * @param items Elements to be inserted. If the parameter isn't specified, the element will be removed.
 */
@Generated
fun DivScope.patchChangeProps(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    items: List<Div>? = null,
) = Patch.Change.Properties(
    id = valueOrNull(id),
    items = valueOrNull(items),
)

/**
 * @param id ID of an element to be replaced or removed.
 * @param items Elements to be inserted. If the parameter isn't specified, the element will be removed.
 */
@Generated
fun TemplateScope.patchChangeRefs(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Div>>? = null,
) = Patch.Change.Properties(
    id = id,
    items = items,
)

/**
 * @param id ID of an element to be replaced or removed.
 * @param items Elements to be inserted. If the parameter isn't specified, the element will be removed.
 */
@Generated
fun Patch.Change.override(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    items: List<Div>? = null,
): Patch.Change = Patch.Change(
    Patch.Change.Properties(
        id = valueOrNull(id) ?: properties.id,
        items = valueOrNull(items) ?: properties.items,
    )
)

/**
 * @param id ID of an element to be replaced or removed.
 * @param items Elements to be inserted. If the parameter isn't specified, the element will be removed.
 */
@Generated
fun Patch.Change.defer(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    items: ReferenceProperty<List<Div>>? = null,
): Patch.Change = Patch.Change(
    Patch.Change.Properties(
        id = id ?: properties.id,
        items = items ?: properties.items,
    )
)

/**
 * @param id ID of an element to be replaced or removed.
 * @param items Elements to be inserted. If the parameter isn't specified, the element will be removed.
 */
@Generated
fun Patch.Change.modify(
    `use named arguments`: Guard = Guard.instance,
    id: Property<String>? = null,
    items: Property<List<Div>>? = null,
): Patch.Change = Patch.Change(
    Patch.Change.Properties(
        id = id ?: properties.id,
        items = items ?: properties.items,
    )
)

@Generated
fun Patch.Change.asList() = listOf(this)

@Generated
fun Patch.Mode.asList() = listOf(this)
