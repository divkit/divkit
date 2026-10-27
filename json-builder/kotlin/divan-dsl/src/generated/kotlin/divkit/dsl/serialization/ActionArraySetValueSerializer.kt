package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionArraySetValue

@Generated
internal class ActionArraySetValueSerializer : EntitySerializer<ActionArraySetValue>(ActionArraySetValue::class.java, "array_set_value") {
    override fun writeFields(value: ActionArraySetValue, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(INDEX, properties.index, provider)
        gen.writeProperty(VALUE, properties.value, provider)
        gen.writeProperty(VARIABLE_NAME, properties.variableName, provider)
    }

    private companion object {
        val INDEX = PropertyName("index")
        val VALUE = PropertyName("value")
        val VARIABLE_NAME = PropertyName("variable_name")
    }
}
