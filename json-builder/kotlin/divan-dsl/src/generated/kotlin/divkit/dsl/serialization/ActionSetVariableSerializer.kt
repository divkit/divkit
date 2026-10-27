package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionSetVariable

@Generated
internal class ActionSetVariableSerializer : EntitySerializer<ActionSetVariable>(ActionSetVariable::class.java, "set_variable") {
    override fun writeFields(value: ActionSetVariable, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(VALUE, properties.value, provider)
        gen.writeProperty(VARIABLE_NAME, properties.variableName, provider)
    }

    private companion object {
        val VALUE = PropertyName("value")
        val VARIABLE_NAME = PropertyName("variable_name")
    }
}
