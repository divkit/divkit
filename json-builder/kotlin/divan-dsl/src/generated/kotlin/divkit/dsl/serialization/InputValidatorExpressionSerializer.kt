package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.InputValidatorExpression

@Generated
internal class InputValidatorExpressionSerializer : EntitySerializer<InputValidatorExpression>(InputValidatorExpression::class.java, "expression") {
    override fun writeFields(value: InputValidatorExpression, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ALLOW_EMPTY, properties.allowEmpty, provider)
        gen.writeProperty(CONDITION, properties.condition, provider)
        gen.writeProperty(LABEL_ID, properties.labelId, provider)
        gen.writeProperty(VARIABLE, properties.variable, provider)
    }

    private companion object {
        val ALLOW_EMPTY = PropertyName("allow_empty")
        val CONDITION = PropertyName("condition")
        val LABEL_ID = PropertyName("label_id")
        val VARIABLE = PropertyName("variable")
    }
}
