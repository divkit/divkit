package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithoutDefault

@Generated
internal class WithoutDefaultSerializer : EntitySerializer<WithoutDefault>(WithoutDefault::class.java, "non_default")
