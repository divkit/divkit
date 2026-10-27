package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PageSize

@Generated
internal class PageSizeSerializer : EntitySerializer<PageSize>(PageSize::class.java, "percentage") {
    override fun writeFields(value: PageSize, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(PAGE_WIDTH, properties.pageWidth, provider)
    }

    private companion object {
        val PAGE_WIDTH = PropertyName("page_width")
    }
}
