package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Extension

@Generated
internal class ExtensionSerializer : EntitySerializer<Extension>(Extension::class.java) {
    override fun writeFields(value: Extension, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(IS_ENABLED, properties.isEnabled, provider)
        gen.writeProperty(PARAMS, properties.params, provider)
    }

    private companion object {
        val ID = PropertyName("id")
        val IS_ENABLED = PropertyName("is_enabled")
        val PARAMS = PropertyName("params")
    }
}
