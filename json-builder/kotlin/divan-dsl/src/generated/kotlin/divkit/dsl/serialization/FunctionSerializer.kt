package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Function

@Generated
internal class FunctionSerializer : EntitySerializer<Function>(Function::class.java) {
    override fun writeFields(value: Function, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ARGUMENTS, properties.arguments, provider)
        gen.writeProperty(BODY, properties.body, provider)
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(RETURN_TYPE, properties.returnType, provider)
    }

    private companion object {
        val ARGUMENTS = PropertyName("arguments")
        val BODY = PropertyName("body")
        val NAME = PropertyName("name")
        val RETURN_TYPE = PropertyName("return_type")
    }
}
