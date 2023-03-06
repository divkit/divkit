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
 * It sets margins.
 * 
 * Can be created using the method [edgeInsets].
 */
@Generated
class EdgeInsets internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): EdgeInsets = EdgeInsets(
        Properties(
            bottom = additive.bottom ?: properties.bottom,
            left = additive.left ?: properties.left,
            right = additive.right ?: properties.right,
            top = additive.top ?: properties.top,
            unit = additive.unit ?: properties.unit,
        )
    )

    class Properties internal constructor(
        /**
         * Bottom margin.
         * Default value: `0`.
         */
        val bottom: Property<Int>?,
        /**
         * Left margin.
         * Default value: `0`.
         */
        val left: Property<Int>?,
        /**
         * Right margin.
         * Default value: `0`.
         */
        val right: Property<Int>?,
        /**
         * Top margin.
         * Default value: `0`.
         */
        val top: Property<Int>?,
        /**
         * Default value: `dp`.
         */
        val unit: Property<SizeUnit>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("bottom", bottom)
            result.tryPutProperty("left", left)
            result.tryPutProperty("right", right)
            result.tryPutProperty("top", top)
            result.tryPutProperty("unit", unit)
            return result
        }
    }
}

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun DivScope.edgeInsets(
    `use named arguments`: Guard = Guard.instance,
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
    unit: SizeUnit? = null,
): EdgeInsets = EdgeInsets(
    EdgeInsets.Properties(
        bottom = valueOrNull(bottom),
        left = valueOrNull(left),
        right = valueOrNull(right),
        top = valueOrNull(top),
        unit = valueOrNull(unit),
    )
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun DivScope.edgeInsetsProps(
    `use named arguments`: Guard = Guard.instance,
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
    unit: SizeUnit? = null,
) = EdgeInsets.Properties(
    bottom = valueOrNull(bottom),
    left = valueOrNull(left),
    right = valueOrNull(right),
    top = valueOrNull(top),
    unit = valueOrNull(unit),
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun TemplateScope.edgeInsetsRefs(
    `use named arguments`: Guard = Guard.instance,
    bottom: ReferenceProperty<Int>? = null,
    left: ReferenceProperty<Int>? = null,
    right: ReferenceProperty<Int>? = null,
    top: ReferenceProperty<Int>? = null,
    unit: ReferenceProperty<SizeUnit>? = null,
) = EdgeInsets.Properties(
    bottom = bottom,
    left = left,
    right = right,
    top = top,
    unit = unit,
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun EdgeInsets.override(
    `use named arguments`: Guard = Guard.instance,
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
    unit: SizeUnit? = null,
): EdgeInsets = EdgeInsets(
    EdgeInsets.Properties(
        bottom = valueOrNull(bottom) ?: properties.bottom,
        left = valueOrNull(left) ?: properties.left,
        right = valueOrNull(right) ?: properties.right,
        top = valueOrNull(top) ?: properties.top,
        unit = valueOrNull(unit) ?: properties.unit,
    )
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun EdgeInsets.defer(
    `use named arguments`: Guard = Guard.instance,
    bottom: ReferenceProperty<Int>? = null,
    left: ReferenceProperty<Int>? = null,
    right: ReferenceProperty<Int>? = null,
    top: ReferenceProperty<Int>? = null,
    unit: ReferenceProperty<SizeUnit>? = null,
): EdgeInsets = EdgeInsets(
    EdgeInsets.Properties(
        bottom = bottom ?: properties.bottom,
        left = left ?: properties.left,
        right = right ?: properties.right,
        top = top ?: properties.top,
        unit = unit ?: properties.unit,
    )
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun EdgeInsets.evaluate(
    `use named arguments`: Guard = Guard.instance,
    bottom: ExpressionProperty<Int>? = null,
    left: ExpressionProperty<Int>? = null,
    right: ExpressionProperty<Int>? = null,
    top: ExpressionProperty<Int>? = null,
    unit: ExpressionProperty<SizeUnit>? = null,
): EdgeInsets = EdgeInsets(
    EdgeInsets.Properties(
        bottom = bottom ?: properties.bottom,
        left = left ?: properties.left,
        right = right ?: properties.right,
        top = top ?: properties.top,
        unit = unit ?: properties.unit,
    )
)

@Generated
fun EdgeInsets.asList() = listOf(this)
