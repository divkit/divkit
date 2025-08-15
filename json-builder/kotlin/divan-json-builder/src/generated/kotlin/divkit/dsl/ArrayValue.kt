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
 * Can be created using the method [arrayValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
data class ArrayValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TypedValue {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "array")
    )

    operator fun plus(additive: Properties): ArrayValue = ArrayValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
        val value: Property<List<Any>>?,
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
fun DivScope.arrayValue(
    `use named arguments`: Guard = Guard.instance,
    value: List<Any>? = null,
): ArrayValue = ArrayValue(
    ArrayValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.arrayValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: List<Any>? = null,
) = ArrayValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.arrayValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<List<Any>>? = null,
) = ArrayValue.Properties(
    value = value,
)

@Generated
fun ArrayValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: List<Any>? = null,
): ArrayValue = ArrayValue(
    ArrayValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun ArrayValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<List<Any>>? = null,
): ArrayValue = ArrayValue(
    ArrayValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ArrayValue.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<List<Any>>? = null,
): ArrayValue = ArrayValue(
    ArrayValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ArrayValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<List<Any>>? = null,
): ArrayValue = ArrayValue(
    ArrayValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ArrayValue.asList() = listOf(this)
