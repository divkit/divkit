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
 * A mask to hide text (spoiler) that looks like randomly distributed particles (telegram alike).
 * 
 * Can be created using the method [textRangeMaskParticles].
 * 
 * Required parameters: `type, color`.
 */
@Generated
data class TextRangeMaskParticles internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TextRangeMask {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "particles")
    )

    operator fun plus(additive: Properties): TextRangeMaskParticles = TextRangeMaskParticles(
        Properties(
            color = additive.color ?: properties.color,
            density = additive.density ?: properties.density,
            isAnimated = additive.isAnimated ?: properties.isAnimated,
            isEnabled = additive.isEnabled ?: properties.isEnabled,
            particleSize = additive.particleSize ?: properties.particleSize,
        )
    )

    data class Properties internal constructor(
        /**
         * Color of particles on the mask.
         */
        val color: Property<Color>?,
        /**
         * Density of particles on the mask, interpreted as a probability of a particle to spawn in a given point on the mask.
         * Default value: `0.8`.
         */
        val density: Property<Double>?,
        /**
         * Defines whether particles on the mask will be animated or not. Animation looks like smooth random particle movements (telegram alike).
         * Default value: `false`.
         */
        val isAnimated: Property<Boolean>?,
        /**
         * Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
         * Default value: `true`.
         */
        val isEnabled: Property<Boolean>?,
        /**
         * Size of a single particle on a mask.
         * Default value: `{"type":"fixed","value":1}`.
         */
        val particleSize: Property<FixedSize>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("color", color)
            result.tryPutProperty("density", density)
            result.tryPutProperty("is_animated", isAnimated)
            result.tryPutProperty("is_enabled", isEnabled)
            result.tryPutProperty("particle_size", particleSize)
            return result
        }
    }
}

/**
 * @param color Color of particles on the mask.
 * @param density Density of particles on the mask, interpreted as a probability of a particle to spawn in a given point on the mask.
 * @param isAnimated Defines whether particles on the mask will be animated or not. Animation looks like smooth random particle movements (telegram alike).
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 * @param particleSize Size of a single particle on a mask.
 */
@Generated
fun DivScope.textRangeMaskParticles(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    density: Double? = null,
    isAnimated: Boolean? = null,
    isEnabled: Boolean? = null,
    particleSize: FixedSize? = null,
): TextRangeMaskParticles = TextRangeMaskParticles(
    TextRangeMaskParticles.Properties(
        color = valueOrNull(color),
        density = valueOrNull(density),
        isAnimated = valueOrNull(isAnimated),
        isEnabled = valueOrNull(isEnabled),
        particleSize = valueOrNull(particleSize),
    )
)

/**
 * @param color Color of particles on the mask.
 * @param density Density of particles on the mask, interpreted as a probability of a particle to spawn in a given point on the mask.
 * @param isAnimated Defines whether particles on the mask will be animated or not. Animation looks like smooth random particle movements (telegram alike).
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 * @param particleSize Size of a single particle on a mask.
 */
@Generated
fun DivScope.textRangeMaskParticlesProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    density: Double? = null,
    isAnimated: Boolean? = null,
    isEnabled: Boolean? = null,
    particleSize: FixedSize? = null,
) = TextRangeMaskParticles.Properties(
    color = valueOrNull(color),
    density = valueOrNull(density),
    isAnimated = valueOrNull(isAnimated),
    isEnabled = valueOrNull(isEnabled),
    particleSize = valueOrNull(particleSize),
)

/**
 * @param color Color of particles on the mask.
 * @param density Density of particles on the mask, interpreted as a probability of a particle to spawn in a given point on the mask.
 * @param isAnimated Defines whether particles on the mask will be animated or not. Animation looks like smooth random particle movements (telegram alike).
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 * @param particleSize Size of a single particle on a mask.
 */
@Generated
fun TemplateScope.textRangeMaskParticlesRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    density: ReferenceProperty<Double>? = null,
    isAnimated: ReferenceProperty<Boolean>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    particleSize: ReferenceProperty<FixedSize>? = null,
) = TextRangeMaskParticles.Properties(
    color = color,
    density = density,
    isAnimated = isAnimated,
    isEnabled = isEnabled,
    particleSize = particleSize,
)

/**
 * @param color Color of particles on the mask.
 * @param density Density of particles on the mask, interpreted as a probability of a particle to spawn in a given point on the mask.
 * @param isAnimated Defines whether particles on the mask will be animated or not. Animation looks like smooth random particle movements (telegram alike).
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 * @param particleSize Size of a single particle on a mask.
 */
@Generated
fun TextRangeMaskParticles.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    density: Double? = null,
    isAnimated: Boolean? = null,
    isEnabled: Boolean? = null,
    particleSize: FixedSize? = null,
): TextRangeMaskParticles = TextRangeMaskParticles(
    TextRangeMaskParticles.Properties(
        color = valueOrNull(color) ?: properties.color,
        density = valueOrNull(density) ?: properties.density,
        isAnimated = valueOrNull(isAnimated) ?: properties.isAnimated,
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
        particleSize = valueOrNull(particleSize) ?: properties.particleSize,
    )
)

/**
 * @param color Color of particles on the mask.
 * @param density Density of particles on the mask, interpreted as a probability of a particle to spawn in a given point on the mask.
 * @param isAnimated Defines whether particles on the mask will be animated or not. Animation looks like smooth random particle movements (telegram alike).
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 * @param particleSize Size of a single particle on a mask.
 */
@Generated
fun TextRangeMaskParticles.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    density: ReferenceProperty<Double>? = null,
    isAnimated: ReferenceProperty<Boolean>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
    particleSize: ReferenceProperty<FixedSize>? = null,
): TextRangeMaskParticles = TextRangeMaskParticles(
    TextRangeMaskParticles.Properties(
        color = color ?: properties.color,
        density = density ?: properties.density,
        isAnimated = isAnimated ?: properties.isAnimated,
        isEnabled = isEnabled ?: properties.isEnabled,
        particleSize = particleSize ?: properties.particleSize,
    )
)

/**
 * @param color Color of particles on the mask.
 * @param density Density of particles on the mask, interpreted as a probability of a particle to spawn in a given point on the mask.
 * @param isAnimated Defines whether particles on the mask will be animated or not. Animation looks like smooth random particle movements (telegram alike).
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 */
@Generated
fun TextRangeMaskParticles.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
    density: ExpressionProperty<Double>? = null,
    isAnimated: ExpressionProperty<Boolean>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
): TextRangeMaskParticles = TextRangeMaskParticles(
    TextRangeMaskParticles.Properties(
        color = color ?: properties.color,
        density = density ?: properties.density,
        isAnimated = isAnimated ?: properties.isAnimated,
        isEnabled = isEnabled ?: properties.isEnabled,
        particleSize = properties.particleSize,
    )
)

@Generated
fun TextRangeMaskParticles.asList() = listOf(this)
