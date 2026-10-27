package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionHaptic

@Generated
internal class ActionHapticSerializer : EntitySerializer<ActionHaptic>(ActionHaptic::class.java, "haptic") {
    override fun writeFields(value: ActionHaptic, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(FEEDBACK, properties.feedback, provider)
    }

    private companion object {
        val FEEDBACK = PropertyName("feedback")
    }
}
