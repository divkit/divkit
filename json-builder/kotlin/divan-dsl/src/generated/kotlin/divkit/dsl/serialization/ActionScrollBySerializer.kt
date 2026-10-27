package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionScrollBy

@Generated
internal class ActionScrollBySerializer : EntitySerializer<ActionScrollBy>(ActionScrollBy::class.java, "scroll_by") {
    override fun writeFields(value: ActionScrollBy, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ANIMATED, properties.animated, provider)
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(ITEM_COUNT, properties.itemCount, provider)
        gen.writeProperty(OFFSET, properties.offset, provider)
        gen.writeProperty(OVERFLOW, properties.overflow, provider)
    }

    private companion object {
        val ANIMATED = PropertyName("animated")
        val DURATION = PropertyName("duration")
        val ID = PropertyName("id")
        val ITEM_COUNT = PropertyName("item_count")
        val OFFSET = PropertyName("offset")
        val OVERFLOW = PropertyName("overflow")
    }
}
