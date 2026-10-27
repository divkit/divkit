package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionShowTooltip

@Generated
internal class ActionShowTooltipSerializer : EntitySerializer<ActionShowTooltip>(ActionShowTooltip::class.java, "show_tooltip") {
    override fun writeFields(value: ActionShowTooltip, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(MULTIPLE, properties.multiple, provider)
    }

    private companion object {
        val ID = PropertyName("id")
        val MULTIPLE = PropertyName("multiple")
    }
}
