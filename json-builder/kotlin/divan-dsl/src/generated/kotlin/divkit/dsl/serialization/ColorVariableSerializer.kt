package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ColorVariable

@Generated
internal class ColorVariableSerializer : EntitySerializer<ColorVariable>(ColorVariable::class.java, "color") {
    override fun writeFields(value: ColorVariable, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val NAME = PropertyName("name")
        val VALUE = PropertyName("value")
    }
}
