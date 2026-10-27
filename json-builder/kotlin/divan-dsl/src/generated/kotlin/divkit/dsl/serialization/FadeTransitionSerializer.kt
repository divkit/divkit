package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.FadeTransition

@Generated
internal class FadeTransitionSerializer : EntitySerializer<FadeTransition>(FadeTransition::class.java, "fade") {
    override fun writeFields(value: FadeTransition, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ALPHA, properties.alpha, provider)
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(INTERPOLATOR, properties.interpolator, provider)
        gen.writeProperty(START_DELAY, properties.startDelay, provider)
    }

    private companion object {
        val ALPHA = PropertyName("alpha")
        val DURATION = PropertyName("duration")
        val INTERPOLATOR = PropertyName("interpolator")
        val START_DELAY = PropertyName("start_delay")
    }
}
