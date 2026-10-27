package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Trigger

@Generated
internal class TriggerSerializer : EntitySerializer<Trigger>(Trigger::class.java) {
    override fun writeFields(value: Trigger, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACTIONS, properties.actions, provider)
        gen.writeProperty(CONDITION, properties.condition, provider)
        gen.writeProperty(MODE, properties.mode, provider)
    }

    private companion object {
        val ACTIONS = PropertyName("actions")
        val CONDITION = PropertyName("condition")
        val MODE = PropertyName("mode")
    }
}
