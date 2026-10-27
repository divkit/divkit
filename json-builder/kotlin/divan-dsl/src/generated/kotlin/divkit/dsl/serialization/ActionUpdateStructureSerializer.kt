package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionUpdateStructure

@Generated
internal class ActionUpdateStructureSerializer : EntitySerializer<ActionUpdateStructure>(ActionUpdateStructure::class.java, "update_structure") {
    override fun writeFields(value: ActionUpdateStructure, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PATH, properties.path, provider)
        gen.writeProperty(VALUE, properties.value, provider)
        gen.writeProperty(VARIABLE_NAME, properties.variableName, provider)
    }

    private companion object {
        val PATH = PropertyName("path")
        val VALUE = PropertyName("value")
        val VARIABLE_NAME = PropertyName("variable_name")
    }
}
