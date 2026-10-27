package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Point

@Generated
internal class PointSerializer : EntitySerializer<Point>(Point::class.java) {
    override fun writeFields(value: Point, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(X, properties.x, provider)
        gen.writeProperty(Y, properties.y, provider)
    }

    private companion object {
        val X = PropertyName("x")
        val Y = PropertyName("y")
    }
}
