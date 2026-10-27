package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.AbsoluteEdgeInsets

@Generated
internal class AbsoluteEdgeInsetsSerializer : EntitySerializer<AbsoluteEdgeInsets>(AbsoluteEdgeInsets::class.java) {
    override fun writeFields(value: AbsoluteEdgeInsets, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(BOTTOM, properties.bottom, provider)
        gen.writeProperty(LEFT, properties.left, provider)
        gen.writeProperty(RIGHT, properties.right, provider)
        gen.writeProperty(TOP, properties.top, provider)
    }

    private companion object {
        val BOTTOM = PropertyName("bottom")
        val LEFT = PropertyName("left")
        val RIGHT = PropertyName("right")
        val TOP = PropertyName("top")
    }
}
