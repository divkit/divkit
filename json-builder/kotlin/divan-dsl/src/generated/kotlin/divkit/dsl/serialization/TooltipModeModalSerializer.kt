package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.TooltipModeModal

@Generated
internal class TooltipModeModalSerializer : EntitySerializer<TooltipModeModal>(TooltipModeModal::class.java, "modal")
