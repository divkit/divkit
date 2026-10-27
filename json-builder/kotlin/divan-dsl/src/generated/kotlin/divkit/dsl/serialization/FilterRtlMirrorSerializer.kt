package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.FilterRtlMirror

@Generated
internal class FilterRtlMirrorSerializer : EntitySerializer<FilterRtlMirror>(FilterRtlMirror::class.java, "rtl_mirror")
