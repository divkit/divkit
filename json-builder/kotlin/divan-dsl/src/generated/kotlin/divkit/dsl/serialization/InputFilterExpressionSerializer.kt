package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.InputFilterExpression

@Generated
internal class InputFilterExpressionSerializer : EntitySerializer<InputFilterExpression>(InputFilterExpression::class.java, "expression") {
    override fun writeFields(value: InputFilterExpression, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(CONDITION, properties.condition, provider)
    }

    private companion object {
        val CONDITION = PropertyName("condition")
    }
}
