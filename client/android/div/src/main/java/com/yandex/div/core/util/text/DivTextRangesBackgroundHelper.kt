package com.yandex.div.core.util.text

import android.graphics.Canvas
import android.text.Layout
import android.text.Spanned
import android.view.View
import com.yandex.div.core.view2.divs.DivBackgroundSpan
import com.yandex.div.json.expressions.ExpressionResolver

internal class DivTextRangesBackgroundHelper(
    val view: View,
    val resolver: ExpressionResolver,
) {

    private val singleLineRenderer: DivTextRangesBackgroundRenderer by lazy {
        SingleLineRenderer(
            view = view,
            resolver = resolver
        )
    }

    private val multiLineRenderer: DivTextRangesBackgroundRenderer by lazy {
        MultiLineRenderer(
            view = view,
            resolver = resolver
        )
    }

    fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
        val spans = text.getSpans(0, text.length, DivBackgroundSpan::class.java)
        spans.forEach { span ->
            if (span.cache == null) {
                val spanStart = text.getSpanStart(span)
                val spanEnd = text.getSpanEnd(span)
                val startLine = layout.getLineForOffset(spanStart)
                val endLine = layout.getLineForOffset(spanEnd)

                //TODO: DIVKIT-895 edubinskaya, parse horizontal paddings from background or anything from schema.
                val horizontalPadding = 0
                val startOffset = (layout.getPrimaryHorizontal(spanStart)
                    + -1 * layout.getParagraphDirection(startLine) * horizontalPadding).toInt()
                val endOffset = (layout.getPrimaryHorizontal(spanEnd)
                    + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

                val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
                renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset,
                    span.border, span.background)
                span.cache = DivBackgroundSpan.Cache(startLine, endLine, startOffset, endOffset)
            } else {
                span.cache?.apply {
                    val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
                    renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset,
                        span.border, span.background)
                }
            }
        }
    }
}
