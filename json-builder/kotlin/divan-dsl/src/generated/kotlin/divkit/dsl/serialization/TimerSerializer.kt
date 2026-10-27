package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Timer

@Generated
internal class TimerSerializer : EntitySerializer<Timer>(Timer::class.java) {
    override fun writeFields(value: Timer, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(END_ACTIONS, properties.endActions, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(TICK_ACTIONS, properties.tickActions, provider)
        gen.writeProperty(TICK_INTERVAL, properties.tickInterval, provider)
        gen.writeProperty(VALUE_VARIABLE, properties.valueVariable, provider)
    }

    private companion object {
        val DURATION = PropertyName("duration")
        val END_ACTIONS = PropertyName("end_actions")
        val ID = PropertyName("id")
        val TICK_ACTIONS = PropertyName("tick_actions")
        val TICK_INTERVAL = PropertyName("tick_interval")
        val VALUE_VARIABLE = PropertyName("value_variable")
    }
}
