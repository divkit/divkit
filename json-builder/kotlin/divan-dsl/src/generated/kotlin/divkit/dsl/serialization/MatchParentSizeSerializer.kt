package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.MatchParentSize

@Generated
internal class MatchParentSizeSerializer : EntitySerializer<MatchParentSize>(MatchParentSize::class.java, "match_parent") {
    override fun writeFields(value: MatchParentSize, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(MAX_SIZE, properties.maxSize, provider)
        gen.writeProperty(MIN_SIZE, properties.minSize, provider)
        gen.writeProperty(WEIGHT, properties.weight, provider)
    }

    private companion object {
        val MAX_SIZE = PropertyName("max_size")
        val MIN_SIZE = PropertyName("min_size")
        val WEIGHT = PropertyName("weight")
    }
}
