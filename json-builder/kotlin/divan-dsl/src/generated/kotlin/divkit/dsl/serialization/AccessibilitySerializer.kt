package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Accessibility

@Generated
internal class AccessibilitySerializer : EntitySerializer<Accessibility>(Accessibility::class.java) {
    override fun writeFields(value: Accessibility, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DESCRIPTION, properties.description, provider)
        gen.writeProperty(HINT, properties.hint, provider)
        gen.writeProperty(IS_CHECKED, properties.isChecked, provider)
        gen.writeProperty(MODE, properties.mode, provider)
        gen.writeProperty(MUTE_AFTER_ACTION, properties.muteAfterAction, provider)
        gen.writeProperty(STATE_DESCRIPTION, properties.stateDescription, provider)
        gen.writeProperty(TYPE, properties.type, provider)
    }

    private companion object {
        val DESCRIPTION = PropertyName("description")
        val HINT = PropertyName("hint")
        val IS_CHECKED = PropertyName("is_checked")
        val MODE = PropertyName("mode")
        val MUTE_AFTER_ACTION = PropertyName("mute_after_action")
        val STATE_DESCRIPTION = PropertyName("state_description")
        val TYPE = PropertyName("type")
    }
}
