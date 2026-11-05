package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.AccessibilityMenuItem

@Generated
internal class AccessibilityMenuItemSerializer : EntitySerializer<AccessibilityMenuItem>(AccessibilityMenuItem::class.java) {
    override fun writeFields(value: AccessibilityMenuItem, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACTIONS, properties.actions, provider)
        gen.writeProperty(LABEL, properties.label, provider)
    }

    private companion object {
        val ACTIONS = PropertyName("actions")
        val LABEL = PropertyName("label")
    }
}
