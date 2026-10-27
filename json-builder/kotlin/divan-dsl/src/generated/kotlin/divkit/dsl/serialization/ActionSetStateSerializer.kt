package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionSetState

@Generated
internal class ActionSetStateSerializer : EntitySerializer<ActionSetState>(ActionSetState::class.java, "set_state") {
    override fun writeFields(value: ActionSetState, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(STATE_ID, properties.stateId, provider)
        gen.writeProperty(TEMPORARY, properties.temporary, provider)
    }

    private companion object {
        val STATE_ID = PropertyName("state_id")
        val TEMPORARY = PropertyName("temporary")
    }
}
