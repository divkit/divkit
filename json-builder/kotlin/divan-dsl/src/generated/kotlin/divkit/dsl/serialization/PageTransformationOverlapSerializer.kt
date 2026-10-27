package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.PageTransformationOverlap

@Generated
internal class PageTransformationOverlapSerializer : EntitySerializer<PageTransformationOverlap>(PageTransformationOverlap::class.java, "overlap") {
    override fun writeFields(value: PageTransformationOverlap, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(INTERPOLATOR, properties.interpolator, provider)
        gen.writeProperty(NEXT_PAGE_ALPHA, properties.nextPageAlpha, provider)
        gen.writeProperty(NEXT_PAGE_SCALE, properties.nextPageScale, provider)
        gen.writeProperty(PREVIOUS_PAGE_ALPHA, properties.previousPageAlpha, provider)
        gen.writeProperty(PREVIOUS_PAGE_SCALE, properties.previousPageScale, provider)
        gen.writeProperty(REVERSED_STACKING_ORDER, properties.reversedStackingOrder, provider)
    }

    private companion object {
        val INTERPOLATOR = PropertyName("interpolator")
        val NEXT_PAGE_ALPHA = PropertyName("next_page_alpha")
        val NEXT_PAGE_SCALE = PropertyName("next_page_scale")
        val PREVIOUS_PAGE_ALPHA = PropertyName("previous_page_alpha")
        val PREVIOUS_PAGE_SCALE = PropertyName("previous_page_scale")
        val REVERSED_STACKING_ORDER = PropertyName("reversed_stacking_order")
    }
}
