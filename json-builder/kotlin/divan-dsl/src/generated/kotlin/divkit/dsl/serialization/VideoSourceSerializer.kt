package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.VideoSource

@Generated
internal class VideoSourceSerializer : EntitySerializer<VideoSource>(VideoSource::class.java, "video_source") {
    override fun writeFields(value: VideoSource, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(BITRATE, properties.bitrate, provider)
        gen.writeProperty(MIME_TYPE, properties.mimeType, provider)
        gen.writeProperty(RESOLUTION, properties.resolution, provider)
        gen.writeProperty(URL, properties.url, provider)
    }

    private companion object {
        val BITRATE = PropertyName("bitrate")
        val MIME_TYPE = PropertyName("mime_type")
        val RESOLUTION = PropertyName("resolution")
        val URL = PropertyName("url")
    }
}

@Generated
internal class VideoSourceResolutionSerializer : EntitySerializer<VideoSource.Resolution>(VideoSource.Resolution::class.java, "resolution") {
    override fun writeFields(value: VideoSource.Resolution, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(HEIGHT, properties.height, provider)
        gen.writeProperty(WIDTH, properties.width, provider)
    }

    private companion object {
        val HEIGHT = PropertyName("height")
        val WIDTH = PropertyName("width")
    }
}
