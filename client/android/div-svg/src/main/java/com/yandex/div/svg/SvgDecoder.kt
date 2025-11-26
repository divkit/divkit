package com.yandex.div.svg

import android.graphics.RectF
import android.graphics.drawable.PictureDrawable
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.yandex.div.core.annotations.InternalApi
import java.io.InputStream

@InternalApi
public class SvgDecoder(
    private val useViewBoundsAsIntrinsicSize: Boolean = true
) {
    public fun decode(source: InputStream): PictureDrawable? {
        return try {
            val svg: SVG = SVG.getFromInputStream(source)

            val svgWidth: Float
            val svgHeight: Float
            val viewBox: RectF? = svg.documentViewBox
            if (useViewBoundsAsIntrinsicSize && viewBox != null) {
                svgWidth = viewBox.width()
                svgHeight = viewBox.height()
            } else {
                svgWidth = svg.documentWidth
                svgHeight = svg.documentHeight
            }

            if (viewBox == null && svgWidth > 0 && svgHeight > 0) {
                svg.setDocumentViewBox(0f, 0f, svgWidth, svgHeight)
            }

            PictureDrawable(svg.renderToPicture())
        } catch (ex: SVGParseException) {
            null
        }
    }
}
