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
 * Can be created using the method [layoutProvider].
 */
@Generated
class LayoutProvider internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): LayoutProvider = LayoutProvider(
        Properties(
            heightVariableName = additive.heightVariableName ?: properties.heightVariableName,
            widthVariableName = additive.widthVariableName ?: properties.widthVariableName,
        )
    )

    class Properties internal constructor(
        /**
         * Variable name to store element height.
         */
        val heightVariableName: Property<String>?,
        /**
         * Variable name to store element width.
         */
        val widthVariableName: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("height_variable_name", heightVariableName)
            result.tryPutProperty("width_variable_name", widthVariableName)
            return result
        }
    }
}

/**
 * @param heightVariableName Variable name to store element height.
 * @param widthVariableName Variable name to store element width.
 */
@Generated
fun DivScope.layoutProvider(
    `use named arguments`: Guard = Guard.instance,
    heightVariableName: String? = null,
    widthVariableName: String? = null,
): LayoutProvider = LayoutProvider(
    LayoutProvider.Properties(
        heightVariableName = valueOrNull(heightVariableName),
        widthVariableName = valueOrNull(widthVariableName),
    )
)

/**
 * @param heightVariableName Variable name to store element height.
 * @param widthVariableName Variable name to store element width.
 */
@Generated
fun DivScope.layoutProviderProps(
    `use named arguments`: Guard = Guard.instance,
    heightVariableName: String? = null,
    widthVariableName: String? = null,
) = LayoutProvider.Properties(
    heightVariableName = valueOrNull(heightVariableName),
    widthVariableName = valueOrNull(widthVariableName),
)

/**
 * @param heightVariableName Variable name to store element height.
 * @param widthVariableName Variable name to store element width.
 */
@Generated
fun TemplateScope.layoutProviderRefs(
    `use named arguments`: Guard = Guard.instance,
    heightVariableName: ReferenceProperty<String>? = null,
    widthVariableName: ReferenceProperty<String>? = null,
) = LayoutProvider.Properties(
    heightVariableName = heightVariableName,
    widthVariableName = widthVariableName,
)

/**
 * @param heightVariableName Variable name to store element height.
 * @param widthVariableName Variable name to store element width.
 */
@Generated
fun LayoutProvider.override(
    `use named arguments`: Guard = Guard.instance,
    heightVariableName: String? = null,
    widthVariableName: String? = null,
): LayoutProvider = LayoutProvider(
    LayoutProvider.Properties(
        heightVariableName = valueOrNull(heightVariableName) ?: properties.heightVariableName,
        widthVariableName = valueOrNull(widthVariableName) ?: properties.widthVariableName,
    )
)

/**
 * @param heightVariableName Variable name to store element height.
 * @param widthVariableName Variable name to store element width.
 */
@Generated
fun LayoutProvider.defer(
    `use named arguments`: Guard = Guard.instance,
    heightVariableName: ReferenceProperty<String>? = null,
    widthVariableName: ReferenceProperty<String>? = null,
): LayoutProvider = LayoutProvider(
    LayoutProvider.Properties(
        heightVariableName = heightVariableName ?: properties.heightVariableName,
        widthVariableName = widthVariableName ?: properties.widthVariableName,
    )
)

@Generated
fun LayoutProvider.asList() = listOf(this)
