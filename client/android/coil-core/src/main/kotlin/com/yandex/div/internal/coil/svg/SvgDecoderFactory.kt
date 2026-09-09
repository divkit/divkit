@file:OptIn(coil3.annotation.ExperimentalCoilApi::class)

package com.yandex.div.internal.coil.svg

import android.content.Context
import coil3.ComponentRegistry
import coil3.Extras
import coil3.Image
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
import coil3.svg.css
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.yandex.div.core.annotations.InternalApi
import kotlin.math.ceil
import kotlin.math.max
import coil3.svg.SvgImage as CoilSvgImage

private const val SVG_DEFAULT_SIZE = 512f

@InternalApi
fun ComponentRegistry.Builder.addSvgDecoderFactoryIfAvailable(context: Context) {
    try {
        val metrics = context.resources.displayMetrics
        add(SvgDecoderFactory(max(metrics.widthPixels, metrics.heightPixels)))
    } catch (_: LinkageError) {
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

private class SizedSvg(private val svg: SVG) : Svg {

    override val width: Float
    override val height: Float
    private val documentWidth = svg.documentWidth
    private val documentHeight = svg.documentHeight
    private val hasDocumentDimensions = documentWidth > 0f && documentWidth.isFinite() &&
        documentHeight > 0f && documentHeight.isFinite()
    private var renderOptions: RenderOptions? = null

    override var viewBox: Svg.ViewBox?
        get() = svg.documentViewBox?.let { Svg.ViewBox(it.left, it.top, it.right, it.bottom) }
        set(value) {
            // Match the View decoder: only actual document dimensions define a missing viewBox,
            // never the rounded or fallback dimensions that Coil passes here.
            if (hasDocumentDimensions) {
                requireNotNull(value)
                svg.setDocumentViewBox(value.left, value.top, documentWidth, documentHeight)
            }
        }

    init {
        val viewBox = svg.documentViewBox
        if (hasDocumentDimensions) {
            // Picture dimensions in the View decoder round up, including subpixel sizes.
            width = ceil(documentWidth)
            height = ceil(documentHeight)
        } else if (viewBox != null && (viewBox.width() <= 0f || viewBox.height() <= 0f)) {
            width = SVG_DEFAULT_SIZE
            height = SVG_DEFAULT_SIZE
        } else {
            // The dimension getters lose both dimensions when either one is relative.
            // AndroidSVG's Picture API resolves the original attributes like the View decoder,
            // including its 512 x 512 fallback when only a viewBox is supplied.
            val picture = svg.renderToPicture()
            width = picture.width.takeIf { it > 0 }?.toFloat() ?: SVG_DEFAULT_SIZE
            height = picture.height.takeIf { it > 0 }?.toFloat() ?: SVG_DEFAULT_SIZE
        }
    }

    // Keep the original relative attributes. The viewport in asImage is fixed,
    // so Coil's percentage setters must not replace the document's own dimensions.
    override fun width(value: String) = Unit

    override fun height(value: String) = Unit

    override fun options(options: Options) {
        renderOptions = options.css?.let { RenderOptions().css(it) }
    }

    override fun asImage(width: Int, height: Int): Image {
        val options = (renderOptions ?: RenderOptions()).viewPort(0f, 0f, this.width, this.height)
        val image = CoilSvgImage(svg, options, width, height)
        return SvgImage(image, this.width, this.height, width, height)
    }
}
