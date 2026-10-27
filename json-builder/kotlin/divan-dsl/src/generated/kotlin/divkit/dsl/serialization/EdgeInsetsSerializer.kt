package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.EdgeInsets

@Generated
internal class EdgeInsetsSerializer : EntitySerializer<EdgeInsets>(EdgeInsets::class.java) {
    override fun writeFields(value: EdgeInsets, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(BOTTOM, properties.bottom, provider)
        gen.writeProperty(END, properties.end, provider)
        gen.writeProperty(LEFT, properties.left, provider)
        gen.writeProperty(RIGHT, properties.right, provider)
        gen.writeProperty(START, properties.start, provider)
        gen.writeProperty(TOP, properties.top, provider)
        gen.writeProperty(UNIT, properties.unit, provider)
    }

    private companion object {
        val BOTTOM = PropertyName("bottom")
        val END = PropertyName("end")
        val LEFT = PropertyName("left")
        val RIGHT = PropertyName("right")
        val START = PropertyName("start")
        val TOP = PropertyName("top")
        val UNIT = PropertyName("unit")
    }
}
