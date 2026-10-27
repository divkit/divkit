package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.InfinityCount

@Generated
internal class InfinityCountSerializer : EntitySerializer<InfinityCount>(InfinityCount::class.java, "infinity")
