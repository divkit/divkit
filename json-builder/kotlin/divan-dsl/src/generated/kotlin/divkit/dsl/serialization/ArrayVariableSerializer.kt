package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ArrayVariable

@Generated
internal class ArrayVariableSerializer : EntitySerializer<ArrayVariable>(ArrayVariable::class.java, "array") {
    override fun writeFields(value: ArrayVariable, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val NAME = PropertyName("name")
        val VALUE = PropertyName("value")
    }
}
