package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.StrokeStyleSolid

@Generated
internal class StrokeStyleSolidSerializer : EntitySerializer<StrokeStyleSolid>(StrokeStyleSolid::class.java, "solid")
