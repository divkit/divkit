package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ColorAnimator

@Generated
internal class ColorAnimatorSerializer : EntitySerializer<ColorAnimator>(ColorAnimator::class.java, "color_animator") {
    override fun writeFields(value: ColorAnimator, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(CANCEL_ACTIONS, properties.cancelActions, provider)
        gen.writeProperty(DIRECTION, properties.direction, provider)
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(END_ACTIONS, properties.endActions, provider)
        gen.writeProperty(END_VALUE, properties.endValue, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(INTERPOLATOR, properties.interpolator, provider)
        gen.writeProperty(REPEAT_COUNT, properties.repeatCount, provider)
        gen.writeProperty(START_DELAY, properties.startDelay, provider)
        gen.writeProperty(START_VALUE, properties.startValue, provider)
        gen.writeProperty(VARIABLE_NAME, properties.variableName, provider)
    }

    private companion object {
        val CANCEL_ACTIONS = PropertyName("cancel_actions")
        val DIRECTION = PropertyName("direction")
        val DURATION = PropertyName("duration")
        val END_ACTIONS = PropertyName("end_actions")
        val END_VALUE = PropertyName("end_value")
        val ID = PropertyName("id")
        val INTERPOLATOR = PropertyName("interpolator")
        val REPEAT_COUNT = PropertyName("repeat_count")
        val START_DELAY = PropertyName("start_delay")
        val START_VALUE = PropertyName("start_value")
        val VARIABLE_NAME = PropertyName("variable_name")
    }
}
