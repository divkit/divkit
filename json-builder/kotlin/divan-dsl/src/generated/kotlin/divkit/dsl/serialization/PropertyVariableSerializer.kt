package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PropertyVariable

@Generated
internal class PropertyVariableSerializer : EntitySerializer<PropertyVariable>(PropertyVariable::class.java, "property") {
    override fun writeFields(value: PropertyVariable, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(GET, properties.get, provider)
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(NEW_VALUE_VARIABLE_NAME, properties.newValueVariableName, provider)
        gen.writeProperty(SET, properties.set, provider)
        gen.writeProperty(VALUE_TYPE, properties.valueType, provider)
    }

    private companion object {
        val GET = PropertyName("get")
        val NAME = PropertyName("name")
        val NEW_VALUE_VARIABLE_NAME = PropertyName("new_value_variable_name")
        val SET = PropertyName("set")
        val VALUE_TYPE = PropertyName("value_type")
    }
}
