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
 * Specifies the position measured in `dp` from the container start as the scrolling end position. Only applies in `gallery`.
 * 
 * Can be created using the method [offsetDestination].
 * 
 * Required parameters: `value, type`.
 */
@Generated
data class OffsetDestination internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionScrollDestination {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "offset")
    )

    operator fun plus(additive: Properties): OffsetDestination = OffsetDestination(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
        /**
         * Position measured in `dp`.
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
 * @param value Position measured in `dp`.
 */
@Generated
fun DivScope.offsetDestination(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
): OffsetDestination = OffsetDestination(
    OffsetDestination.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Position measured in `dp`.
 */
@Generated
fun DivScope.offsetDestinationProps(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
) = OffsetDestination.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Position measured in `dp`.
 */
@Generated
fun TemplateScope.offsetDestinationRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
) = OffsetDestination.Properties(
    value = value,
)

/**
 * @param value Position measured in `dp`.
 */
@Generated
fun OffsetDestination.override(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
): OffsetDestination = OffsetDestination(
    OffsetDestination.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Position measured in `dp`.
 */
@Generated
fun OffsetDestination.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
): OffsetDestination = OffsetDestination(
    OffsetDestination.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Position measured in `dp`.
 */
@Generated
fun OffsetDestination.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Int>? = null,
): OffsetDestination = OffsetDestination(
    OffsetDestination.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Position measured in `dp`.
 */
@Generated
fun OffsetDestination.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Int>? = null,
): OffsetDestination = OffsetDestination(
    OffsetDestination.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun OffsetDestination.asList() = listOf(this)
