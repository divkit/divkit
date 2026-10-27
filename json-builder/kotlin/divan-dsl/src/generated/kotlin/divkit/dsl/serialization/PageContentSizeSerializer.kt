package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PageContentSize

@Generated
internal class PageContentSizeSerializer : EntitySerializer<PageContentSize>(PageContentSize::class.java, "wrap_content")
