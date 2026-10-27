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
 * A set of animations to be applied simultaneously.
 * 
 * Can be created using the method [appearanceSetTransition].
 * 
 * Required parameters: `type, items`.
 */
@Generated
@JsonSerialize(using = AppearanceSetTransitionSerializer::class)
@ExposedCopyVisibility
data class AppearanceSetTransition internal constructor(
    val properties: Properties,
) : AppearanceTransition {
    operator fun plus(additive: Properties): AppearanceSetTransition = AppearanceSetTransition(
        Properties(
            items = additive.items ?: properties.items,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * An array of animations.
         */
        val items: Property<List<AppearanceTransition>>?,
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
 * @param items An array of animations.
 */
@Generated
fun DivScope.appearanceSetTransition(
    `use named arguments`: Guard = Guard.instance,
    items: List<AppearanceTransition>? = null,
): AppearanceSetTransition = AppearanceSetTransition(
    AppearanceSetTransition.Properties(
        items = valueOrNull(items),
    )
)

/**
 * @param items An array of animations.
 */
@Generated
fun DivScope.appearanceSetTransitionProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<AppearanceTransition>? = null,
) = AppearanceSetTransition.Properties(
    items = valueOrNull(items),
)

/**
 * @param items An array of animations.
 */
@Generated
fun TemplateScope.appearanceSetTransitionRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<AppearanceTransition>>? = null,
) = AppearanceSetTransition.Properties(
    items = items,
)

/**
 * @param items An array of animations.
 */
@Generated
fun AppearanceSetTransition.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<AppearanceTransition>? = null,
): AppearanceSetTransition = AppearanceSetTransition(
    AppearanceSetTransition.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

/**
 * @param items An array of animations.
 */
@Generated
fun AppearanceSetTransition.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<AppearanceTransition>>? = null,
): AppearanceSetTransition = AppearanceSetTransition(
    AppearanceSetTransition.Properties(
        items = items ?: properties.items,
    )
)

/**
 * @param items An array of animations.
 */
@Generated
fun AppearanceSetTransition.modify(
    `use named arguments`: Guard = Guard.instance,
    items: Property<List<AppearanceTransition>>? = null,
): AppearanceSetTransition = AppearanceSetTransition(
    AppearanceSetTransition.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun AppearanceSetTransition.asList() = listOf(this)
