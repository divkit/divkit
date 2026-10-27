package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.SerializableString
import com.fasterxml.jackson.core.io.SerializedString
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.Color
import divkit.dsl.EnumValue
import divkit.dsl.Url
import divkit.dsl.core.ExpressionArrayElement
import divkit.dsl.core.ExpressionProperty
import divkit.dsl.core.LiteralArrayElement
import divkit.dsl.core.LiteralProperty
import divkit.dsl.core.Property
import divkit.dsl.core.ReferenceProperty

/**
 * Pre-encoded JSON names of a property: for literal/expression values
 * and for template references (prefixed with `$`).
 */
internal class PropertyName(name: String) {
    val literal: SerializableString = SerializedString(name)
    val reference: SerializableString = SerializedString("$$name")
}

/**
 * Writes property as a JSON field. Does nothing if [property] is `null`.
 */
internal fun JsonGenerator.writeProperty(
    name: PropertyName,
    property: Property<*>?,
    provider: SerializerProvider
) {
    when (property) {
        null -> Unit
        is LiteralProperty -> {
            writeFieldName(name.literal)
            writeLiteral(property.value, provider)
        }
        is ReferenceProperty -> {
            writeFieldName(name.reference)
            writeString(property.name)
        }
        is ExpressionProperty -> {
            writeFieldName(name.literal)
            writeString(property.expression)
        }
    }
}

/**
 * Writes an arbitrary value. Values of unknown types are delegated to Jackson.
 */
internal fun JsonGenerator.writeAny(value: Any?, provider: SerializerProvider) {
    when (value) {
        null -> writeNull()
        is String -> writeString(value)
        is EnumValue -> writeString(value.serialized)
        is Int -> writeNumber(value)
        is Double -> writeNumber(value)
        is Long -> writeNumber(value)
        is Boolean -> writeBoolean(value)
        is Color -> writeString(value.value)
        is Url -> writeString(value.value)
        is List<*> -> {
            writeStartArray()
            for (item in value) {
                writeAny(item, provider)
            }
            writeEndArray()
        }
        else -> provider.defaultSerializeValue(value, this)
    }
}

private fun JsonGenerator.writeLiteral(value: Any?, provider: SerializerProvider) {
    when (value) {
        is Boolean -> writeNumber(if (value) 1 else 0)
        is List<*> -> {
            writeStartArray()
            for (item in value) {
                when (item) {
                    is LiteralArrayElement<*> -> writeAny(item.value, provider)
                    is ExpressionArrayElement<*> -> writeString(item.expression)
                    else -> writeAny(item, provider)
                }
            }
            writeEndArray()
        }
        else -> writeAny(value, provider)
    }
}
