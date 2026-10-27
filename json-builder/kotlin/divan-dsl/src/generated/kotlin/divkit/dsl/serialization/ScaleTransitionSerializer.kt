package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ScaleTransition

@Generated
internal class ScaleTransitionSerializer : EntitySerializer<ScaleTransition>(ScaleTransition::class.java, "scale") {
    override fun writeFields(value: ScaleTransition, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(INTERPOLATOR, properties.interpolator, provider)
        gen.writeProperty(PIVOT_X, properties.pivotX, provider)
        gen.writeProperty(PIVOT_Y, properties.pivotY, provider)
        gen.writeProperty(SCALE, properties.scale, provider)
        gen.writeProperty(START_DELAY, properties.startDelay, provider)
    }

    private companion object {
        val DURATION = PropertyName("duration")
        val INTERPOLATOR = PropertyName("interpolator")
        val PIVOT_X = PropertyName("pivot_x")
        val PIVOT_Y = PropertyName("pivot_y")
        val SCALE = PropertyName("scale")
        val START_DELAY = PropertyName("start_delay")
    }
}
