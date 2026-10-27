package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionExtensionAction

@Generated
internal class ActionExtensionActionSerializer : EntitySerializer<ActionExtensionAction>(ActionExtensionAction::class.java, "extension_action") {
    override fun writeFields(value: ActionExtensionAction, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DIV_ID, properties.divId, provider)
        gen.writeProperty(EXTENSION_ID, properties.extensionId, provider)
    }

    private companion object {
        val DIV_ID = PropertyName("div_id")
        val EXTENSION_ID = PropertyName("extension_id")
    }
}
