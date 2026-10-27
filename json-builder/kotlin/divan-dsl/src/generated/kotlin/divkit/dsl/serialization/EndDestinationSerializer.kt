package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.EndDestination

@Generated
internal class EndDestinationSerializer : EntitySerializer<EndDestination>(EndDestination::class.java, "end")
