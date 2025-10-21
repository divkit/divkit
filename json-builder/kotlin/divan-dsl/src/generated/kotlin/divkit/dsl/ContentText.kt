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
 * Can be created using the method [contentText].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@ExposedCopyVisibility
data class ContentText internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionCopyToClipboardContent {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "text")
    )

    operator fun plus(additive: Properties): ContentText = ContentText(
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
fun DivScope.contentText(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): ContentText = ContentText(
    ContentText.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.contentTextProps(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
) = ContentText.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.contentTextRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
) = ContentText.Properties(
    value = value,
)

@Generated
fun ContentText.override(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): ContentText = ContentText(
    ContentText.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun ContentText.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
): ContentText = ContentText(
    ContentText.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ContentText.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<String>? = null,
): ContentText = ContentText(
    ContentText.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ContentText.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<String>? = null,
): ContentText = ContentText(
    ContentText.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ContentText.asList() = listOf(this)
