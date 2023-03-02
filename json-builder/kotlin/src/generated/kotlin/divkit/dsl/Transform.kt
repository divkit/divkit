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
 * Transformation of the element.
 * 
 * Can be created using the method [transform].
 */
@Generated
class Transform internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Transform = Transform(
        Properties(
            pivotX = additive.pivotX ?: properties.pivotX,
            pivotY = additive.pivotY ?: properties.pivotY,
            rotation = additive.rotation ?: properties.rotation,
        )
    )

    class Properties internal constructor(
        /**
         * X coordinate of the rotation axis.
         * Default value: `{"type": "pivot-percentage","value":50}`.
         */
        val pivotX: Property<Pivot>?,
        /**
         * Y coordinate of the rotation axis.
         * Default value: `{"type": "pivot-percentage","value":50}`.
         */
        val pivotY: Property<Pivot>?,
        /**
         * Degrees of the element rotation. Positive values used for clockwise rotation.
         */
        val rotation: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("pivot_x", pivotX)
            result.tryPutProperty("pivot_y", pivotY)
            result.tryPutProperty("rotation", rotation)
            return result
        }
    }
}

/**
 * @param pivotX X coordinate of the rotation axis.
 * @param pivotY Y coordinate of the rotation axis.
 * @param rotation Degrees of the element rotation. Positive values used for clockwise rotation.
 */
@Generated
fun DivScope.transform(
    `use named arguments`: Guard = Guard.instance,
    pivotX: Pivot? = null,
    pivotY: Pivot? = null,
    rotation: Double? = null,
): Transform = Transform(
    Transform.Properties(
        pivotX = valueOrNull(pivotX),
        pivotY = valueOrNull(pivotY),
        rotation = valueOrNull(rotation),
    )
)

/**
 * @param pivotX X coordinate of the rotation axis.
 * @param pivotY Y coordinate of the rotation axis.
 * @param rotation Degrees of the element rotation. Positive values used for clockwise rotation.
 */
@Generated
fun DivScope.transformProps(
    `use named arguments`: Guard = Guard.instance,
    pivotX: Pivot? = null,
    pivotY: Pivot? = null,
    rotation: Double? = null,
) = Transform.Properties(
    pivotX = valueOrNull(pivotX),
    pivotY = valueOrNull(pivotY),
    rotation = valueOrNull(rotation),
)

/**
 * @param pivotX X coordinate of the rotation axis.
 * @param pivotY Y coordinate of the rotation axis.
 * @param rotation Degrees of the element rotation. Positive values used for clockwise rotation.
 */
@Generated
fun TemplateScope.transformRefs(
    `use named arguments`: Guard = Guard.instance,
    pivotX: ReferenceProperty<Pivot>? = null,
    pivotY: ReferenceProperty<Pivot>? = null,
    rotation: ReferenceProperty<Double>? = null,
) = Transform.Properties(
    pivotX = pivotX,
    pivotY = pivotY,
    rotation = rotation,
)

/**
 * @param pivotX X coordinate of the rotation axis.
 * @param pivotY Y coordinate of the rotation axis.
 * @param rotation Degrees of the element rotation. Positive values used for clockwise rotation.
 */
@Generated
fun Transform.override(
    `use named arguments`: Guard = Guard.instance,
    pivotX: Pivot? = null,
    pivotY: Pivot? = null,
    rotation: Double? = null,
): Transform = Transform(
    Transform.Properties(
        pivotX = valueOrNull(pivotX) ?: properties.pivotX,
        pivotY = valueOrNull(pivotY) ?: properties.pivotY,
        rotation = valueOrNull(rotation) ?: properties.rotation,
    )
)

/**
 * @param pivotX X coordinate of the rotation axis.
 * @param pivotY Y coordinate of the rotation axis.
 * @param rotation Degrees of the element rotation. Positive values used for clockwise rotation.
 */
@Generated
fun Transform.defer(
    `use named arguments`: Guard = Guard.instance,
    pivotX: ReferenceProperty<Pivot>? = null,
    pivotY: ReferenceProperty<Pivot>? = null,
    rotation: ReferenceProperty<Double>? = null,
): Transform = Transform(
    Transform.Properties(
        pivotX = pivotX ?: properties.pivotX,
        pivotY = pivotY ?: properties.pivotY,
        rotation = rotation ?: properties.rotation,
    )
)

/**
 * @param rotation Degrees of the element rotation. Positive values used for clockwise rotation.
 */
@Generated
fun Transform.evaluate(
    `use named arguments`: Guard = Guard.instance,
    rotation: ExpressionProperty<Double>? = null,
): Transform = Transform(
    Transform.Properties(
        pivotX = properties.pivotX,
        pivotY = properties.pivotY,
        rotation = rotation ?: properties.rotation,
    )
)

@Generated
fun Transform.asList() = listOf(this)
