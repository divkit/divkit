package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.WrapContentSize

@Generated
internal class WrapContentSizeSerializer : EntitySerializer<WrapContentSize>(WrapContentSize::class.java, "wrap_content") {
    override fun writeFields(value: WrapContentSize, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(CONSTRAINED, properties.constrained, provider)
        gen.writeProperty(MAX_SIZE, properties.maxSize, provider)
        gen.writeProperty(MIN_SIZE, properties.minSize, provider)
    }

    private companion object {
        val CONSTRAINED = PropertyName("constrained")
        val MAX_SIZE = PropertyName("max_size")
        val MIN_SIZE = PropertyName("min_size")
    }
}
