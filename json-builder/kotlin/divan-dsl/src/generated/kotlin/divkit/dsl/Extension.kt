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
         * Additional extension parameters.
         */
        val params: Property<Map<String, Any>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("id", id)
            result.tryPutProperty("params", params)
            return result
        }
    }
}

/**
 * @param id Extension ID.
 * @param params Additional extension parameters.
 */
@Generated
fun DivScope.extension(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    params: Map<String, Any>? = null,
): Extension = Extension(
    Extension.Properties(
        id = valueOrNull(id),
        params = valueOrNull(params),
    )
)

/**
 * @param id Extension ID.
 * @param params Additional extension parameters.
 */
@Generated
fun DivScope.extensionProps(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    params: Map<String, Any>? = null,
) = Extension.Properties(
    id = valueOrNull(id),
    params = valueOrNull(params),
)

/**
 * @param id Extension ID.
 * @param params Additional extension parameters.
 */
@Generated
fun TemplateScope.extensionRefs(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    params: ReferenceProperty<Map<String, Any>>? = null,
) = Extension.Properties(
    id = id,
    params = params,
)

/**
 * @param id Extension ID.
 * @param params Additional extension parameters.
 */
@Generated
fun Extension.override(
    `use named arguments`: Guard = Guard.instance,
    id: String? = null,
    params: Map<String, Any>? = null,
): Extension = Extension(
    Extension.Properties(
        id = valueOrNull(id) ?: properties.id,
        params = valueOrNull(params) ?: properties.params,
    )
)

/**
 * @param id Extension ID.
 * @param params Additional extension parameters.
 */
@Generated
fun Extension.defer(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<String>? = null,
    params: ReferenceProperty<Map<String, Any>>? = null,
): Extension = Extension(
    Extension.Properties(
        id = id ?: properties.id,
        params = params ?: properties.params,
    )
)

/**
 * @param id Extension ID.
 * @param params Additional extension parameters.
 */
@Generated
fun Extension.modify(
    `use named arguments`: Guard = Guard.instance,
    id: Property<String>? = null,
    params: Property<Map<String, Any>>? = null,
): Extension = Extension(
    Extension.Properties(
        id = id ?: properties.id,
        params = params ?: properties.params,
    )
)

@Generated
fun Extension.asList() = listOf(this)
