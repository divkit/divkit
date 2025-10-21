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
 * Can be created using the method [contentUrl].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@ExposedCopyVisibility
data class ContentUrl internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionCopyToClipboardContent {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "url")
    )

    operator fun plus(additive: Properties): ContentUrl = ContentUrl(
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
fun DivScope.contentUrl(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
): ContentUrl = ContentUrl(
    ContentUrl.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.contentUrlProps(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
) = ContentUrl.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.contentUrlRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Url>? = null,
) = ContentUrl.Properties(
    value = value,
)

@Generated
fun ContentUrl.override(
    `use named arguments`: Guard = Guard.instance,
    value: Url? = null,
): ContentUrl = ContentUrl(
    ContentUrl.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun ContentUrl.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Url>? = null,
): ContentUrl = ContentUrl(
    ContentUrl.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ContentUrl.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Url>? = null,
): ContentUrl = ContentUrl(
    ContentUrl.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ContentUrl.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Url>? = null,
): ContentUrl = ContentUrl(
    ContentUrl.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ContentUrl.asList() = listOf(this)
