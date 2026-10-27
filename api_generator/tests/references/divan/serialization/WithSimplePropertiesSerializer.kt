package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithSimpleProperties

@Generated
internal class WithSimplePropertiesSerializer : EntitySerializer<WithSimpleProperties>(WithSimpleProperties::class.java, "entity_with_simple_properties") {
    override fun writeFields(value: WithSimpleProperties, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(BOOLEAN, properties.boolean, provider)
        gen.writeProperty(BOOLEAN_INT, properties.booleanInt, provider)
        gen.writeProperty(COLOR, properties.color, provider)
        gen.writeProperty(DOUBLE, properties.double, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(INTEGER, properties.integer, provider)
        gen.writeProperty(POSITIVE_INTEGER, properties.positiveInteger, provider)
        gen.writeProperty(STRING, properties.string, provider)
        gen.writeProperty(URL, properties.url, provider)
    }

    private companion object {
        val BOOLEAN = PropertyName("boolean")
        val BOOLEAN_INT = PropertyName("boolean_int")
        val COLOR = PropertyName("color")
        val DOUBLE = PropertyName("double")
        val ID = PropertyName("id")
        val INTEGER = PropertyName("integer")
        val POSITIVE_INTEGER = PropertyName("positive_integer")
        val STRING = PropertyName("string")
        val URL = PropertyName("url")
    }
}
