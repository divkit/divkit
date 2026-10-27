package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Animation

@Generated
internal class AnimationSerializer : EntitySerializer<Animation>(Animation::class.java) {
    override fun writeFields(value: Animation, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(END_VALUE, properties.endValue, provider)
        gen.writeProperty(INTERPOLATOR, properties.interpolator, provider)
        gen.writeProperty(ITEMS, properties.items, provider)
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(REPEAT, properties.repeat, provider)
        gen.writeProperty(START_DELAY, properties.startDelay, provider)
        gen.writeProperty(START_VALUE, properties.startValue, provider)
    }

    private companion object {
        val DURATION = PropertyName("duration")
        val END_VALUE = PropertyName("end_value")
        val INTERPOLATOR = PropertyName("interpolator")
        val ITEMS = PropertyName("items")
        val NAME = PropertyName("name")
        val REPEAT = PropertyName("repeat")
        val START_DELAY = PropertyName("start_delay")
        val START_VALUE = PropertyName("start_value")
    }
}
