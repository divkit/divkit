package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.StretchIndicatorItemPlacement

@Generated
internal class StretchIndicatorItemPlacementSerializer : EntitySerializer<StretchIndicatorItemPlacement>(StretchIndicatorItemPlacement::class.java, "stretch") {
    override fun writeFields(value: StretchIndicatorItemPlacement, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ITEM_SPACING, properties.itemSpacing, provider)
        gen.writeProperty(MAX_VISIBLE_ITEMS, properties.maxVisibleItems, provider)
    }

    private companion object {
        val ITEM_SPACING = PropertyName("item_spacing")
        val MAX_VISIBLE_ITEMS = PropertyName("max_visible_items")
    }
}
