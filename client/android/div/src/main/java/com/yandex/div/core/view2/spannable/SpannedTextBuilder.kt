package com.yandex.div.core.view2.spannable

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuffColorFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.getSpans
import androidx.core.view.ViewCompat
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.text.DivBackgroundSpan
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.core.view2.divs.getFontVariations
import com.yandex.div.core.view2.divs.supportFontVariations
import com.yandex.div.core.view2.divs.toPorterDuffMode
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.core.view2.divs.toTextVerticalAlignment
import com.yandex.div.core.view2.divs.unitToPx
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.hasBackgroundSpan
import com.yandex.div.core.view2.getTypeface
import com.yandex.div.core.view2.getTypefaceValue
import com.yandex.div.internal.spannable.LetterSpacingSpan
import com.yandex.div.internal.spannable.NoStrikethroughSpan
import com.yandex.div.internal.spannable.NoUnderlineSpan
import com.yandex.div.internal.spannable.TextColorSpan
import com.yandex.div.internal.spannable.TypefaceSpan
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivLineStyle
import com.yandex.div2.DivShadow
import com.yandex.div2.DivText
import com.yandex.div2.DivTextAlignmentVertical
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder
import javax.inject.Inject

private typealias TextConsumer = (Spanned) -> Unit

@DivScope
internal class SpannedTextBuilder @Inject constructor(
    private val typefaceResolver: DivTypefaceResolver,
    private val imageLoader: DivImageLoader,
) {

    private val tempPaint = Paint()
    private val debugFontMetrics = false

    fun buildPlainText(
        bindingContext: BindingContext,
        textView: TextView,
        divText: DivText
    ): Spanned {
        return buildText(
            bindingContext,
            textView,
            divText,
            divText.text.evaluate(bindingContext.expressionResolver),
            null,
            null,
            null,
            null
        )
    }

    fun buildText(
        bindingContext: BindingContext,
        textView: TextView,
        divText: DivText,
        textConsumer: TextConsumer? = null
    ): Spanned {
        return buildText(
            bindingContext,
            textView,
            divText,
            divText.text.evaluate(bindingContext.expressionResolver),
            divText.ranges,
            divText.images,
            divText.actions,
            textConsumer
        )
    }

    fun buildEllipsis(
        bindingContext: BindingContext,
        textView: TextView,
        divText: DivText,
        ellipsis: DivText.Ellipsis,
        textConsumer: TextConsumer? = null
    ): Spanned {
        return buildText(bindingContext,
            textView,
            divText,
            ellipsis.text.evaluate(bindingContext.expressionResolver),
            ellipsis.ranges,
            ellipsis.images,
            ellipsis.actions,
            textConsumer)
    }

    private fun buildText(
        bindingContext: BindingContext,
        textView: TextView,
        divText: DivText,
        text: String,
        ranges: List<DivText.Range>?,
        images: List<DivText.Image>?,
        actions: List<DivAction>?,
        textConsumer: TextConsumer? = null
    ): Spanned {
        val context = textView.context
        val divView = bindingContext.divView
        val resolver = bindingContext.expressionResolver

        // We use zero-width space for empty text to make sure line height span will be applied properly.
        val spannedText = SpannableStringBuilder(text.ifEmpty { ZWSP })
        val textData = createTextData(context, bindingContext, divText, text)
        val textLength = textData.textLength
        val spans = preprocessSpans(context, bindingContext, textData, ranges)
        val sortedImages = preprocessImages(textData, images, resolver)

        if (debugFontMetrics) {
            spannedText.setSpan(LineMetricsSpan(), 0, spannedText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        (textView as? DivLineHeightTextView)?.apply {
            clearImageSpans()
            textRoundedBgHelper?.invalidateSpansCache()
        }

        spans.forEach { span ->
            addSpan(textView, spannedText, textData, span)
        }

        val hasAdditionalRanges = ranges?.any { range ->
            range.actions != null
                || range.background != null
                || range.border != null
        } ?: false

        if (hasAdditionalRanges) {
            ranges?.forEach { range ->
                val start = range.start.evaluate(resolver).toIntSafely().coerceAtMost(textLength)
                val end = range.end?.evaluate(resolver)?.toIntSafely()?.coerceAtMost(textLength) ?: textLength
                addActionSpan(bindingContext, textView, spannedText, start, end, range.actions)
                addDecorationSpan(bindingContext, textView, spannedText, start, end, range.border, range.background)
            }
        }

        addActionSpan(
            bindingContext,
            textView,
            spannedText,
            0,
            spannedText.length,
            actions,
            enableClickableSpanSupport = false
        )

        for (index in sortedImages.indices.reversed()) {
            val image = sortedImages[index]
            val position = imagePosition(textData.textLength, image, resolver)
            val prevImagePosition = if (index > 0) {
                imagePosition(textData.textLength, sortedImages[index - 1], resolver)
            } else {
                Int.MIN_VALUE
            }
            spannedText.insert(position, IMAGE_PLACEHOLDER)
            val imageSpan = addImageSpan(bindingContext, textView, spannedText, textData, image)

            val nextAfterImage = prevImagePosition + 1 == position
            val nextAfterWord = position > 0 && !spannedText[position - 1].isWhitespace()
            if (!nextAfterImage && nextAfterWord) {
                spannedText.insert(position, WORD_JOINER)
            }

            val reference = imageLoader.loadImage(
                image.url.evaluate(resolver).toString(),
                ImageDownloadCallbackImpl(bindingContext, image, imageSpan, spannedText, textConsumer)
            )
            divView.addLoadReference(reference, textView)
        }

        textConsumer?.invoke(spannedText)
        return spannedText
    }

    private fun preprocessSpans(
        context: Context,
        bindingContext: BindingContext,
        textData: TextData,
        ranges: List<DivText.Range>?,
    ): List<SpanData> {
        if (textData.lineHeight == null && ranges.isNullOrEmpty()) return emptyList()

        val resolver = bindingContext.expressionResolver
        val textLength = textData.textLength

        val rangeCount = ranges?.size ?: 0
        val boundSet = sortedSetOf<Int>()
        val overlappingSpans = ArrayList<SpanData>(rangeCount + 1)

        ranges?.forEach { range ->
            val start = range.start.evaluate(resolver).toIntSafely().coerceAtMost(textLength)
            val end = range.end?.evaluate(resolver)?.toIntSafely()?.coerceAtMost(textLength) ?: textLength
            if (start < end) {
                val span = createSpanData(context, bindingContext, textData, range, start, end)
                if (!span.isEmpty()) {
                    boundSet += start
                    boundSet += end
                    overlappingSpans += span
                }
            }
        }
        overlappingSpans.sort()

        textData.lineHeight?.let { lineHeight ->
            boundSet += 0
            boundSet += textLength
            overlappingSpans.add(0, SpanData.lineHeight(start = 0, end = textLength, lineHeight))
        }
        if (overlappingSpans.isEmpty()) return emptyList()

        val bounds = boundSet.toList()
        val sequentialSpans = mutableListOf<SpanData>()
        val activeSpans = mutableListOf<SpanData>()
        var leftBound = bounds.first()

        if (bounds.size == 1) {
            val rightBound = leftBound
            sequentialSpans += overlappingSpans.fold(SpanData.empty(leftBound, rightBound)) { result, span ->
                result.mergeWith(span, start = leftBound, end = rightBound)
            }
            return sequentialSpans
        }

        for (i in 1 until bounds.size) {
            val rightBound = bounds[i]
            for (j in overlappingSpans.indices) {
                val span = overlappingSpans[j]
                if (rightBound < span.start) break
                if (rightBound > span.start && rightBound <= span.end) {
                    activeSpans += span
                }
            }
            if (activeSpans.isNotEmpty()) {
                sequentialSpans += activeSpans.fold(SpanData.empty(leftBound, rightBound)) { result, span ->
                    result.mergeWith(span, start = leftBound, end = rightBound)
                }
            }
            activeSpans.clear()
            leftBound = rightBound
        }
        return sequentialSpans
    }

    private fun preprocessImages(
        textData: TextData,
        images: List<DivText.Image>?,
        resolver: ExpressionResolver
    ): List<DivText.Image> {
        return images?.filter { it.start.evaluate(resolver) <= textData.textLength }
            ?.sortedBy { imagePosition(textData.textLength, it, resolver) } ?: emptyList()
    }

    private fun imagePosition(textLength: Int, image: DivText.Image, resolver: ExpressionResolver): Int {
        val position = image.start.evaluate(resolver)
        return when (image.indexingDirection.evaluate(resolver)) {
            DivText.Image.IndexingDirection.NORMAL -> position.toIntSafely()
            DivText.Image.IndexingDirection.REVERSED -> (textLength - position).toIntSafely()
        }
    }

    private fun addSpan(
        textView: TextView,
        spannedText: Spannable,
        textData: TextData,
        span: SpanData
    ) {
        val start = span.start
        val end = span.end
        if (start > end) return

        val alignment = span.alignmentVertical ?: DivTextAlignmentVertical.BASELINE
        val baselineOffset = span.baselineOffset
        if (baselineOffset != 0) {
            spannedText.setSpan(
                BaselineShiftSpan(
                    baselineShift = baselineOffset,
                    lineHeight = span.lineHeight ?: textData.lineHeight ?: 0
                ),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else if (alignment != DivTextAlignmentVertical.BASELINE) {
            spannedText.setSpan(
                VerticalAlignmentSpan(
                    fontSize = span.fontSize ?: 0,
                    alignment = alignment.toTextVerticalAlignment(),
                    layoutProvider = { textView.layout }
                ),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        span.fontSize?.let { fontSize ->
            spannedText.setSpan(
                FontSizeSpan(
                    fontSize = fontSize,
                    lineHeight = span.lineHeight ?: textData.lineHeight ?: 0
                ),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        span.fontFeatureSettings?.let { settings ->
            spannedText.setSpan(FontFeatureSpan(settings), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        span.textColor?.let { textColor ->
            spannedText.setSpan(TextColorSpan(textColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        span.letterSpacing?.let { letterSpacing ->
            spannedText.setSpan(
                LetterSpacingSpan(letterSpacing.toFloat()),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        span.strike?.let { strike ->
            when (strike) {
                DivLineStyle.SINGLE -> {
                    spannedText.setSpan(StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                DivLineStyle.NONE -> {
                    spannedText.setSpan(NoStrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                else -> Unit
            }
        }

        span.underline?.let { underline ->
            when (underline) {
                DivLineStyle.SINGLE -> {
                    spannedText.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                DivLineStyle.NONE -> {
                    spannedText.setSpan(NoUnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                else -> Unit
            }
        }

        val typefaceProvider = typefaceResolver.getTypefaceProvider(span.fontFamily ?: textData.fontFamily)
        if (span.fontFamily != null || span.fontWeight != null || span.fontWeightValue != null) {
            val fontWeightValue = if (span.fontWeight != null || span.fontWeightValue != null) {
                getTypefaceValue(span.fontWeight, span.fontWeightValue)
            } else {
                getTypefaceValue(textData.fontWeight, textData.fontWeightValue)
            }
            spannedText.setSpan(
                TypefaceSpan(getTypeface(fontWeightValue, typefaceProvider)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        if (supportFontVariations && typefaceProvider.isVariable &&
            (span.fontWeight != null || span.fontWeightValue != null || span.fontVariationSettings != null)) {
            spannedText.setSpan(
                FontVariationSpan(getFontVariations(span.fontWeight, span.fontWeightValue, span.fontVariationSettings)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        if (span.lineHeight != null || span.topOffset != null) {
            val type = if (span.lineHeight == textData.lineHeight) {
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            } else {
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            }

            spannedText.setSpan(
                LineHeightWithTopOffsetSpan(
                    topOffset = span.topOffset ?: 0,
                    lineHeight = span.lineHeight ?: 0,
                    topOffsetStart = span.topOffsetStart ?: start,
                    topOffsetEnd = span.topOffsetEnd ?: end
                ),
                start,
                end,
                type
            )
        }

        span.textShadow?.let { textShadow ->
            spannedText.setSpan(ShadowSpan(textShadow), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    private fun addActionSpan(
        bindingContext: BindingContext,
        textView: TextView,
        spannedText: Spannable,
        start: Int,
        end: Int,
        actions: List<DivAction>?,
        enableClickableSpanSupport: Boolean = true,
    ) {
        if (actions.isNullOrEmpty()) return

        textView.movementMethod = LinkMovementMethod.getInstance()
        spannedText.setSpan(
            PerformActionSpan(bindingContext, actions),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (enableClickableSpanSupport) {
            ViewCompat.enableAccessibleClickableSpanSupport(textView)
        }
    }

    private fun addDecorationSpan(
        bindingContext: BindingContext,
        textView: TextView,
        spannedText: Spannable,
        start: Int,
        end: Int,
        border: DivTextRangeBorder?,
        background: DivTextRangeBackground?
    ) {
        if (border == null && background == null) return

        val resolver = bindingContext.expressionResolver
        val backgroundSpan = DivBackgroundSpan(border, background)
        if (textView is DivLineHeightTextView &&
            !textView.hasBackgroundSpan(spannedText, backgroundSpan, start, end, resolver)) {
            spannedText.setSpan(backgroundSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.textRoundedBgHelper?.addBackgroundSpan(backgroundSpan)
        }
    }

    private fun addImageSpan(
        bindingContext: BindingContext,
        textView: TextView,
        spannedText: Spannable,
        textData: TextData,
        image: DivText.Image
    ): ImageSpan {
        val displayMetrics = textView.resources.displayMetrics
        val divView = bindingContext.divView
        val resolver = bindingContext.expressionResolver

        val position = imagePosition(textData.textLength, image, resolver)
        val width = image.width.toPx(displayMetrics, resolver)
        val height = image.height.toPx(displayMetrics, resolver)
        val lineHeight = textData.lineHeight ?: 0
        val alignment = image.alignmentVertical.evaluate(resolver).toTextVerticalAlignment()

        val accessibility = image.accessibility?.run {
            val accessibilityType = when (type) {
                DivText.Image.Accessibility.Type.NONE -> null
                DivText.Image.Accessibility.Type.BUTTON -> Button::class.qualifiedName
                DivText.Image.Accessibility.Type.IMAGE -> ImageView::class.qualifiedName
                DivText.Image.Accessibility.Type.TEXT -> TextView::class.qualifiedName
                DivText.Image.Accessibility.Type.AUTO -> ImageView::class.qualifiedName
                else -> null
            }
            val contentDescription = description?.evaluate(resolver)
            val spanActions = getActionsForPosition(bindingContext, spannedText, position)
            val onClickAction = spanActions?.let { actions ->
                ImageSpan.OnAccessibilityClickAction {
                    val actionBinder = divView.div2Component.actionBinder
                    actionBinder.handleTapClick(bindingContext, textView, actions)
                }
            }
            ImageSpan.Accessibility(accessibilityType, contentDescription, onClickAction)
        }

        val imageSpan = ImageSpan(null, width, height, lineHeight, alignment, accessibility)
        spannedText.setSpan(imageSpan, position, position + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        (textView as? DivLineHeightTextView)?.addImageSpan(imageSpan)
        return imageSpan
    }

    private fun getActionsForPosition(
        bindingContext: BindingContext,
        spannedText: Spannable,
        position: Int
    ): List<DivAction>? {
        val actionSpans = spannedText.getSpans<PerformActionSpan>(position, position + 1)

        if (actionSpans.size > 1) {
            bindingContext.divView.logWarning(Throwable("Two or more clickable ranges intersect."))
        }

        actionSpans.firstOrNull()?.let { span ->
            return span.actions
        }
        return null
    }

    private fun createTextData(
        context: Context,
        bindingContext: BindingContext,
        divText: DivText,
        text: String
    ): TextData {
        val displayMetrics = context.resources.displayMetrics
        val resolver = bindingContext.expressionResolver
        val fontSizeValue = divText.fontSize.evaluate(resolver).toIntSafely()
        val fontSizeUnit = divText.fontSizeUnit.evaluate(resolver)
        return TextData(
            text = text,
            fontSize = fontSizeValue.unitToPx(displayMetrics, fontSizeUnit),
            fontSizeValue = fontSizeValue,
            fontSizeUnit = fontSizeUnit,
            fontWeight = divText.fontWeight.evaluate(resolver),
            fontWeightValue = divText.fontWeightValue?.evaluate(resolver)?.toIntSafely(),
            fontFamily = divText.fontFamily?.evaluate(resolver),
            lineHeight = divText.lineHeight?.evaluate(resolver)?.toIntSafely()?.unitToPx(displayMetrics, fontSizeUnit),
            textColor = divText.textColor.evaluate(resolver)
        )
    }

    private fun createSpanData(
        context: Context,
        bindingContext: BindingContext,
        textData: TextData,
        range: DivText.Range,
        start: Int,
        end: Int
    ) : SpanData {
        val displayMetrics = context.resources.displayMetrics
        val resolver = bindingContext.expressionResolver
        val textFontSizeValue = textData.fontSizeValue
        val fontSizeValue = range.fontSize?.evaluate(resolver)?.toIntSafely()
        val fontSizeUnit = range.fontSizeUnit.evaluate(resolver)
        return SpanData(
            start = start,
            end = end,
            alignmentVertical = range.alignmentVertical?.evaluate(resolver),
            baselineOffset = range.baselineOffset.evaluate(resolver).unitToPx(displayMetrics, fontSizeUnit),
            fontFamily = range.fontFamily?.evaluate(resolver),
            fontFeatureSettings = range.fontFeatureSettings?.evaluate(resolver),
            fontSize = fontSizeValue?.unitToPx(displayMetrics, fontSizeUnit),
            fontSizeUnit = fontSizeUnit,
            fontWeight = range.fontWeight?.evaluate(resolver),
            fontWeightValue = range.fontWeightValue?.evaluate(resolver)?.toIntSafely(),
            fontVariationSettings = range.fontVariationSettings?.evaluate(resolver),
            letterSpacing = range.letterSpacing?.evaluate(resolver)?.div(fontSizeValue ?: textFontSizeValue),
            lineHeight = range.lineHeight?.evaluate(resolver)?.unitToPx(displayMetrics, fontSizeUnit),
            strike = range.strike?.evaluate(resolver),
            textColor = range.textColor?.evaluate(resolver),
            textShadow = createShadowData(context, bindingContext, range.textShadow, textData.textColor),
            topOffset = range.topOffset?.evaluate(resolver)?.toIntSafely()?.unitToPx(displayMetrics, fontSizeUnit),
            topOffsetStart = if (range.topOffset != null) start else null,
            topOffsetEnd = if (range.topOffset != null) end else null,
            underline = range.underline?.evaluate(resolver)
        )
    }

    private fun createShadowData(
        context: Context,
        bindingContext: BindingContext,
        shadow: DivShadow?,
        @ColorInt textColor: Int
    ): ShadowData? {
        if (shadow == null) return null

        val displayMetrics = context.resources.displayMetrics
        val resolver = bindingContext.expressionResolver
        val textAlpha = textColor ushr 24

        val radius = shadow.blur.evaluate(resolver).dpToPxF(displayMetrics)
        val offsetX = shadow.offset.x.toPx(displayMetrics, resolver).toFloat()
        val offsetY = shadow.offset.y.toPx(displayMetrics, resolver).toFloat()
        val color = tempPaint.apply {
            color = shadow.color.evaluate(resolver)
            alpha = (shadow.alpha.evaluate(resolver) * textAlpha).toInt()
        }.color

        return ShadowData(offsetX, offsetY, radius, color)
    }

    private class ImageDownloadCallbackImpl(
        private val bindingContext: BindingContext,
        private val image: DivText.Image,
        private val imageSpan: ImageSpan,
        private val spannedText: Spanned,
        private val textConsumer: TextConsumer? = null
    ) : DivIdLoggingImageDownloadCallback(bindingContext.divView) {

        override fun onSuccess(cachedBitmap: CachedBitmap) {
            super.onSuccess(cachedBitmap)

            val resources = bindingContext.divView.resources
            val resolver = bindingContext.expressionResolver
            val tintColor = image.tintColor?.evaluate(resolver)
            val tintMode = image.tintMode.evaluate(resolver).toPorterDuffMode()

            val imageDrawable = cachedBitmap.bitmap.toDrawable(resources)
            if (tintColor != null) {
                imageDrawable.colorFilter = PorterDuffColorFilter(tintColor, tintMode)
            }
            imageSpan.image = imageDrawable
            textConsumer?.invoke(spannedText)
        }
    }

    private companion object {
        private const val IMAGE_PLACEHOLDER = "#"
        private const val WORD_JOINER = "\u2060"
        private const val ZWSP = "\u200B"
    }
}
