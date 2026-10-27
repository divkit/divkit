package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionAnimatorStart

@Generated
internal class ActionAnimatorStartSerializer : EntitySerializer<ActionAnimatorStart>(ActionAnimatorStart::class.java, "animator_start") {
    override fun writeFields(value: ActionAnimatorStart, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ANIMATOR_ID, properties.animatorId, provider)
        gen.writeProperty(DIRECTION, properties.direction, provider)
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(END_VALUE, properties.endValue, provider)
        gen.writeProperty(INTERPOLATOR, properties.interpolator, provider)
        gen.writeProperty(REPEAT_COUNT, properties.repeatCount, provider)
        gen.writeProperty(START_DELAY, properties.startDelay, provider)
        gen.writeProperty(START_VALUE, properties.startValue, provider)
    }

    private companion object {
        val ANIMATOR_ID = PropertyName("animator_id")
        val DIRECTION = PropertyName("direction")
        val DURATION = PropertyName("duration")
        val END_VALUE = PropertyName("end_value")
        val INTERPOLATOR = PropertyName("interpolator")
        val REPEAT_COUNT = PropertyName("repeat_count")
        val START_DELAY = PropertyName("start_delay")
        val START_VALUE = PropertyName("start_value")
    }
}
