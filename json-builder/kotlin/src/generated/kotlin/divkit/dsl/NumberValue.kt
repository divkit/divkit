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
 * Can be created using the method [numberValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
data class NumberValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TypedValue {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "number")
    )

    operator fun plus(additive: Properties): NumberValue = NumberValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

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
