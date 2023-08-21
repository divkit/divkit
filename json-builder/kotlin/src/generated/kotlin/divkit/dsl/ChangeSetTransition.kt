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
 * Animations.
 * 
 * Can be created using the method [changeSetTransition].
 * 
 * Required parameters: `type, items`.
 */
@Generated
class ChangeSetTransition internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ChangeTransition {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "set")
    )

    operator fun plus(additive: Properties): ChangeSetTransition = ChangeSetTransition(
        Properties(
            items = additive.items ?: properties.items,
        )
    )

    class Properties internal constructor(
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

@Generated
fun ChangeSetTransition.asList() = listOf(this)
