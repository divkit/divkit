package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.StrokeStyleDashed

@Generated
internal class StrokeStyleDashedSerializer : EntitySerializer<StrokeStyleDashed>(StrokeStyleDashed::class.java, "dashed")
