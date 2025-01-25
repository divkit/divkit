package com.yandex.div.core.util.text

import android.graphics.Canvas
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivTextRangeBackground

internal class DivTextRangesBackgroundHelper(
    val view: View,
    val resolver: ExpressionResolver,
) {

    private var spans = ArrayList<DivBackgroundSpan>()
    internal fun invalidateSpansCache() = spans.clear()
    internal fun addBackgroundSpan(span: DivBackgroundSpan) = spans.add(span)
    internal fun hasBackgroundSpan(): Boolean = spans.isNotEmpty()
    internal fun hasSameSpan(spannable: SpannableStringBuilder, backgroundSpan: DivBackgroundSpan, start: Int, end: Int): Boolean {
        return spans.any { span ->
            span.border == backgroundSpan.border && span.background == backgroundSpan.background
                && end == spannable.getSpanEnd(span) && start == spannable.getSpanStart(span)
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
        spans.forEach { span ->
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
}
