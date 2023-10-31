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
 * Can be created using the method [colorValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
class ColorValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TypedValue {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "color")
    )

    operator fun plus(additive: Properties): ColorValue = ColorValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    class Properties internal constructor(
        val value: Property<Color>?,
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
fun DivScope.colorValue(
    `use named arguments`: Guard = Guard.instance,
    value: Color? = null,
): ColorValue = ColorValue(
    ColorValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.colorValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: Color? = null,
) = ColorValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.colorValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Color>? = null,
) = ColorValue.Properties(
    value = value,
)

@Generated
fun ColorValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: Color? = null,
): ColorValue = ColorValue(
    ColorValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun ColorValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Color>? = null,
): ColorValue = ColorValue(
    ColorValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ColorValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Color>? = null,
): ColorValue = ColorValue(
    ColorValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ColorValue.asList() = listOf(this)
