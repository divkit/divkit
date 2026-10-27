package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.SerializableString
import com.fasterxml.jackson.core.io.SerializedString
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * Base serializer of DSL entities.
 * Writes JSON object with static `type` field (if any) followed by fields written by [writeFields].
 */
internal abstract class EntitySerializer<T : Any>(
    type: Class<*>,
    staticType: String? = null,
) : StdSerializer<T>(type, false) {

    private val staticType: SerializableString? = staticType?.let(::SerializedString)

    final override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject(value)
        writeObjectContent(value, gen, provider)
        gen.writeEndObject()
    }

    final override fun serializeWithType(
        value: T,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer
    ) {
        val typeId = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT))
        writeObjectContent(value, gen, provider)
        typeSer.writeTypeSuffix(gen, typeId)
    }

    private fun writeObjectContent(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        if (staticType != null) {
            gen.writeFieldName(TYPE_FIELD)
            gen.writeString(staticType)
        }
        writeFields(value, gen, provider)
    }

    protected open fun writeFields(value: T, gen: JsonGenerator, provider: SerializerProvider) = Unit
}

internal val TYPE_FIELD: SerializableString = SerializedString("type")
