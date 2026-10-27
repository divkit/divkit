package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionCustom

@Generated
internal class ActionCustomSerializer : EntitySerializer<ActionCustom>(ActionCustom::class.java, "custom")
