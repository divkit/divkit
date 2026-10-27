package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionTimer

@Generated
internal class ActionTimerSerializer : EntitySerializer<ActionTimer>(ActionTimer::class.java, "timer") {
    override fun writeFields(value: ActionTimer, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACTION, properties.action, provider)
        gen.writeProperty(ID, properties.id, provider)
    }

    private companion object {
        val ACTION = PropertyName("action")
        val ID = PropertyName("id")
    }
}
