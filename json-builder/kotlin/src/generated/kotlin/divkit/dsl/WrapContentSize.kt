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
 * Required properties: `type`.
 */
@Generated
class WrapContentSize internal constructor(
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

    class Properties internal constructor(
        /**
         * The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`.
         */
        val constrained: Property<Boolean>?,
        /**
         * Maximum size of an element.
         */
        val maxSize: Property<ConstraintSize>?,
        /**
         * Minimum size of an element.
         */
        val minSize: Property<ConstraintSize>?,
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

    /**
     * Can be created using the method [wrapContentSizeConstraintSize].
     * 
     * Required properties: `value`.
     */
    @Generated
    class ConstraintSize internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): ConstraintSize = ConstraintSize(
            Properties(
                unit = additive.unit ?: properties.unit,
                value = additive.value ?: properties.value,
            )
        )

        class Properties internal constructor(
            /**
             * Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
             * Default value: `dp`.
             */
            val unit: Property<SizeUnit>?,
            val value: Property<Int>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("unit", unit)
                result.tryPutProperty("value", value)
                return result
            }
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
    maxSize: WrapContentSize.ConstraintSize? = null,
    minSize: WrapContentSize.ConstraintSize? = null,
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
    maxSize: WrapContentSize.ConstraintSize? = null,
    minSize: WrapContentSize.ConstraintSize? = null,
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
    maxSize: ReferenceProperty<WrapContentSize.ConstraintSize>? = null,
    minSize: ReferenceProperty<WrapContentSize.ConstraintSize>? = null,
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
    maxSize: WrapContentSize.ConstraintSize? = null,
    minSize: WrapContentSize.ConstraintSize? = null,
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
    maxSize: ReferenceProperty<WrapContentSize.ConstraintSize>? = null,
    minSize: ReferenceProperty<WrapContentSize.ConstraintSize>? = null,
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

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 */
@Generated
fun DivScope.wrapContentSizeConstraintSize(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int,
): WrapContentSize.ConstraintSize = WrapContentSize.ConstraintSize(
    WrapContentSize.ConstraintSize.Properties(
        unit = valueOrNull(unit),
        value = valueOrNull(value),
    )
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 */
@Generated
fun DivScope.wrapContentSizeConstraintSizeProps(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
) = WrapContentSize.ConstraintSize.Properties(
    unit = valueOrNull(unit),
    value = valueOrNull(value),
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 */
@Generated
fun TemplateScope.wrapContentSizeConstraintSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
) = WrapContentSize.ConstraintSize.Properties(
    unit = unit,
    value = value,
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 */
@Generated
fun WrapContentSize.ConstraintSize.override(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): WrapContentSize.ConstraintSize = WrapContentSize.ConstraintSize(
    WrapContentSize.ConstraintSize.Properties(
        unit = valueOrNull(unit) ?: properties.unit,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param unit Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>
 */
@Generated
fun WrapContentSize.ConstraintSize.defer(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
): WrapContentSize.ConstraintSize = WrapContentSize.ConstraintSize(
    WrapContentSize.ConstraintSize.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

@Generated
fun WrapContentSize.ConstraintSize.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Int>? = null,
): WrapContentSize.ConstraintSize = WrapContentSize.ConstraintSize(
    WrapContentSize.ConstraintSize.Properties(
        unit = properties.unit,
        value = value ?: properties.value,
    )
)

@Generated
fun WrapContentSize.ConstraintSize.asList() = listOf(this)
