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
 * Can be created using the method [urlValue].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = UrlValueSerializer::class)
@ExposedCopyVisibility
data class UrlValue internal constructor(
    val properties: Properties,
) : TypedValue {
    operator fun plus(additive: Properties): UrlValue = UrlValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val value: Property<Url>?,
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
fun DivScope.urlValue(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
): UrlValue = UrlValue(
    UrlValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.urlValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
) = UrlValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.urlValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Url>? = null,
) = UrlValue.Properties(
    value = value,
)

@Generated
fun UrlValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
): UrlValue = UrlValue(
    UrlValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun UrlValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Url>? = null,
): UrlValue = UrlValue(
    UrlValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun UrlValue.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Url>? = null,
): UrlValue = UrlValue(
    UrlValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun UrlValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Url>? = null,
): UrlValue = UrlValue(
    UrlValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun UrlValue.asList() = listOf(this)
