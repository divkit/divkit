package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.StartDestination

@Generated
internal class StartDestinationSerializer : EntitySerializer<StartDestination>(StartDestination::class.java, "start")
