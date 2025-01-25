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
 * Can be created using the method [integerValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
data class IntegerValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TypedValue {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "integer")
    )

    operator fun plus(additive: Properties): IntegerValue = IntegerValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
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

@Generated
fun DivScope.integerValue(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
): IntegerValue = IntegerValue(
    IntegerValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.integerValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
) = IntegerValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.integerValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
) = IntegerValue.Properties(
    value = value,
)

@Generated
fun IntegerValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
): IntegerValue = IntegerValue(
    IntegerValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun IntegerValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
): IntegerValue = IntegerValue(
    IntegerValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun IntegerValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Int>? = null,
): IntegerValue = IntegerValue(
    IntegerValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun IntegerValue.asList() = listOf(this)
