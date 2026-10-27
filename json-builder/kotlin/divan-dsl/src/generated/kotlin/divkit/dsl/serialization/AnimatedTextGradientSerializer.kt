package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.AnimatedTextGradient

@Generated
internal class AnimatedTextGradientSerializer : EntitySerializer<AnimatedTextGradient>(AnimatedTextGradient::class.java, "animated") {
    override fun writeFields(value: AnimatedTextGradient, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(GRADIENT, properties.gradient, provider)
    }

    private companion object {
        val DURATION = PropertyName("duration")
        val GRADIENT = PropertyName("gradient")
    }
}
