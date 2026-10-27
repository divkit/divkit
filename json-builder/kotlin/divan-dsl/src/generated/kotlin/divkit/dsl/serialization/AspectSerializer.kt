package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Aspect

@Generated
internal class AspectSerializer : EntitySerializer<Aspect>(Aspect::class.java) {
    override fun writeFields(value: Aspect, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(RATIO, properties.ratio, provider)
    }

    private companion object {
        val RATIO = PropertyName("ratio")
    }
}
