package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithDefault

@Generated
internal class WithDefaultSerializer : EntitySerializer<WithDefault>(WithDefault::class.java, "default")
