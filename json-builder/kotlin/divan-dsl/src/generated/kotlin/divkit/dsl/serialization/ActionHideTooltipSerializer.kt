package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionHideTooltip

@Generated
internal class ActionHideTooltipSerializer : EntitySerializer<ActionHideTooltip>(ActionHideTooltip::class.java, "hide_tooltip") {
    override fun writeFields(value: ActionHideTooltip, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ID, properties.id, provider)
    }

    private companion object {
        val ID = PropertyName("id")
    }
}
