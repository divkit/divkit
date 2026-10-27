package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.SlideTransition

@Generated
internal class SlideTransitionSerializer : EntitySerializer<SlideTransition>(SlideTransition::class.java, "slide") {
    override fun writeFields(value: SlideTransition, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DISTANCE, properties.distance, provider)
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(EDGE, properties.edge, provider)
        gen.writeProperty(INTERPOLATOR, properties.interpolator, provider)
        gen.writeProperty(START_DELAY, properties.startDelay, provider)
    }

    private companion object {
        val DISTANCE = PropertyName("distance")
        val DURATION = PropertyName("duration")
        val EDGE = PropertyName("edge")
        val INTERPOLATOR = PropertyName("interpolator")
        val START_DELAY = PropertyName("start_delay")
    }
}
