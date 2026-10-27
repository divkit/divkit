package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionDictSetValue

@Generated
internal class ActionDictSetValueSerializer : EntitySerializer<ActionDictSetValue>(ActionDictSetValue::class.java, "dict_set_value") {
    override fun writeFields(value: ActionDictSetValue, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(KEY, properties.key, provider)
        gen.writeProperty(VALUE, properties.value, provider)
        gen.writeProperty(VARIABLE_NAME, properties.variableName, provider)
    }

    private companion object {
        val KEY = PropertyName("key")
        val VALUE = PropertyName("value")
        val VARIABLE_NAME = PropertyName("variable_name")
    }
}
