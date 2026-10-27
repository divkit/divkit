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
 * Can be created using the method [dictValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = DictValueSerializer::class)
@ExposedCopyVisibility
data class DictValue internal constructor(
    val properties: Properties,
) : TypedValue {
    operator fun plus(additive: Properties): DictValue = DictValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val value: Property<Map<String, Any>>?,
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
fun DivScope.dictValue(
    `use named arguments`: Guard = Guard.instance,
    value: Map<String, Any>? = null,
): DictValue = DictValue(
    DictValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.dictValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: Map<String, Any>? = null,
) = DictValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.dictValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Map<String, Any>>? = null,
) = DictValue.Properties(
    value = value,
)

@Generated
fun DictValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: Map<String, Any>? = null,
): DictValue = DictValue(
    DictValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun DictValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Map<String, Any>>? = null,
): DictValue = DictValue(
    DictValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun DictValue.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Map<String, Any>>? = null,
): DictValue = DictValue(
    DictValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun DictValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Map<String, Any>>? = null,
): DictValue = DictValue(
    DictValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun DictValue.asList() = listOf(this)
