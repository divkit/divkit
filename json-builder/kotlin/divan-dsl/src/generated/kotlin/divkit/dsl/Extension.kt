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
 * Extension that affects an element.
 * 
 * Can be created using the method [extension].
 * 
 * Required parameters: `id`.
 */
@Generated
@ExposedCopyVisibility
data class Extension internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Extension = Extension(
        Properties(
            id = additive.id ?: properties.id,
            isEnabled = additive.isEnabled ?: properties.isEnabled,
            params = additive.params ?: properties.params,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Extension ID.
         */
        val id: Property<String>?,
        /**
         * Controls whether the extension is enabled (`true` by default). When `false`, the extension is not applied to the element.
         * Default value: `true`.
         */
        val isEnabled: Property<Boolean>?,
        /**
         * Additional extension parameters.
         */
        val params: Property<Map<String, Any>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("id", id)
            result.tryPutProperty("is_enabled", isEnabled)
            result.tryPutProperty("params", params)
            return result
        }
    }
}

/**
 * @param id Extension ID.
 * @param isEnabled Controls whether the extension is enabled (`true` by default). When `false`, the extension is not applied to the element.
 * @param params Additional extension parameters.
 */
@Generated
fun DivScope.extension(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    isEnabled: Boolean? = null,
    params: Map<String, Any>? = null,
): Extension = Extension(
    Extension.Properties(
        id = valueOrNull(id),
        isEnabled = valueOrNull(isEnabled),
        params = valueOrNull(params),
    )
)

/**
 * @param id Extension ID.
 * @param isEnabled Controls whether the extension is enabled (`true` by default). When `false`, the extension is not applied to the element.
 * @param params Additional extension parameters.
 */
@Generated
fun DivScope.extensionProps(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    isEnabled: Boolean? = null,
    params: Map<String, Any>? = null,
) = Extension.Properties(
    id = valueOrNull(id),
    isEnabled = valueOrNull(isEnabled),
    params = valueOrNull(params),
)

/**
 * @param id Extension ID.
 * @param isEnabled Controls whether the extension is enabled (`true` by default). When `false`, the extension is not applied to the element.
 * @param params Additional extension parameters.
 */
@Generated
fun TemplateScope.extensionRefs(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    params: ReferenceProperty<Map<String, Any>>? = null,
) = Extension.Properties(
    id = id,
    isEnabled = isEnabled,
    params = params,
)

/**
 * @param id Extension ID.
 * @param isEnabled Controls whether the extension is enabled (`true` by default). When `false`, the extension is not applied to the element.
 * @param params Additional extension parameters.
 */
@Generated
fun Extension.override(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    isEnabled: Boolean? = null,
    params: Map<String, Any>? = null,
): Extension = Extension(
    Extension.Properties(
        id = valueOrNull(id) ?: properties.id,
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
        params = valueOrNull(params) ?: properties.params,
    )
)

/**
 * @param id Extension ID.
 * @param isEnabled Controls whether the extension is enabled (`true` by default). When `false`, the extension is not applied to the element.
 * @param params Additional extension parameters.
 */
@Generated
fun Extension.defer(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    params: ReferenceProperty<Map<String, Any>>? = null,
): Extension = Extension(
    Extension.Properties(
        id = id ?: properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        params = params ?: properties.params,
    )
)

/**
 * @param id Extension ID.
 * @param isEnabled Controls whether the extension is enabled (`true` by default). When `false`, the extension is not applied to the element.
 * @param params Additional extension parameters.
 */
@Generated
fun Extension.modify(
    `use named arguments`: Guard = Guard.instance,
    id: Property<String>? = null,
    isEnabled: Property<Boolean>? = null,
    params: Property<Map<String, Any>>? = null,
): Extension = Extension(
    Extension.Properties(
        id = id ?: properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        params = params ?: properties.params,
    )
)

/**
 * @param isEnabled Controls whether the extension is enabled (`true` by default). When `false`, the extension is not applied to the element.
 */
@Generated
fun Extension.evaluate(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: ExpressionProperty<Boolean>? = null,
): Extension = Extension(
    Extension.Properties(
        id = properties.id,
        isEnabled = isEnabled ?: properties.isEnabled,
        params = properties.params,
    )
)

@Generated
fun Extension.asList() = listOf(this)
