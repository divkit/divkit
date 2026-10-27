package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionVideo

@Generated
internal class ActionVideoSerializer : EntitySerializer<ActionVideo>(ActionVideo::class.java, "video") {
    override fun writeFields(value: ActionVideo, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACTION, properties.action, provider)
        gen.writeProperty(ID, properties.id, provider)
    }

    private companion object {
        val ACTION = PropertyName("action")
        val ID = PropertyName("id")
    }
}
