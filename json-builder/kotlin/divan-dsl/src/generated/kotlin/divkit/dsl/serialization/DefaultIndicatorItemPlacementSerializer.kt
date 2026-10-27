package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.DefaultIndicatorItemPlacement

@Generated
internal class DefaultIndicatorItemPlacementSerializer : EntitySerializer<DefaultIndicatorItemPlacement>(DefaultIndicatorItemPlacement::class.java, "default") {
    override fun writeFields(value: DefaultIndicatorItemPlacement, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(SPACE_BETWEEN_CENTERS, properties.spaceBetweenCenters, provider)
    }

    private companion object {
        val SPACE_BETWEEN_CENTERS = PropertyName("space_between_centers")
    }
}
