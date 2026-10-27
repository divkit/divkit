package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.NinePatchBackground

@Generated
internal class NinePatchBackgroundSerializer : EntitySerializer<NinePatchBackground>(NinePatchBackground::class.java, "nine_patch_image") {
    override fun writeFields(value: NinePatchBackground, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(IMAGE_URL, properties.imageUrl, provider)
        gen.writeProperty(INSETS, properties.insets, provider)
    }

    private companion object {
        val IMAGE_URL = PropertyName("image_url")
        val INSETS = PropertyName("insets")
    }
}
