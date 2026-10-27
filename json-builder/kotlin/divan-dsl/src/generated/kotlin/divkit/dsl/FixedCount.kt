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
 * Fixed number of repetitions.
 * 
 * Can be created using the method [fixedCount].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = FixedCountSerializer::class)
@ExposedCopyVisibility
data class FixedCount internal constructor(
    val properties: Properties,
) : Count {
    operator fun plus(additive: Properties): FixedCount = FixedCount(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Number of repetitions.
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
 * @param value Number of repetitions.
 */
@Generated
fun DivScope.fixedCount(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
): FixedCount = FixedCount(
    FixedCount.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Number of repetitions.
 */
@Generated
fun DivScope.fixedCountProps(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
) = FixedCount.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Number of repetitions.
 */
@Generated
fun TemplateScope.fixedCountRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
) = FixedCount.Properties(
    value = value,
)

/**
 * @param value Number of repetitions.
 */
@Generated
fun FixedCount.override(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
): FixedCount = FixedCount(
    FixedCount.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Number of repetitions.
 */
@Generated
fun FixedCount.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
): FixedCount = FixedCount(
    FixedCount.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Number of repetitions.
 */
@Generated
fun FixedCount.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Int>? = null,
): FixedCount = FixedCount(
    FixedCount.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Number of repetitions.
 */
@Generated
fun FixedCount.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Int>? = null,
): FixedCount = FixedCount(
    FixedCount.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun FixedCount.asList() = listOf(this)
