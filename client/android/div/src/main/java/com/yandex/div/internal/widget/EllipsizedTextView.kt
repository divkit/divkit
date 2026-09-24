package com.yandex.div.internal.widget

import android.content.Context
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextUtils.TruncateAt
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import androidx.annotation.VisibleForTesting
import androidx.core.widget.TextViewCompat
import com.yandex.div.R
import com.yandex.div.core.text.TextTruncation
import com.yandex.div.core.view2.spannable.LineHeightWithTopOffsetSpan
import com.yandex.div.internal.view.DrawingPassOverrideStrategy
import com.yandex.div2.DivText
import java.util.Locale

open class EllipsizedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SuperLineHeightTextView(context, attrs, defStyleAttr) {

    var ellipsis: CharSequence = DEFAULT_ELLIPSIS
        set(value) {
            onEllipsisChanged(value)
            field = value
        }

    internal var truncatePolicy: DivText.TruncatePolicy = DivText.TruncatePolicy.GRAPHEME
        set(value) {
            if (field == value) {
                return
            }
            field = value
            onEllipsisChanged(ellipsis)
        }

    internal val untruncatedLineCount: Int
        get() = untruncatedLayout?.lineCount ?: lineCount

    private var untruncatedLayout: Layout? = null

    /**
     * If text auto ellipsize is enabled.
     */
    var autoEllipsize: Boolean = false
        set(value) {
            field = value
            autoEllipsizeHelper.isEnabled = value
        }

    var ellipsisLocation: TruncateAt? = TruncateAt.END
        set(value) {
            field = value
            if (value == null) {
                autoEllipsize = false
            }
            onEllipsisChanged(ellipsis)
        }

    internal var drawingPassOverrideStrategy: DrawingPassOverrideStrategy
        get() = autoEllipsizeHelper.drawingPassOverrideStrategy
        set(value) {
            autoEllipsizeHelper.drawingPassOverrideStrategy = value
        }

    private var isRemeasureNeeded = false

    @VisibleForTesting
    var ellipsizedText: CharSequence? = null
        private set

    @VisibleForTesting
    var displayText: CharSequence? = null
        private set

    protected var isInternalTextChange = false

    private var lastMeasuredWidth = NOT_SET
    protected var lastMeasuredHeight = NOT_SET
    private var lastWidthMeasureSpec = NOT_SET
    private var lastHeightMeasureSpec = NOT_SET

    private var originalText: CharSequence? = null
    private var sourceTextRevision = 0
    private var lastSafeNativeOffset = NOT_SET
    private var lastSafeNativeTextRevision = NOT_SET
    private var lastSafeNativeLocale: Locale? = null
    private var cachedEllipsisWidth = ELLIPSIS_WIDTH_UNKNOWN
    private var isEllipsizeRequested = false

    @Suppress("LeakingThis")
    private val autoEllipsizeHelper = AutoEllipsizeHelper(this)

