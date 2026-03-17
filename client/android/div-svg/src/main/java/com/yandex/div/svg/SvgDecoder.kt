package com.yandex.div.svg

import android.graphics.drawable.PictureDrawable
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.internal.KLog
import java.io.InputStream

@InternalApi
public object SvgDecoder {

    public fun isSvg(imageUrl: String): Boolean {
        val queryStartIndex = imageUrl.indexOf('?')
        val pathEndIndex = if (queryStartIndex < 0) imageUrl.length else queryStartIndex
        return imageUrl.substring(0, pathEndIndex).endsWith(".svg")
    }

    public fun decode(source: InputStream): PictureDrawable? {
        val svg = try {
            SVG.getFromInputStream(source)
        } catch (e: SVGParseException) {
            KLog.e(TAG) { e.toString() }
            return null
        }

        if (svg.documentViewBox != null) return svg.toDrawable()

        val svgWidth = svg.documentWidth
        val svgHeight = svg.documentHeight
        if (svgWidth > 0 && svgHeight > 0) {
            svg.setDocumentViewBox(0f, 0f, svgWidth, svgHeight)
        }

        return svg.toDrawable()
    }

    private fun SVG.toDrawable() = PictureDrawable(renderToPicture())
}

private const val TAG = "SvgDecoder"
