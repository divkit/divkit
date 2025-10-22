package com.yandex.div.core.util.text

import android.graphics.Canvas
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.view.View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivTextRangeBackground

internal class DivTextRangesBackgroundHelper(
    val view: View,
    val resolver: ExpressionResolver,
) {

    private val spans = ArrayList<DivBackgroundSpan>()
    private val ellipsisSpans = ArrayList<DivBackgroundSpan>()
    internal fun invalidateSpansCache(inEllipsis: Boolean) {
        if (inEllipsis) {
            ellipsisSpans.clear()
        } else {
            spans.clear()
        }
    }
    internal fun addBackgroundSpan(span: DivBackgroundSpan, inEllipsis: Boolean) {
        if (inEllipsis) {
            ellipsisSpans.add(span)
        } else {
            spans.add(span)
        }
    }
    internal fun hasBackgroundSpan(): Boolean = spans.isNotEmpty() || ellipsisSpans.isNotEmpty()
    internal fun hasSameSpan(text: CharSequence, backgroundSpan: DivBackgroundSpan, start: Int, end: Int): Boolean {
        val spannedText = text as? Spannable ?: return false
        return spans.any { span ->
            span.border == backgroundSpan.border && span.background == backgroundSpan.background
                && end == spannedText.getSpanEnd(span) && start == spannedText.getSpanStart(span)
        }
    }

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

    private val cloudBackgroundRenderer: CloudTextRangeBackgroundRenderer by lazy {
        CloudTextRangeBackgroundRenderer(
            context = view.context,
            expressionResolver = resolver
        )
    }

    fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
        spans.forEach { applySpan(it, canvas, text, layout) }
        ellipsisSpans.forEach { applySpan(it, canvas, text, layout) }
    }

    private fun applySpan(span: DivBackgroundSpan, canvas: Canvas, text: Spanned, layout: Layout) {
        val spanStart = text.getSpanStart(span)
        val spanEnd = text.getSpanEnd(span)
        val startLine = layout.getLineForOffset(spanStart)
        val endLine = layout.getLineForOffset(spanEnd)

        val startOffset = layout.getPrimaryHorizontal(spanStart).toInt()
        val endOffset = layout.getPrimaryHorizontal(spanEnd).toInt()

        when (span.background) {
            is DivTextRangeBackground.Cloud -> {
                cloudBackgroundRenderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset,
                    span.border, span.background)
            }

            else -> {
                val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
                renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset,
                    span.border, span.background)
            }
        }
    }
}
