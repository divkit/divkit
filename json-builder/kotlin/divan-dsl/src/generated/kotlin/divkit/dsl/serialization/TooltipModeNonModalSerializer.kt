package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.TooltipModeNonModal

@Generated
internal class TooltipModeNonModalSerializer : EntitySerializer<TooltipModeNonModal>(TooltipModeNonModal::class.java, "non_modal")
