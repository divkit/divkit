package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.InputValidatorBase

@Generated
internal class InputValidatorBaseSerializer : EntitySerializer<InputValidatorBase>(InputValidatorBase::class.java) {
    override fun writeFields(value: InputValidatorBase, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ALLOW_EMPTY, properties.allowEmpty, provider)
        gen.writeProperty(LABEL_ID, properties.labelId, provider)
        gen.writeProperty(VARIABLE, properties.variable, provider)
    }

    private companion object {
        val ALLOW_EMPTY = PropertyName("allow_empty")
        val LABEL_ID = PropertyName("label_id")
        val VARIABLE = PropertyName("variable")
    }
}
