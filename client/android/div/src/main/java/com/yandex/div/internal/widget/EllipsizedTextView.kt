package com.yandex.div.internal.widget

import android.content.Context
import android.os.Build
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextUtils
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import com.yandex.div.R
import com.yandex.div.internal.util.isHyphenationEnabled

open class EllipsizedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var ellipsis: CharSequence = DEFAULT_ELLIPSIS
        set(value) {
            onEllipsisChanged(value)
            field = value
        }

    /**
     * If text auto ellipsize is enabled.
     */
    var autoEllipsize: Boolean = false
        set(value) {
            field = value
            autoEllipsizeHelper.isEnabled = value
        }

    private var isRemeasureNeeded = false

    @VisibleForTesting
    var ellipsizedText: CharSequence? = null
        private set(value) {
            field = value
            setTextInternal(value)
        }

    @VisibleForTesting
    var displayText: CharSequence? = null
        private set

    protected var isInternalTextChange = false

    private var lastMeasuredWidth = NOT_SET
    protected var lastMeasuredHeight = NOT_SET

    private var originalText: CharSequence? = null
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
        when {
            noMaxLines() -> super.setEllipsize(null)
            ellipsis == DEFAULT_ELLIPSIS -> super.setEllipsize(TextUtils.TruncateAt.END)
            else -> {
                super.setEllipsize(null)
                requestEllipsize()
                invalidateEllipsis()
            }
        }
        requestLayout()
    }

    override fun getText(): CharSequence {
        return originalText ?: ""
    }

    private fun setTextInternal(text: CharSequence?) {
        isInternalTextChange = true
        super.setText(text)
        isInternalTextChange = false
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        displayText = text
        super.setText(text, type)
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        onEllipsisChanged(ellipsis)
        requestEllipsize()
        invalidateEllipsis()
    }

    override fun setEllipsize(where: TextUtils.TruncateAt?) = Unit

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (isInternalTextChange) return

        originalText = text
        requestLayout()
        requestEllipsize()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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

    private fun requestEllipsize() {
        isEllipsizeRequested = true
    }

    private fun invalidateEllipsis() {
        cachedEllipsisWidth = ELLIPSIS_WIDTH_UNKNOWN
        isRemeasureNeeded = false
    }

    private fun ellipsizeIfNeeded() {
        val oldEllipsizedText = ellipsizedText
        val noCustomEllipsisNeeded = noMaxLines() || ellipsis == DEFAULT_ELLIPSIS
        when {
            ellipsizedText == null && noCustomEllipsisNeeded -> Unit
            noCustomEllipsisNeeded -> ellipsizedText = originalText?.also { isRemeasureNeeded = it != oldEllipsizedText }
            else -> ellipsizedText = ellipsize(originalText)
        }
        logDebug {
            "ellipsized text: $ellipsizedText, remeasure: $isRemeasureNeeded"
        }
        isEllipsizeRequested = false
    }

    private fun noMaxLines(): Boolean {
        return maxLines < 0 || maxLines == Int.MAX_VALUE
    }

    private fun ellipsize(text: CharSequence?): CharSequence? {
        if (text.isNullOrEmpty()) return null

        val ellipsis = ellipsis
        val fittedSymbols = calculateFittedSymbols(text, ellipsis)
        when {
            fittedSymbols <= 0 -> return null
            fittedSymbols == text.length -> return text
        }

        val stringBuilder = SpannableStringBuilder(text, 0, fittedSymbols)
        stringBuilder.append(ellipsis)
        return stringBuilder
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

    @RequiresApi(Build.VERSION_CODES.M)
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
