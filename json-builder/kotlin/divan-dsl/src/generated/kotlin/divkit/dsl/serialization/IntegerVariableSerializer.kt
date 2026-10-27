package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.IntegerVariable

@Generated
internal class IntegerVariableSerializer : EntitySerializer<IntegerVariable>(IntegerVariable::class.java, "integer") {
    override fun writeFields(value: IntegerVariable, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val NAME = PropertyName("name")
        val VALUE = PropertyName("value")
    }
}
