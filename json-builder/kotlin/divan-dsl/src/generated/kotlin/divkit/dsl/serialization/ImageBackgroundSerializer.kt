package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ImageBackground

@Generated
internal class ImageBackgroundSerializer : EntitySerializer<ImageBackground>(ImageBackground::class.java, "image") {
    override fun writeFields(value: ImageBackground, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ALPHA, properties.alpha, provider)
        gen.writeProperty(CONTENT_ALIGNMENT_HORIZONTAL, properties.contentAlignmentHorizontal, provider)
        gen.writeProperty(CONTENT_ALIGNMENT_VERTICAL, properties.contentAlignmentVertical, provider)
        gen.writeProperty(FILTERS, properties.filters, provider)
        gen.writeProperty(IMAGE_URL, properties.imageUrl, provider)
        gen.writeProperty(PRELOAD_REQUIRED, properties.preloadRequired, provider)
        gen.writeProperty(SCALE, properties.scale, provider)
    }

    private companion object {
        val ALPHA = PropertyName("alpha")
        val CONTENT_ALIGNMENT_HORIZONTAL = PropertyName("content_alignment_horizontal")
        val CONTENT_ALIGNMENT_VERTICAL = PropertyName("content_alignment_vertical")
        val FILTERS = PropertyName("filters")
        val IMAGE_URL = PropertyName("image_url")
        val PRELOAD_REQUIRED = PropertyName("preload_required")
        val SCALE = PropertyName("scale")
    }
}
