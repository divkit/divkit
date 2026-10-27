package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.Component

internal class ComponentSerializer : EntitySerializer<Component<*>>(Component::class.java) {

    override fun writeFields(value: Component<*>, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeFieldName(TYPE_FIELD)
        gen.writeAny(properties[TYPE] ?: value.template.name, provider)
        for ((name, property) in properties) {
            if (name == TYPE) continue
            gen.writeFieldName(name)
            gen.writeAny(property, provider)
        }
    }

    private companion object {
        const val TYPE = "type"
    }
}
