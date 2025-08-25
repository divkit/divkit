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
 * Sets margins without regard to screen properties.
 * 
 * Can be created using the method [absoluteEdgeInsets].
 */
@Generated
@ExposedCopyVisibility
data class AbsoluteEdgeInsets internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): AbsoluteEdgeInsets = AbsoluteEdgeInsets(
        Properties(
            bottom = additive.bottom ?: properties.bottom,
            left = additive.left ?: properties.left,
            right = additive.right ?: properties.right,
            top = additive.top ?: properties.top,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
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
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("bottom", bottom)
            result.tryPutProperty("left", left)
            result.tryPutProperty("right", right)
            result.tryPutProperty("top", top)
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
fun DivScope.absoluteEdgeInsets(
    `use named arguments`: Guard = Guard.instance,
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
): AbsoluteEdgeInsets = AbsoluteEdgeInsets(
    AbsoluteEdgeInsets.Properties(
        bottom = valueOrNull(bottom),
        left = valueOrNull(left),
        right = valueOrNull(right),
        top = valueOrNull(top),
    )
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun DivScope.absoluteEdgeInsetsProps(
    `use named arguments`: Guard = Guard.instance,
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
) = AbsoluteEdgeInsets.Properties(
    bottom = valueOrNull(bottom),
    left = valueOrNull(left),
    right = valueOrNull(right),
    top = valueOrNull(top),
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun TemplateScope.absoluteEdgeInsetsRefs(
    `use named arguments`: Guard = Guard.instance,
    bottom: ReferenceProperty<Int>? = null,
    left: ReferenceProperty<Int>? = null,
    right: ReferenceProperty<Int>? = null,
    top: ReferenceProperty<Int>? = null,
) = AbsoluteEdgeInsets.Properties(
    bottom = bottom,
    left = left,
    right = right,
    top = top,
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun AbsoluteEdgeInsets.override(
    `use named arguments`: Guard = Guard.instance,
    bottom: Int? = null,
    left: Int? = null,
    right: Int? = null,
    top: Int? = null,
): AbsoluteEdgeInsets = AbsoluteEdgeInsets(
    AbsoluteEdgeInsets.Properties(
        bottom = valueOrNull(bottom) ?: properties.bottom,
        left = valueOrNull(left) ?: properties.left,
        right = valueOrNull(right) ?: properties.right,
        top = valueOrNull(top) ?: properties.top,
    )
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun AbsoluteEdgeInsets.defer(
    `use named arguments`: Guard = Guard.instance,
    bottom: ReferenceProperty<Int>? = null,
    left: ReferenceProperty<Int>? = null,
    right: ReferenceProperty<Int>? = null,
    top: ReferenceProperty<Int>? = null,
): AbsoluteEdgeInsets = AbsoluteEdgeInsets(
    AbsoluteEdgeInsets.Properties(
        bottom = bottom ?: properties.bottom,
        left = left ?: properties.left,
        right = right ?: properties.right,
        top = top ?: properties.top,
    )
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun AbsoluteEdgeInsets.modify(
    `use named arguments`: Guard = Guard.instance,
    bottom: Property<Int>? = null,
    left: Property<Int>? = null,
    right: Property<Int>? = null,
    top: Property<Int>? = null,
): AbsoluteEdgeInsets = AbsoluteEdgeInsets(
    AbsoluteEdgeInsets.Properties(
        bottom = bottom ?: properties.bottom,
        left = left ?: properties.left,
        right = right ?: properties.right,
        top = top ?: properties.top,
    )
)

/**
 * @param bottom Bottom margin.
 * @param left Left margin.
 * @param right Right margin.
 * @param top Top margin.
 */
@Generated
fun AbsoluteEdgeInsets.evaluate(
    `use named arguments`: Guard = Guard.instance,
    bottom: ExpressionProperty<Int>? = null,
    left: ExpressionProperty<Int>? = null,
    right: ExpressionProperty<Int>? = null,
    top: ExpressionProperty<Int>? = null,
): AbsoluteEdgeInsets = AbsoluteEdgeInsets(
    AbsoluteEdgeInsets.Properties(
        bottom = bottom ?: properties.bottom,
        left = left ?: properties.left,
        right = right ?: properties.right,
        top = top ?: properties.top,
    )
)

@Generated
fun AbsoluteEdgeInsets.asList() = listOf(this)
