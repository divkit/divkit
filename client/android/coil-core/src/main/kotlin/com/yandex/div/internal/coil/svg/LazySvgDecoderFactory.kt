@file:OptIn(coil3.annotation.ExperimentalCoilApi::class)

package com.yandex.div.internal.coil.svg

import coil3.Extras
import coil3.ImageLoader
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.maxBitmapSize
import coil3.request.transformations
import coil3.size.Size
import coil3.size.pxOrElse
import coil3.svg.Svg
import coil3.svg.SvgDecoder
import com.caverock.androidsvg.SVG
import com.yandex.div.core.annotations.InternalApi

@InternalApi
class LazySvgDecoderFactory(maxDisplaySize: Int) : Decoder.Factory {

    private val delegate by lazy { SvgDecoderFactoryProvider.createIfAvailable(maxDisplaySize) }

    override fun create(
        result: SourceFetchResult,
        options: Options,
        imageLoader: ImageLoader,
    ): Decoder? = delegate?.create(result, options, imageLoader)
}

// Keep this factory's initialization out of image-loader construction after R8 optimization.
private object SvgDecoderFactoryProvider {

    fun createIfAvailable(maxDisplaySize: Int): Decoder.Factory? {
        return try {
            SvgDecoderFactory(maxDisplaySize)
        } catch (_: LinkageError) {
            null
        }
    }
}

private class SvgDecoderFactory(private val maxDisplaySize: Int) : Decoder.Factory {

    private val parser = Svg.Parser { SizedSvg(SVG.getFromInputStream(it.inputStream())) }

    private val vectorDecoderFactory = SvgDecoder.Factory(
        parser = parser,
        useViewBoundsAsIntrinsicSize = false,
        renderToBitmap = false,
    )

    private val bitmapDecoderFactory = SvgDecoder.Factory(
        parser = parser,
        useViewBoundsAsIntrinsicSize = false,
        renderToBitmap = true,
    )

    override fun create(
        result: SourceFetchResult,
        options: Options,
        imageLoader: ImageLoader,
    ): Decoder? {
        if (options.transformations.isEmpty()) {
            // Vectors retain their intrinsic size across requests and memory-cache hits.
            // Bitmap limits only apply when rasterizing for transformations below.
            val extras = options.extras.newBuilder()
                .set(Extras.Key.maxBitmapSize, Size.ORIGINAL)
                .build()
            return vectorDecoderFactory.create(
                result,
                options.copy(size = Size.ORIGINAL, extras = extras),
                imageLoader,
            )
        }

        val maxSize = options.maxBitmapSize
        val extras = options.extras.newBuilder()
            .set(
                Extras.Key.maxBitmapSize,
                Size(
                    maxSize.width.pxOrElse { maxDisplaySize }.coerceAtMost(maxDisplaySize),
                    maxSize.height.pxOrElse { maxDisplaySize }.coerceAtMost(maxDisplaySize),
                )
            )
            .build()
        return bitmapDecoderFactory.create(result, options.copy(extras = extras), imageLoader)
    }
}
