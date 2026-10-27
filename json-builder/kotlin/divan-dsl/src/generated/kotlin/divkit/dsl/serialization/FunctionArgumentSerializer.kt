package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.FunctionArgument

@Generated
internal class FunctionArgumentSerializer : EntitySerializer<FunctionArgument>(FunctionArgument::class.java) {
    override fun writeFields(value: FunctionArgument, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(TYPE, properties.type, provider)
    }

    private companion object {
        val NAME = PropertyName("name")
        val TYPE = PropertyName("type")
    }
}
