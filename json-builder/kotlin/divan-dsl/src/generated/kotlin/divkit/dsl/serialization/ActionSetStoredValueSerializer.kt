package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionSetStoredValue

@Generated
internal class ActionSetStoredValueSerializer : EntitySerializer<ActionSetStoredValue>(ActionSetStoredValue::class.java, "set_stored_value") {
    override fun writeFields(value: ActionSetStoredValue, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(LIFETIME, properties.lifetime, provider)
        gen.writeProperty(NAME, properties.name, provider)
        gen.writeProperty(SCOPE, properties.scope, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val LIFETIME = PropertyName("lifetime")
        val NAME = PropertyName("name")
        val SCOPE = PropertyName("scope")
        val VALUE = PropertyName("value")
    }
}
