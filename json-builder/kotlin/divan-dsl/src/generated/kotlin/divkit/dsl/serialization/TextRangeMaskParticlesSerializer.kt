package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.TextRangeMaskParticles

@Generated
internal class TextRangeMaskParticlesSerializer : EntitySerializer<TextRangeMaskParticles>(TextRangeMaskParticles::class.java, "particles") {
    override fun writeFields(value: TextRangeMaskParticles, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(DENSITY, properties.density, provider)
        gen.writeProperty(IS_ANIMATED, properties.isAnimated, provider)
        gen.writeProperty(IS_ENABLED, properties.isEnabled, provider)
        gen.writeProperty(PARTICLE_SIZE, properties.particleSize, provider)
    }

    private companion object {
        val COLOR = PropertyName("color")
        val DENSITY = PropertyName("density")
        val IS_ANIMATED = PropertyName("is_animated")
        val IS_ENABLED = PropertyName("is_enabled")
        val PARTICLE_SIZE = PropertyName("particle_size")
    }
}
