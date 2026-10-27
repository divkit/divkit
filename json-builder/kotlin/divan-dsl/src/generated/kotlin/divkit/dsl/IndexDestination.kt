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
 * Specifies the element with the given index as the scrolling end position.
 * 
 * Can be created using the method [indexDestination].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = IndexDestinationSerializer::class)
@ExposedCopyVisibility
data class IndexDestination internal constructor(
    val properties: Properties,
) : ActionScrollDestination {
    operator fun plus(additive: Properties): IndexDestination = IndexDestination(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Container element index.
         */
        val value: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param value Container element index.
 */
@Generated
fun DivScope.indexDestination(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
): IndexDestination = IndexDestination(
    IndexDestination.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Container element index.
 */
@Generated
fun DivScope.indexDestinationProps(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
) = IndexDestination.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Container element index.
 */
@Generated
fun TemplateScope.indexDestinationRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
) = IndexDestination.Properties(
    value = value,
)

/**
 * @param value Container element index.
 */
@Generated
fun IndexDestination.override(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
): IndexDestination = IndexDestination(
    IndexDestination.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Container element index.
 */
@Generated
fun IndexDestination.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
): IndexDestination = IndexDestination(
    IndexDestination.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Container element index.
 */
@Generated
fun IndexDestination.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Int>? = null,
): IndexDestination = IndexDestination(
    IndexDestination.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Container element index.
 */
@Generated
fun IndexDestination.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Int>? = null,
): IndexDestination = IndexDestination(
    IndexDestination.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun IndexDestination.asList() = listOf(this)
