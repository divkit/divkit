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
 * Rotation transformation.
 * 
 * Can be created using the method [rotationTransformation].
 * 
 * Required parameters: `type, angle`.
 */
@Generated
@ExposedCopyVisibility
data class RotationTransformation internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Transformation {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "rotation")
    )

    operator fun plus(additive: Properties): RotationTransformation = RotationTransformation(
        Properties(
            angle = additive.angle ?: properties.angle,
            pivotX = additive.pivotX ?: properties.pivotX,
            pivotY = additive.pivotY ?: properties.pivotY,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Rotation angle in degrees.
         */
        val angle: Property<Double>?,
        /**
         * X coordinate of the rotation pivot point.
         * Default value: `{"type": "pivot-percentage","value":50}`.
         */
        val pivotX: Property<Pivot>?,
        /**
         * Y coordinate of the rotation pivot point.
         * Default value: `{"type": "pivot-percentage","value":50}`.
         */
        val pivotY: Property<Pivot>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("angle", angle)
            result.tryPutProperty("pivot_x", pivotX)
            result.tryPutProperty("pivot_y", pivotY)
            return result
        }
    }
}

/**
 * @param angle Rotation angle in degrees.
 * @param pivotX X coordinate of the rotation pivot point.
 * @param pivotY Y coordinate of the rotation pivot point.
 */
@Generated
fun DivScope.rotationTransformation(
    `use named arguments`: Guard = Guard.instance,
    angle: Double? = null,
    pivotX: Pivot? = null,
    pivotY: Pivot? = null,
): RotationTransformation = RotationTransformation(
    RotationTransformation.Properties(
        angle = valueOrNull(angle),
        pivotX = valueOrNull(pivotX),
        pivotY = valueOrNull(pivotY),
    )
)

/**
 * @param angle Rotation angle in degrees.
 * @param pivotX X coordinate of the rotation pivot point.
 * @param pivotY Y coordinate of the rotation pivot point.
 */
@Generated
fun DivScope.rotationTransformationProps(
    `use named arguments`: Guard = Guard.instance,
    angle: Double? = null,
    pivotX: Pivot? = null,
    pivotY: Pivot? = null,
) = RotationTransformation.Properties(
    angle = valueOrNull(angle),
    pivotX = valueOrNull(pivotX),
    pivotY = valueOrNull(pivotY),
)

/**
 * @param angle Rotation angle in degrees.
 * @param pivotX X coordinate of the rotation pivot point.
 * @param pivotY Y coordinate of the rotation pivot point.
 */
@Generated
fun TemplateScope.rotationTransformationRefs(
    `use named arguments`: Guard = Guard.instance,
    angle: ReferenceProperty<Double>? = null,
    pivotX: ReferenceProperty<Pivot>? = null,
    pivotY: ReferenceProperty<Pivot>? = null,
) = RotationTransformation.Properties(
    angle = angle,
    pivotX = pivotX,
    pivotY = pivotY,
)

/**
 * @param angle Rotation angle in degrees.
 * @param pivotX X coordinate of the rotation pivot point.
 * @param pivotY Y coordinate of the rotation pivot point.
 */
@Generated
fun RotationTransformation.override(
    `use named arguments`: Guard = Guard.instance,
    angle: Double? = null,
    pivotX: Pivot? = null,
    pivotY: Pivot? = null,
): RotationTransformation = RotationTransformation(
    RotationTransformation.Properties(
        angle = valueOrNull(angle) ?: properties.angle,
        pivotX = valueOrNull(pivotX) ?: properties.pivotX,
        pivotY = valueOrNull(pivotY) ?: properties.pivotY,
    )
)

/**
 * @param angle Rotation angle in degrees.
 * @param pivotX X coordinate of the rotation pivot point.
 * @param pivotY Y coordinate of the rotation pivot point.
 */
@Generated
fun RotationTransformation.defer(
    `use named arguments`: Guard = Guard.instance,
    angle: ReferenceProperty<Double>? = null,
    pivotX: ReferenceProperty<Pivot>? = null,
    pivotY: ReferenceProperty<Pivot>? = null,
): RotationTransformation = RotationTransformation(
    RotationTransformation.Properties(
        angle = angle ?: properties.angle,
        pivotX = pivotX ?: properties.pivotX,
        pivotY = pivotY ?: properties.pivotY,
    )
)

/**
 * @param angle Rotation angle in degrees.
 * @param pivotX X coordinate of the rotation pivot point.
 * @param pivotY Y coordinate of the rotation pivot point.
 */
@Generated
fun RotationTransformation.modify(
    `use named arguments`: Guard = Guard.instance,
    angle: Property<Double>? = null,
    pivotX: Property<Pivot>? = null,
    pivotY: Property<Pivot>? = null,
): RotationTransformation = RotationTransformation(
    RotationTransformation.Properties(
        angle = angle ?: properties.angle,
        pivotX = pivotX ?: properties.pivotX,
        pivotY = pivotY ?: properties.pivotY,
    )
)

/**
 * @param angle Rotation angle in degrees.
 */
@Generated
fun RotationTransformation.evaluate(
    `use named arguments`: Guard = Guard.instance,
    angle: ExpressionProperty<Double>? = null,
): RotationTransformation = RotationTransformation(
    RotationTransformation.Properties(
        angle = angle ?: properties.angle,
        pivotX = properties.pivotX,
        pivotY = properties.pivotY,
    )
)

@Generated
fun RotationTransformation.asList() = listOf(this)
