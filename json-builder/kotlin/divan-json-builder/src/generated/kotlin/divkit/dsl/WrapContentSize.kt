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
 * The size of an element adjusts to its contents.
 * 
 * Can be created using the method [wrapContentSize].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class WrapContentSize internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Size {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "wrap_content")
    )

    operator fun plus(additive: Properties): WrapContentSize = WrapContentSize(
        Properties(
            constrained = additive.constrained ?: properties.constrained,
            maxSize = additive.maxSize ?: properties.maxSize,
            minSize = additive.minSize ?: properties.minSize,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
         */
        val constrained: Property<Boolean>?,
        /**
         * Maximum size of an element.
         */
        val maxSize: Property<SizeUnitValue>?,
        /**
         * Minimum size of an element.
         */
        val minSize: Property<SizeUnitValue>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("constrained", constrained)
            result.tryPutProperty("max_size", maxSize)
            result.tryPutProperty("min_size", minSize)
            return result
        }
    }
}

/**
 * @param constrained The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 */
@Generated
fun DivScope.wrapContentSize(
    `use named arguments`: Guard = Guard.instance,
    constrained: Boolean? = null,
    maxSize: SizeUnitValue? = null,
    minSize: SizeUnitValue? = null,
): WrapContentSize = WrapContentSize(
    WrapContentSize.Properties(
        constrained = valueOrNull(constrained),
        maxSize = valueOrNull(maxSize),
        minSize = valueOrNull(minSize),
    )
)

/**
 * @param constrained The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 */
@Generated
fun DivScope.wrapContentSizeProps(
    `use named arguments`: Guard = Guard.instance,
    constrained: Boolean? = null,
    maxSize: SizeUnitValue? = null,
    minSize: SizeUnitValue? = null,
) = WrapContentSize.Properties(
    constrained = valueOrNull(constrained),
    maxSize = valueOrNull(maxSize),
    minSize = valueOrNull(minSize),
)

/**
 * @param constrained The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 */
@Generated
fun TemplateScope.wrapContentSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    constrained: ReferenceProperty<Boolean>? = null,
    maxSize: ReferenceProperty<SizeUnitValue>? = null,
    minSize: ReferenceProperty<SizeUnitValue>? = null,
) = WrapContentSize.Properties(
    constrained = constrained,
    maxSize = maxSize,
    minSize = minSize,
)

/**
 * @param constrained The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 */
@Generated
fun WrapContentSize.override(
    `use named arguments`: Guard = Guard.instance,
    constrained: Boolean? = null,
    maxSize: SizeUnitValue? = null,
    minSize: SizeUnitValue? = null,
): WrapContentSize = WrapContentSize(
    WrapContentSize.Properties(
        constrained = valueOrNull(constrained) ?: properties.constrained,
        maxSize = valueOrNull(maxSize) ?: properties.maxSize,
        minSize = valueOrNull(minSize) ?: properties.minSize,
    )
)

/**
 * @param constrained The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 */
@Generated
fun WrapContentSize.defer(
    `use named arguments`: Guard = Guard.instance,
    constrained: ReferenceProperty<Boolean>? = null,
    maxSize: ReferenceProperty<SizeUnitValue>? = null,
    minSize: ReferenceProperty<SizeUnitValue>? = null,
): WrapContentSize = WrapContentSize(
    WrapContentSize.Properties(
        constrained = constrained ?: properties.constrained,
        maxSize = maxSize ?: properties.maxSize,
        minSize = minSize ?: properties.minSize,
    )
)

/**
 * @param constrained The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 */
@Generated
fun WrapContentSize.modify(
    `use named arguments`: Guard = Guard.instance,
    constrained: Property<Boolean>? = null,
    maxSize: Property<SizeUnitValue>? = null,
    minSize: Property<SizeUnitValue>? = null,
): WrapContentSize = WrapContentSize(
    WrapContentSize.Properties(
        constrained = constrained ?: properties.constrained,
        maxSize = maxSize ?: properties.maxSize,
        minSize = minSize ?: properties.minSize,
    )
)

/**
 * @param constrained The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
 */
@Generated
fun WrapContentSize.evaluate(
    `use named arguments`: Guard = Guard.instance,
    constrained: ExpressionProperty<Boolean>? = null,
): WrapContentSize = WrapContentSize(
    WrapContentSize.Properties(
        constrained = constrained ?: properties.constrained,
        maxSize = properties.maxSize,
        minSize = properties.minSize,
    )
)

@Generated
fun WrapContentSize.asList() = listOf(this)
