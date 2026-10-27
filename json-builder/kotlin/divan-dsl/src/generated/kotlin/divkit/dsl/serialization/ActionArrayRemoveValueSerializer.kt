package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionArrayRemoveValue

@Generated
internal class ActionArrayRemoveValueSerializer : EntitySerializer<ActionArrayRemoveValue>(ActionArrayRemoveValue::class.java, "array_remove_value") {
    override fun writeFields(value: ActionArrayRemoveValue, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(INDEX, properties.index, provider)
        gen.writeProperty(VARIABLE_NAME, properties.variableName, provider)
    }

    private companion object {
        val INDEX = PropertyName("index")
        val VARIABLE_NAME = PropertyName("variable_name")
    }
}
