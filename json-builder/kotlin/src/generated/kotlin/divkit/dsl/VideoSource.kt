@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Can be created using the method [videoSource].
 * 
 * Required parameters: `url, type, mime_type`.
 */
@Generated
class VideoSource internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "video_source")
    )

    operator fun plus(additive: Properties): VideoSource = VideoSource(
        Properties(
            bitrate = additive.bitrate ?: properties.bitrate,
            mimeType = additive.mimeType ?: properties.mimeType,
            resolution = additive.resolution ?: properties.resolution,
            url = additive.url ?: properties.url,
        )
    )

    class Properties internal constructor(
        /**
         * Properties of the media file that determines the data transfer rate in the video stream. Bitrate is measured in kilobits per second (kbps) and indicates how much data is transmitted per unit of time.
         */
        val bitrate: Property<Int>?,
        /**
         * The property defines the MIME type (Multipurpose Internet Mail Extensions) for the media file. A MIME type is a string that indicates the type of file content and is used to determine the type of file and its correct processing.
         */
        val mimeType: Property<String>?,
        /**
         * Media file Resolution.
         */
        val resolution: Property<Resolution>?,
        /**
         * The property contains a link to a media file available for playback or download.
         */
        val url: Property<Url>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("bitrate", bitrate)
            result.tryPutProperty("mime_type", mimeType)
            result.tryPutProperty("resolution", resolution)
            result.tryPutProperty("url", url)
            return result
        }
    }

    /**
     * Media file Resolution.
     * 
     * Can be created using the method [videoSourceResolution].
     * 
     * Required parameters: `width, type, height`.
     */
    @Generated
    class Resolution internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
            mapOf("type" to "resolution")
        )

        operator fun plus(additive: Properties): Resolution = Resolution(
            Properties(
                height = additive.height ?: properties.height,
                width = additive.width ?: properties.width,
            )
        )

        class Properties internal constructor(
            /**
             * Contains information about the resolution height of the Media file.
             */
            val height: Property<Int>?,
            /**
             * Contains information about the resolution width of the video file.
             */
            val width: Property<Int>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("height", height)
                result.tryPutProperty("width", width)
                return result
            }
        }
    }

}

/**
 * @param bitrate Properties of the media file that determines the data transfer rate in the video stream. Bitrate is measured in kilobits per second (kbps) and indicates how much data is transmitted per unit of time.
 * @param mimeType The property defines the MIME type (Multipurpose Internet Mail Extensions) for the media file. A MIME type is a string that indicates the type of file content and is used to determine the type of file and its correct processing.
 * @param resolution Media file Resolution.
 * @param url The property contains a link to a media file available for playback or download.
 */
@Generated
fun DivScope.videoSource(
    `use named arguments`: Guard = Guard.instance,
    bitrate: Int? = null,
    mimeType: String? = null,
    resolution: VideoSource.Resolution? = null,
    url: Url? = null,
): VideoSource = VideoSource(
    VideoSource.Properties(
        bitrate = valueOrNull(bitrate),
        mimeType = valueOrNull(mimeType),
        resolution = valueOrNull(resolution),
        url = valueOrNull(url),
    )
)

/**
 * @param bitrate Properties of the media file that determines the data transfer rate in the video stream. Bitrate is measured in kilobits per second (kbps) and indicates how much data is transmitted per unit of time.
 * @param mimeType The property defines the MIME type (Multipurpose Internet Mail Extensions) for the media file. A MIME type is a string that indicates the type of file content and is used to determine the type of file and its correct processing.
 * @param resolution Media file Resolution.
 * @param url The property contains a link to a media file available for playback or download.
 */
@Generated
fun DivScope.videoSourceProps(
    `use named arguments`: Guard = Guard.instance,
    bitrate: Int? = null,
    mimeType: String? = null,
    resolution: VideoSource.Resolution? = null,
    url: Url? = null,
) = VideoSource.Properties(
    bitrate = valueOrNull(bitrate),
    mimeType = valueOrNull(mimeType),
    resolution = valueOrNull(resolution),
    url = valueOrNull(url),
)

/**
 * @param bitrate Properties of the media file that determines the data transfer rate in the video stream. Bitrate is measured in kilobits per second (kbps) and indicates how much data is transmitted per unit of time.
 * @param mimeType The property defines the MIME type (Multipurpose Internet Mail Extensions) for the media file. A MIME type is a string that indicates the type of file content and is used to determine the type of file and its correct processing.
 * @param resolution Media file Resolution.
 * @param url The property contains a link to a media file available for playback or download.
 */
@Generated
fun TemplateScope.videoSourceRefs(
    `use named arguments`: Guard = Guard.instance,
    bitrate: ReferenceProperty<Int>? = null,
    mimeType: ReferenceProperty<String>? = null,
    resolution: ReferenceProperty<VideoSource.Resolution>? = null,
    url: ReferenceProperty<Url>? = null,
) = VideoSource.Properties(
    bitrate = bitrate,
    mimeType = mimeType,
    resolution = resolution,
    url = url,
)

/**
 * @param bitrate Properties of the media file that determines the data transfer rate in the video stream. Bitrate is measured in kilobits per second (kbps) and indicates how much data is transmitted per unit of time.
 * @param mimeType The property defines the MIME type (Multipurpose Internet Mail Extensions) for the media file. A MIME type is a string that indicates the type of file content and is used to determine the type of file and its correct processing.
 * @param resolution Media file Resolution.
 * @param url The property contains a link to a media file available for playback or download.
 */
