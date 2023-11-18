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
class Patch internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Patch = Patch(
        Properties(
            changes = additive.changes ?: properties.changes,
            mode = additive.mode ?: properties.mode,
        )
    )

    class Properties internal constructor(
        /**
         * Element changes.
         */
        val changes: Property<List<Change>>?,
        /**
         * Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
         * Default value: `partial`.
         */
        val mode: Property<Mode>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("changes", changes)
            result.tryPutProperty("mode", mode)
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
    class Change internal constructor(
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

        class Properties internal constructor(
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
 */
@Generated
fun DivScope.patch(
    `use named arguments`: Guard = Guard.instance,
    changes: List<Patch.Change>? = null,
    mode: Patch.Mode? = null,
): Patch = Patch(
    Patch.Properties(
        changes = valueOrNull(changes),
        mode = valueOrNull(mode),
    )
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 */
@Generated
fun DivScope.patchProps(
    `use named arguments`: Guard = Guard.instance,
    changes: List<Patch.Change>? = null,
    mode: Patch.Mode? = null,
) = Patch.Properties(
    changes = valueOrNull(changes),
    mode = valueOrNull(mode),
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 */
@Generated
fun TemplateScope.patchRefs(
    `use named arguments`: Guard = Guard.instance,
    changes: ReferenceProperty<List<Patch.Change>>? = null,
    mode: ReferenceProperty<Patch.Mode>? = null,
) = Patch.Properties(
    changes = changes,
    mode = mode,
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 */
@Generated
fun Patch.override(
    `use named arguments`: Guard = Guard.instance,
    changes: List<Patch.Change>? = null,
    mode: Patch.Mode? = null,
): Patch = Patch(
    Patch.Properties(
        changes = valueOrNull(changes) ?: properties.changes,
        mode = valueOrNull(mode) ?: properties.mode,
    )
)

/**
 * @param changes Element changes.
 * @param mode Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>
 */
@Generated
fun Patch.defer(
    `use named arguments`: Guard = Guard.instance,
    changes: ReferenceProperty<List<Patch.Change>>? = null,
    mode: ReferenceProperty<Patch.Mode>? = null,
): Patch = Patch(
    Patch.Properties(
        changes = changes ?: properties.changes,
        mode = mode ?: properties.mode,
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

@Generated
fun Patch.Change.asList() = listOf(this)

@Generated
fun Patch.Mode.asList() = listOf(this)
