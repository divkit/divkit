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
@ExposedCopyVisibility
data class VideoSource internal constructor(
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

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
         */
        val bitrate: Property<Int>?,
        /**
         * MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
         */
        val mimeType: Property<String>?,
        /**
         * Media file resolution.
         */
        val resolution: Property<Resolution>?,
        /**
         * Link to the media file available for playback or download.
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
     * Media file resolution.
     * 
     * Can be created using the method [videoSourceResolution].
     * 
     * Required parameters: `width, type, height`.
     */
    @Generated
    @ExposedCopyVisibility
    data class Resolution internal constructor(
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

        @ExposedCopyVisibility
        data class Properties internal constructor(
            /**
             * Media file frame height.
             */
            val height: Property<Int>?,
            /**
             * Media file frame width.
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
 * @param bitrate Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
 * @param mimeType MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
 * @param resolution Media file resolution.
 * @param url Link to the media file available for playback or download.
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
 * @param bitrate Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
 * @param mimeType MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
 * @param resolution Media file resolution.
 * @param url Link to the media file available for playback or download.
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
 * @param bitrate Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
 * @param mimeType MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
 * @param resolution Media file resolution.
 * @param url Link to the media file available for playback or download.
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
 * @param bitrate Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
 * @param mimeType MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
 * @param resolution Media file resolution.
 * @param url Link to the media file available for playback or download.
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
 * @param bitrate Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
 * @param mimeType MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
 * @param resolution Media file resolution.
 * @param url Link to the media file available for playback or download.
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
 * @param bitrate Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
 * @param mimeType MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
 * @param resolution Media file resolution.
 * @param url Link to the media file available for playback or download.
 */
@Generated
fun VideoSource.modify(
    `use named arguments`: Guard = Guard.instance,
    bitrate: Property<Int>? = null,
    mimeType: Property<String>? = null,
    resolution: Property<VideoSource.Resolution>? = null,
    url: Property<Url>? = null,
): VideoSource = VideoSource(
    VideoSource.Properties(
        bitrate = bitrate ?: properties.bitrate,
        mimeType = mimeType ?: properties.mimeType,
        resolution = resolution ?: properties.resolution,
        url = url ?: properties.url,
    )
)

/**
 * @param bitrate Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps).
 * @param mimeType MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly.
 * @param url Link to the media file available for playback or download.
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
 * @param height Media file frame height.
 * @param width Media file frame width.
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
 * @param height Media file frame height.
 * @param width Media file frame width.
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
 * @param height Media file frame height.
 * @param width Media file frame width.
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
 * @param height Media file frame height.
 * @param width Media file frame width.
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
 * @param height Media file frame height.
 * @param width Media file frame width.
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
 * @param height Media file frame height.
 * @param width Media file frame width.
 */
@Generated
fun VideoSource.Resolution.modify(
    `use named arguments`: Guard = Guard.instance,
    height: Property<Int>? = null,
    width: Property<Int>? = null,
): VideoSource.Resolution = VideoSource.Resolution(
    VideoSource.Resolution.Properties(
        height = height ?: properties.height,
        width = width ?: properties.width,
    )
)

/**
 * @param height Media file frame height.
 * @param width Media file frame width.
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
