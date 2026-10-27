package divan.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divan.annotation.Generated
import divan.WithoutProperties

@Generated
internal class WithoutPropertiesSerializer : EntitySerializer<WithoutProperties>(WithoutProperties::class.java, "entity_without_properties")
