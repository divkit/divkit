package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionFocusElement

@Generated
internal class ActionFocusElementSerializer : EntitySerializer<ActionFocusElement>(ActionFocusElement::class.java, "focus_element") {
    override fun writeFields(value: ActionFocusElement, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ELEMENT_ID, properties.elementId, provider)
    }

    private companion object {
        val ELEMENT_ID = PropertyName("element_id")
    }
}