@Generated
fun VideoSource.override(
    `use named arguments`: Guard = Guard.instance,
    bitrate: Int? = null,
    mimeType: String? = null,
    resolution: VideoSource.Resolution? = null,
    url: Url? = null,
): VideoSource = VideoSource(
    VideoSource.Properties(
        bitrate = valueOrNull(bitrate) ?: properties.bitrate,
        mimeType = valueOrNull(mimeType) ?: properties.mimeType,
        resolution = valueOrNull(resolution) ?: properties.resolution,
        url = valueOrNull(url) ?: properties.url,
    )
)

/**
 * @param bitrate Properties of the media file that determines the data transfer rate in the video stream. Bitrate is measured in kilobits per second (kbps) and indicates how much data is transmitted per unit of time.
 * @param mimeType The property defines the MIME type (Multipurpose Internet Mail Extensions) for the media file. A MIME type is a string that indicates the type of file content and is used to determine the type of file and its correct processing.
 * @param resolution Media file Resolution.
 * @param url The property contains a link to a media file available for playback or download.
 */
@Generated
fun VideoSource.defer(
    `use named arguments`: Guard = Guard.instance,
    bitrate: ReferenceProperty<Int>? = null,
    mimeType: ReferenceProperty<String>? = null,
    resolution: ReferenceProperty<VideoSource.Resolution>? = null,
    url: ReferenceProperty<Url>? = null,
): VideoSource = VideoSource(
    VideoSource.Properties(
        bitrate = bitrate ?: properties.bitrate,
        mimeType = mimeType ?: properties.mimeType,
        resolution = resolution ?: properties.resolution,
        url = url ?: properties.url,
    )
)

/**
 * @param bitrate Properties of the media file that determines the data transfer rate in the video stream. Bitrate is measured in kilobits per second (kbps) and indicates how much data is transmitted per unit of time.
 * @param mimeType The property defines the MIME type (Multipurpose Internet Mail Extensions) for the media file. A MIME type is a string that indicates the type of file content and is used to determine the type of file and its correct processing.
 * @param url The property contains a link to a media file available for playback or download.
 */
@Generated
fun VideoSource.evaluate(
    `use named arguments`: Guard = Guard.instance,
    bitrate: ExpressionProperty<Int>? = null,
    mimeType: ExpressionProperty<String>? = null,
    url: ExpressionProperty<Url>? = null,
): VideoSource = VideoSource(
    VideoSource.Properties(
        bitrate = bitrate ?: properties.bitrate,
        mimeType = mimeType ?: properties.mimeType,
        resolution = properties.resolution,
        url = url ?: properties.url,
    )
)

@Generated
fun VideoSource.asList() = listOf(this)

/**
 * @param height Contains information about the resolution height of the Media file.
 * @param width Contains information about the resolution width of the video file.
 */
@Generated
fun DivScope.videoSourceResolution(
    `use named arguments`: Guard = Guard.instance,
    height: Int? = null,
    width: Int? = null,
): VideoSource.Resolution = VideoSource.Resolution(
    VideoSource.Resolution.Properties(
        height = valueOrNull(height),
        width = valueOrNull(width),
    )
)

/**
 * @param height Contains information about the resolution height of the Media file.
 * @param width Contains information about the resolution width of the video file.
 */
@Generated
fun DivScope.videoSourceResolutionProps(
    `use named arguments`: Guard = Guard.instance,
    height: Int? = null,
    width: Int? = null,
) = VideoSource.Resolution.Properties(
    height = valueOrNull(height),
    width = valueOrNull(width),
)

/**
 * @param height Contains information about the resolution height of the Media file.
 * @param width Contains information about the resolution width of the video file.
 */
@Generated
fun TemplateScope.videoSourceResolutionRefs(
    `use named arguments`: Guard = Guard.instance,
    height: ReferenceProperty<Int>? = null,
    width: ReferenceProperty<Int>? = null,
) = VideoSource.Resolution.Properties(
    height = height,
    width = width,
)

/**
 * @param height Contains information about the resolution height of the Media file.
 * @param width Contains information about the resolution width of the video file.
 */
@Generated
fun VideoSource.Resolution.override(
    `use named arguments`: Guard = Guard.instance,
    height: Int? = null,
    width: Int? = null,
): VideoSource.Resolution = VideoSource.Resolution(
    VideoSource.Resolution.Properties(
        height = valueOrNull(height) ?: properties.height,
        width = valueOrNull(width) ?: properties.width,
    )
)

/**
 * @param height Contains information about the resolution height of the Media file.
 * @param width Contains information about the resolution width of the video file.
 */
@Generated
fun VideoSource.Resolution.defer(
    `use named arguments`: Guard = Guard.instance,
    height: ReferenceProperty<Int>? = null,
    width: ReferenceProperty<Int>? = null,
): VideoSource.Resolution = VideoSource.Resolution(
    VideoSource.Resolution.Properties(
        height = height ?: properties.height,
        width = width ?: properties.width,
    )
)

/**
 * @param height Contains information about the resolution height of the Media file.
 * @param width Contains information about the resolution width of the video file.
 */
@Generated
fun VideoSource.Resolution.evaluate(
    `use named arguments`: Guard = Guard.instance,
    height: ExpressionProperty<Int>? = null,
    width: ExpressionProperty<Int>? = null,
): VideoSource.Resolution = VideoSource.Resolution(
    VideoSource.Resolution.Properties(
        height = height ?: properties.height,
        width = width ?: properties.width,
    )
)

@Generated
fun VideoSource.Resolution.asList() = listOf(this)
