@file:OptIn(coil3.annotation.ExperimentalCoilApi::class)

package com.yandex.div.internal.coil.svg

import coil3.Image
import coil3.request.Options
import coil3.svg.Svg
import coil3.svg.css
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import kotlin.math.ceil
import coil3.svg.SvgImage as CoilSvgImage

private const val SVG_DEFAULT_SIZE = 512f

internal class SizedSvg(private val svg: SVG) : Svg {

    private val documentWidth = svg.documentWidth
    private val documentHeight = svg.documentHeight

    private val hasDocumentDimensions = documentWidth > 0f && documentWidth.isFinite() &&
            documentHeight > 0f && documentHeight.isFinite()

    private var renderOptions: RenderOptions? = null

    override val width: Float
    override val height: Float

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
