package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Data

@Generated
internal class DataSerializer : EntitySerializer<Data>(Data::class.java) {
    override fun writeFields(value: Data, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(LOG_ID, properties.logId, provider)
        gen.writeProperty(STATES, properties.states, provider)
        gen.writeProperty(FUNCTIONS, properties.functions, provider)
        gen.writeProperty(TIMERS, properties.timers, provider)
        gen.writeProperty(TRANSITION_ANIMATION_SELECTOR, properties.transitionAnimationSelector, provider)
        gen.writeProperty(VARIABLE_TRIGGERS, properties.variableTriggers, provider)
        gen.writeProperty(VARIABLES, properties.variables, provider)
    }

    private companion object {
        val LOG_ID = PropertyName("log_id")
        val STATES = PropertyName("states")
        val FUNCTIONS = PropertyName("functions")
        val TIMERS = PropertyName("timers")
        val TRANSITION_ANIMATION_SELECTOR = PropertyName("transition_animation_selector")
        val VARIABLE_TRIGGERS = PropertyName("variable_triggers")
        val VARIABLES = PropertyName("variables")
    }
}

@Generated
internal class DataStateSerializer : EntitySerializer<Data.State>(Data.State::class.java) {
    override fun writeFields(value: Data.State, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(STATE_ID, properties.stateId, provider)
        gen.writeProperty(DIV, properties.div, provider)
    }

    private companion object {
        val STATE_ID = PropertyName("state_id")
        val DIV = PropertyName("div")
    }
}
