package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.CornersRadius

@Generated
internal class CornersRadiusSerializer : EntitySerializer<CornersRadius>(CornersRadius::class.java) {
    override fun writeFields(value: CornersRadius, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(BOTTOM_LEFT, properties.bottomLeft, provider)
        gen.writeProperty(BOTTOM_RIGHT, properties.bottomRight, provider)
        gen.writeProperty(TOP_LEFT, properties.topLeft, provider)
        gen.writeProperty(TOP_RIGHT, properties.topRight, provider)
    }

    private companion object {
        val BOTTOM_LEFT = PropertyName("bottom-left")
        val BOTTOM_RIGHT = PropertyName("bottom-right")
        val TOP_LEFT = PropertyName("top-left")
        val TOP_RIGHT = PropertyName("top-right")
    }
}
