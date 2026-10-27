@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Animations.
 * 
 * Can be created using the method [changeSetTransition].
 * 
 * Required parameters: `type, items`.
 */
@Generated
@JsonSerialize(using = ChangeSetTransitionSerializer::class)
@ExposedCopyVisibility
data class ChangeSetTransition internal constructor(
    val properties: Properties,
) : ChangeTransition {
    operator fun plus(additive: Properties): ChangeSetTransition = ChangeSetTransition(
        Properties(
            items = additive.items ?: properties.items,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * List of animations.
         */
        val items: Property<List<ChangeTransition>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("items", items)
            return result
        }
    }
}

/**
 * @param items List of animations.
 */
@Generated
fun DivScope.changeSetTransition(
    `use named arguments`: Guard = Guard.instance,
    items: List<ChangeTransition>? = null,
): ChangeSetTransition = ChangeSetTransition(
    ChangeSetTransition.Properties(
        items = valueOrNull(items),
    )
)

/**
 * @param items List of animations.
 */
@Generated
fun DivScope.changeSetTransitionProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<ChangeTransition>? = null,
) = ChangeSetTransition.Properties(
    items = valueOrNull(items),
)

/**
 * @param items List of animations.
 */
@Generated
fun TemplateScope.changeSetTransitionRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<ChangeTransition>>? = null,
) = ChangeSetTransition.Properties(
    items = items,
)

/**
 * @param items List of animations.
 */
@Generated
fun ChangeSetTransition.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<ChangeTransition>? = null,
): ChangeSetTransition = ChangeSetTransition(
    ChangeSetTransition.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

/**
 * @param items List of animations.
 */
@Generated
fun ChangeSetTransition.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<ChangeTransition>>? = null,
): ChangeSetTransition = ChangeSetTransition(
    ChangeSetTransition.Properties(
        items = items ?: properties.items,
    )
)

/**
 * @param items List of animations.
 */
@Generated
fun ChangeSetTransition.modify(
    `use named arguments`: Guard = Guard.instance,
    items: Property<List<ChangeTransition>>? = null,
): ChangeSetTransition = ChangeSetTransition(
    ChangeSetTransition.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun ChangeSetTransition.asList() = listOf(this)
