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
 * Variable — URL as a string.
 * 
 * Can be created using the method [urlVariable].
 * 
 * Required parameters: `value, type, name`.
 */
@Generated
data class UrlVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Variable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "url")
    )

    operator fun plus(additive: Properties): UrlVariable = UrlVariable(
        Properties(
            name = additive.name ?: properties.name,
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
        /**
         * Variable name.
         */
        val name: Property<String>?,
        /**
         * Value. Supports expressions for variable initialization.
         */
        val value: Property<Url>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("name", name)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DivScope.urlVariable(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Url? = null,
): UrlVariable = UrlVariable(
    UrlVariable.Properties(
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DivScope.urlVariableProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Url? = null,
) = UrlVariable.Properties(
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun TemplateScope.urlVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Url>? = null,
) = UrlVariable.Properties(
    name = name,
    value = value,
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun UrlVariable.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Url? = null,
): UrlVariable = UrlVariable(
    UrlVariable.Properties(
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun UrlVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Url>? = null,
): UrlVariable = UrlVariable(
    UrlVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun UrlVariable.modify(
    `use named arguments`: Guard = Guard.instance,
    name: Property<String>? = null,
    value: Property<Url>? = null,
): UrlVariable = UrlVariable(
    UrlVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun UrlVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Url>? = null,
): UrlVariable = UrlVariable(
    UrlVariable.Properties(
        name = properties.name,
        value = value ?: properties.value,
    )
)

@Generated
fun UrlVariable.asList() = listOf(this)
