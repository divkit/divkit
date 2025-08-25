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
 * Can be created using the method [booleanValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@ExposedCopyVisibility
data class BooleanValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TypedValue {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "boolean")
    )

    operator fun plus(additive: Properties): BooleanValue = BooleanValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val value: Property<Boolean>?,
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
fun DivScope.booleanValue(
    `use named arguments`: Guard = Guard.instance,
    value: Boolean? = null,
): BooleanValue = BooleanValue(
    BooleanValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.booleanValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: Boolean? = null,
) = BooleanValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.booleanValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Boolean>? = null,
) = BooleanValue.Properties(
    value = value,
)

@Generated
fun BooleanValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: Boolean? = null,
): BooleanValue = BooleanValue(
    BooleanValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun BooleanValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Boolean>? = null,
): BooleanValue = BooleanValue(
    BooleanValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun BooleanValue.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Boolean>? = null,
): BooleanValue = BooleanValue(
    BooleanValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun BooleanValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Boolean>? = null,
): BooleanValue = BooleanValue(
    BooleanValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun BooleanValue.asList() = listOf(this)
