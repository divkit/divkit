package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionScrollTo

@Generated
internal class ActionScrollToSerializer : EntitySerializer<ActionScrollTo>(ActionScrollTo::class.java, "scroll_to") {
    override fun writeFields(value: ActionScrollTo, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ANIMATED, properties.animated, provider)
        gen.writeProperty(DESTINATION, properties.destination, provider)
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(ID, properties.id, provider)
    }

    private companion object {
        val ANIMATED = PropertyName("animated")
        val DESTINATION = PropertyName("destination")
        val DURATION = PropertyName("duration")
        val ID = PropertyName("id")
    }
}
