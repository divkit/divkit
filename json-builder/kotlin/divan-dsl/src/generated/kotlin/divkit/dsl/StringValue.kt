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
 * Can be created using the method [stringValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = StringValueSerializer::class)
@ExposedCopyVisibility
data class StringValue internal constructor(
    val properties: Properties,
) : TypedValue {
    operator fun plus(additive: Properties): StringValue = StringValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val value: Property<String>?,
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
fun DivScope.stringValue(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): StringValue = StringValue(
    StringValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.stringValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
) = StringValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.stringValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
) = StringValue.Properties(
    value = value,
)

@Generated
fun StringValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): StringValue = StringValue(
    StringValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun StringValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
): StringValue = StringValue(
    StringValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun StringValue.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<String>? = null,
): StringValue = StringValue(
    StringValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun StringValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<String>? = null,
): StringValue = StringValue(
    StringValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun StringValue.asList() = listOf(this)
