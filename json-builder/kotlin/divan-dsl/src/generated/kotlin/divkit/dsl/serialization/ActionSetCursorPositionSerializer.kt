package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionSetCursorPosition

@Generated
internal class ActionSetCursorPositionSerializer : EntitySerializer<ActionSetCursorPosition>(ActionSetCursorPosition::class.java, "set_cursor_position") {
    override fun writeFields(value: ActionSetCursorPosition, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(POSITION, properties.position, provider)
    }

    private companion object {
        val ID = PropertyName("id")
        val POSITION = PropertyName("position")
    }
}

@Generated
internal class ActionSetCursorPositionPositionSerializer : EntitySerializer<ActionSetCursorPosition.Position>(ActionSetCursorPosition.Position::class.java, "absolute") {
    override fun writeFields(value: ActionSetCursorPosition.Position, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(END, properties.end, provider)
        gen.writeProperty(START, properties.start, provider)
    }

    private companion object {
        val END = PropertyName("end")
        val START = PropertyName("start")
    }
}
