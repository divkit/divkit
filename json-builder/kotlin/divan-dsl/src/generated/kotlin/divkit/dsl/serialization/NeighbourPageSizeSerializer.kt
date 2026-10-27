package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.NeighbourPageSize

@Generated
internal class NeighbourPageSizeSerializer : EntitySerializer<NeighbourPageSize>(NeighbourPageSize::class.java, "fixed") {
    override fun writeFields(value: NeighbourPageSize, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(NEIGHBOUR_PAGE_WIDTH, properties.neighbourPageWidth, provider)
    }

    private companion object {
        val NEIGHBOUR_PAGE_WIDTH = PropertyName("neighbour_page_width")
    }
}
