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
 * Can be created using the method [numberValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = NumberValueSerializer::class)
@ExposedCopyVisibility
data class NumberValue internal constructor(
    val properties: Properties,
) : TypedValue {
    operator fun plus(additive: Properties): NumberValue = NumberValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val value: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

@Generated
fun DivScope.numberValue(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): NumberValue = NumberValue(
    NumberValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.numberValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
) = NumberValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.numberValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
) = NumberValue.Properties(
    value = value,
)

@Generated
fun NumberValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): NumberValue = NumberValue(
    NumberValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun NumberValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
): NumberValue = NumberValue(
    NumberValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun NumberValue.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Double>? = null,
): NumberValue = NumberValue(
    NumberValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun NumberValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Double>? = null,
): NumberValue = NumberValue(
    NumberValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun NumberValue.asList() = listOf(this)