    init {
        if (isInEditMode) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.EllipsizedTextView, defStyleAttr, 0)
            try {
                ellipsis = array.getText(R.styleable.EllipsizedTextView_ellipsis) ?: DEFAULT_ELLIPSIS
            } finally {
                array.recycle()
            }
        }

        onEllipsisChanged(ellipsis)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        autoEllipsizeHelper.onViewAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        autoEllipsizeHelper.onViewDetachedFromWindow()
    }

    private fun onEllipsisChanged(ellipsis: CharSequence) {
        requestEllipsize()
        invalidateEllipsis()
        when {
            noMaxLines() -> super.setEllipsize(null)
            usesNativeEllipsis(ellipsis) -> super.setEllipsize(ellipsisLocation)
            else -> {
                super.setEllipsize(null)
                requestEllipsize()
                invalidateEllipsis()
            }
        }
        requestLayout()
    }

    override fun getText(): CharSequence {
        return ellipsizedText ?: originalText ?: ""
    }

    private fun setTextInternal(text: CharSequence?) {
        isInternalTextChange = true
        super.setText(text)
        isInternalTextChange = false
    }

    private fun updateEllipsizedText(text: CharSequence?) {
        ellipsizedText = text
        setTextInternal(text)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        displayText = text
        super.setText(text, type)
    }

    override fun setMaxLines(maxLines: Int) {
        if (maxLines == this.maxLines) return

        super.setMaxLines(maxLines)
        onEllipsisChanged(ellipsis)
        requestEllipsize()
        invalidateEllipsis()
    }

    override fun setEllipsize(where: TruncateAt?) = Unit

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (isInternalTextChange) return

        originalText = text
        sourceTextRevision++
        requestLayout()
        requestEllipsize()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val canReuseEllipsizedMeasurement = ellipsizedText != null &&
            !isEllipsizeRequested &&
            layout != null &&
            widthMeasureSpec == lastWidthMeasureSpec &&
            heightMeasureSpec == lastHeightMeasureSpec
        if (canReuseEllipsizedMeasurement) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        // Measure the source again, not the prefix from the previous width/font/policy.
        // In particular, intrinsic sizing and adaptive max lines need the untruncated content.
        if (ellipsizedText != null) {
            ellipsizedText = null
            setTextInternal(originalText)
        }
        untruncatedLayout = null
        requestEllipsize()
        invalidateEllipsis()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        sizeChangeInternal(
            width = measuredWidth,
            height = measuredHeight,
            oldWidth = lastMeasuredWidth,
            oldHeight = lastMeasuredHeight
        )
        if (isEllipsizeRequested) {
            logDebug {
                "Size before ellipsize:\nwidth $measuredWidth\nheight $measuredHeight"
            }
            ellipsizeIfNeeded()
            ellipsizedText?.takeIf { isRemeasureNeeded }?.run {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
            logDebug {
                "Size after ellipsize:\nwidth $measuredWidth\nheight $measuredHeight"
            }
        }
        // |lastMeasuredHeight| is set in subclasses since it changes there.
        lastMeasuredWidth = measuredWidth
        lastWidthMeasureSpec = widthMeasureSpec
        lastHeightMeasureSpec = heightMeasureSpec
        autoEllipsizeHelper.onViewMeasured(sourceTextRevision)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        sizeChangeInternal(width, height, oldWidth, oldHeight)
    }

    private fun sizeChangeInternal(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        if (width != oldWidth || height != oldHeight) {
            requestEllipsize()
        }
    }

    internal val isUsingNativeEllipsis: Boolean
        get() = usesNativeEllipsis()

    private fun requestEllipsize() {
        isEllipsizeRequested = true
    }

    private fun invalidateEllipsis() {
        cachedEllipsisWidth = ELLIPSIS_WIDTH_UNKNOWN
        isRemeasureNeeded = false
    }

    private fun ellipsizeIfNeeded() {
        when {
            noMaxLines() -> Unit
            usesNativeEllipsis() -> {
                val result = originalText?.let(::ellipsizeUnsafeNativeBoundary)
                if (result != null) {
                    updateEllipsizedText(result)
                    isRemeasureNeeded = true
                }
            }
            else -> {
                val result = ellipsize(originalText)
                if (result != null && result !== originalText) {
                    updateEllipsizedText(result)
                    isRemeasureNeeded = true
                }
            }
        }
        logDebug {
            "ellipsized text: $ellipsizedText, remeasure: $isRemeasureNeeded"
        }
        isEllipsizeRequested = false
    }

    private fun noMaxLines(): Boolean {
        return maxLines < 0 || maxLines == Int.MAX_VALUE
    }

    private fun usesNativeEllipsis(ellipsis: CharSequence = this.ellipsis): Boolean {
        return ellipsis == DEFAULT_ELLIPSIS &&
            (ellipsisLocation != TruncateAt.END || truncatePolicy == DivText.TruncatePolicy.GRAPHEME)
    }

    private fun ellipsizeUnsafeNativeBoundary(text: CharSequence): CharSequence? {
        if (ellipsisLocation != TruncateAt.END) {
            return null
        }
        val nativeLayout = layout ?: return null
        val lastLine = minOf(maxLines, nativeLayout.lineCount) - 1
        if (lastLine < 0 || nativeLayout.getEllipsisCount(lastLine) == 0) {
            return null
        }
        val lineStart = nativeLayout.getLineStart(lastLine)
        val nativeOffset = lineStart + nativeLayout.getEllipsisStart(lastLine)
        val locale = textLocale
        val hasSafeGraphemeBoundary = text.hasPrintableAsciiBoundary(nativeOffset) ||
            (lastSafeNativeOffset == nativeOffset &&
                lastSafeNativeTextRevision == sourceTextRevision && lastSafeNativeLocale == locale)
        if (hasSafeGraphemeBoundary && !text.splitsReplacementSpan(nativeOffset)) {
            return null
        }
        val safeOffset = TextTruncation(text.toString(), locale).findOffset(
            lineStart = lineStart,
            lineEnd = nativeOffset,
            byWord = false,
        ) { end -> !text.splitsReplacementSpan(end) } ?: return null
        if (safeOffset == nativeOffset) {
            lastSafeNativeOffset = nativeOffset
            lastSafeNativeTextRevision = sourceTextRevision
            lastSafeNativeLocale = locale
            return null
        }
        val width = availableWidth()
        val sourceLayout = layoutEndText(text, width)
        untruncatedLayout = sourceLayout
        return truncatedEndText(text, safeOffset, sourceLayout, lastLine, width)
    }

    private fun CharSequence.hasPrintableAsciiBoundary(offset: Int): Boolean {
        if (offset == 0 || offset == length) {
            return true
        }
        return this[offset - 1] in ' '..'~' && this[offset] in ' '..'~'
    }

    private fun ellipsize(text: CharSequence?): CharSequence? {
        if (text.isNullOrEmpty()) return null

        if (ellipsisLocation == TruncateAt.END && truncatePolicy == DivText.TruncatePolicy.WORD) {
            return ellipsizeEnd(text)
        }

        val ellipsis = ellipsis
        val fittedSymbols = calculateFittedSymbols(text, ellipsis)
        when {
            fittedSymbols <= 0 && ellipsisLocation == TruncateAt.END -> return ellipsizeEnd(text)
            fittedSymbols <= 0 -> return null
            fittedSymbols == text.length && ellipsisLocation == TruncateAt.END -> return ellipsizeEnd(text)
            fittedSymbols == text.length -> return text
        }

        val safeFittedSymbols = findSafeFittedSymbols(
            text = text,
            locale = textLocale,
            fittedSymbols = fittedSymbols,
        ) { end -> !text.splitsReplacementSpan(end) }

        val stringBuilder = SpannableStringBuilder(text, 0, safeFittedSymbols)
        stringBuilder.append(ellipsis)
        return stringBuilder
    }

    private fun ellipsizeEnd(text: CharSequence): CharSequence? {
        val width = availableWidth()
        if (width <= 0 || maxLines <= 0) {
            return null
        }
        val sourceLayout = layoutEndText(text, width)
        untruncatedLayout = sourceLayout
        val lastLine = minOf(maxLines, sourceLayout.lineCount) - 1
        val fits = sourceLayout.lineCount <= maxLines &&
            (0 until sourceLayout.lineCount).all { sourceLayout.getLineMax(it) <= width }
        if (fits) {
            return text
        }
        val lineStart = sourceLayout.getLineStart(lastLine)
        val nextLineEnd = sourceLayout.getLineVisibleEnd(minOf(lastLine + 1, sourceLayout.lineCount - 1))
        val paragraphEnd = text.indexOf('\n', lineStart).takeIf { it >= 0 } ?: text.length
        val lineEnd = minOf(nextLineEnd, paragraphEnd)
        val truncation = TextTruncation(text.toString(), textLocale)
        val offset = truncation.findOffset(
            lineStart = lineStart,
            lineEnd = lineEnd,
            byWord = truncatePolicy == DivText.TruncatePolicy.WORD,
        ) { end ->
            !text.splitsReplacementSpan(end) && if (end == 0) {
                ellipsisFits(width)
            } else {
                endTextFits(text, end, sourceLayout, lastLine, width)
            }
        } ?: return null
        isRemeasureNeeded = true
        return truncatedEndText(text, offset, sourceLayout, lastLine, width)
    }

    private fun ellipsisFits(width: Int): Boolean {
        val result = layoutEndText(ellipsis, width)
        return result.lineCount == 1 && result.getLineMax(0) <= width
    }

    private fun endTextFits(text: CharSequence, end: Int, source: Layout, lastLine: Int, width: Int): Boolean {
        val candidate = truncatedEndText(text, end, source, lastLine, width)
        val result = layoutEndText(candidate, width)
        if (result.lineCount != lastLine + 1 || result.getLineMax(lastLine) > width) {
            return false
        }
        return true
    }

    /**
     * Truncates [text] at [end], appends the configured ellipsis, and preserves soft wraps from [source].
     * [lastLine] is the final visible line in [source], and [width] is the available text width.
     */
    private fun truncatedEndText(text: CharSequence, end: Int, source: Layout, lastLine: Int, width: Int): CharSequence {
        val result = SpannableStringBuilder(text, 0, end)
        val candidate = SpannableStringBuilder(result).append(ellipsis)
        if (lastLine == 0) {
            return candidate
        }
        val layout = layoutEndText(candidate, width)
        if (layout.lineCount > lastLine &&
            (1..lastLine).all { layout.getLineStart(it) == source.getLineStart(it) }
        ) {
            return candidate
        }
        // Freeze earlier soft wraps: shortening the last word must not pull it into an earlier line.
        val softBreaks: List<Int> = (1..lastLine)
            .map(source::getLineStart)
            .filter { text[it - 1] != '\n' }
        for (start in softBreaks.asReversed()) {
            if (start <= end && text[start - 1] == SOFT_HYPHEN) {
                result.replace(start - 1, start, VISIBLE_HYPHEN)
            }
            val direction = if (source.getParagraphDirection(source.getLineForOffset(start)) < 0) {
                RIGHT_TO_LEFT_MARK
            } else {
                LEFT_TO_RIGHT_MARK
            }
            result.insert(minOf(start, end), "\n" + direction)
        }
        // These spans retain their source offsets internally as well as in the Spanned ranges.
        for (span in result.getSpans(0, result.length, LineHeightWithTopOffsetSpan::class.java)) {
            val replacement = span.withMappedOffsets { offset -> offset + 2 * softBreaks.count { it <= offset } }
            result.setSpan(replacement, result.getSpanStart(span), result.getSpanEnd(span), result.getSpanFlags(span))
            result.removeSpan(span)
        }
        return result.append(ellipsis)
    }

    /**
     * Returns whether truncating this text at [offset] would split a replacement span.
     * [offset] must use the same character indexing as this sequence and its span ranges.
     */
    private fun CharSequence.splitsReplacementSpan(offset: Int): Boolean {
        if (this !is Spanned) {
            return false
        }
        return getSpans(offset, offset, ReplacementSpan::class.java).any {
            getSpanStart(it) < offset && getSpanEnd(it) > offset
        }
    }

    /**
     * Lays out [text] with this view's metrics and truncation-independent settings.
     * [width] is the available text width in pixels.
     */
    private fun layoutEndText(text: CharSequence, width: Int): Layout {
        val direction = TextViewCompat.getTextMetricsParams(this).textDirection ?: TextDirectionHeuristics.FIRSTSTRONG_LTR
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(direction)
            .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
            .setIncludePad(includeFontPadding)
            .setBreakStrategy(breakStrategy)
            .setHyphenationFrequency(hyphenationFrequency)
            .build()
    }

    private fun calculateFittedSymbols(text: CharSequence, ellipsis: CharSequence): Int {
        if (text.isEmpty() || maxLines == 0) return 0

        val textWidth = availableWidth()
        if (textWidth <= 0) return 0

        val textLayout = if (isHyphenationEnabled()) {
            layoutTextWithHyphenation(text, textWidth)
        } else {
            layoutText(text, textWidth)
        }

        val lines = textLayout.lineCount
        val lastLineWidth = textLayout.getLineWidth(lines - 1)
        if (lines < maxLines || lines == maxLines && lastLineWidth <= textWidth) {
            isRemeasureNeeded = true
            return text.length
        }

        if (cachedEllipsisWidth == ELLIPSIS_WIDTH_UNKNOWN) {
            val ellipsisLayout = layoutText(ellipsis)
            cachedEllipsisWidth = ellipsisLayout.getLineWidth(0)
        }

        isRemeasureNeeded = true

        val ellipsizedTextWidth = textWidth - cachedEllipsisWidth
        var fittedSymbols = textLayout.getOffsetForHorizontal(maxLines - 1, ellipsizedTextWidth)
        //It may be required to remove the last symbol from the text to fit ellipsis
        //But there can be a non-printable zero-width symbol, so we need to iterate until ellipsis fits
        while (textLayout.getPrimaryHorizontal(fittedSymbols) > ellipsizedTextWidth && fittedSymbols > 0) {
            fittedSymbols--
        }

        // Dropping last symbol if it represents a first byte of two-byte unicode symbol
        if (fittedSymbols > 0) {
            val lastChar = text[fittedSymbols - 1]
            if (Character.isHighSurrogate(lastChar)) {
                fittedSymbols--
            }
        }
        return fittedSymbols
    }

    protected fun availableWidth(): Int {
        return measuredWidth - compoundPaddingLeft - compoundPaddingRight
    }

    @Suppress("DEPRECATION")
    private fun layoutText(text: CharSequence, textWidth: Int = Int.MAX_VALUE): Layout {
        return StaticLayout(
            text,
            paint,
            textWidth,
            Layout.Alignment.ALIGN_NORMAL,
            lineSpacingMultiplier,
            lineSpacingExtra,
            true
        )
    }

    private fun layoutTextWithHyphenation(text: CharSequence, textWidth: Int = Int.MAX_VALUE): Layout {
        val builder = StaticLayout.Builder.obtain(text, 0, text.length, paint, textWidth)
        return builder
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
            .setIncludePad(true)
            .setHyphenationFrequency(hyphenationFrequency)
            .build()
    }

    companion object {
        const val DEFAULT_ELLIPSIS = "\u2026"
        private const val SOFT_HYPHEN = '\u00AD'
        private const val VISIBLE_HYPHEN = "\u2010"
        private const val LEFT_TO_RIGHT_MARK = "\u200E"
        private const val RIGHT_TO_LEFT_MARK = "\u200F"
        private const val ELLIPSIS_WIDTH_UNKNOWN = -1.0f
        const val NOT_SET = -1

        private const val TAG = "Ya:EllipsizedTextView"
        private const val DEBUG = false

        private inline fun logDebug(message: () -> String) {
            if (DEBUG) {
                android.util.Log.i(TAG, message())
            }
        }
    }
}

internal fun findSafeFittedSymbols(
    text: CharSequence,
    locale: Locale,
    fittedSymbols: Int,
    isSafeBoundary: (Int) -> Boolean,
): Int {
    return TextTruncation(text.toString(), locale).findOffset(
        lineStart = 0,
        lineEnd = fittedSymbols,
        byWord = false,
        fits = isSafeBoundary,
    ) ?: 0
}
