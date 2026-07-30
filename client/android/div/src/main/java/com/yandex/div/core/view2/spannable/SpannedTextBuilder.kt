package com.yandex.div.core.view2.spannable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.getSpans
import androidx.core.view.ViewCompat
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.text.DivBackgroundSpan
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.util.toPorterDuffMode
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.core.view2.divs.getFontVariations
import com.yandex.div.core.view2.divs.supportFontVariations
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.core.view2.divs.unitToPx
import com.yandex.div.core.view2.divs.unitToPxF
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.hasBackgroundSpan
import com.yandex.div.core.view2.getTypeface
import com.yandex.div.core.view2.getTypefaceValue
import com.yandex.div.core.view2.text.SelectableLinkMovementMethod
import com.yandex.div.internal.core.DivTextImageResult
import com.yandex.div.internal.core.DivTextRangeResult
import com.yandex.div.internal.core.buildImages
import com.yandex.div.internal.core.buildRanges
import com.yandex.div.internal.spannable.LetterSpacingSpan
import com.yandex.div.internal.spannable.NoStrikethroughSpan
import com.yandex.div.internal.spannable.NoUnderlineSpan
import com.yandex.div.internal.spannable.TextColorSpan
import com.yandex.div.internal.spannable.TypefaceSpan
import com.yandex.div.internal.util.makeIf
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivLineStyle
import com.yandex.div2.DivShadow
import com.yandex.div2.DivText
import com.yandex.div2.DivTextAlignmentVertical
import com.yandex.div2.DivTextRangeMask
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
        textView: TextView,
        divText: DivText,
        resolver: ExpressionResolver,
        divView: Div2View,
    ): Spanned {
        return buildText(
            textView,
            divText,
            divText.text.evaluate(resolver),
            null,
            null,
            null,
            resolver,
            divView,
            null
        )
    }

    fun buildText(
        textView: TextView,
        divText: DivText,
        resolver: ExpressionResolver,
        path: DivStatePath,
        divView: Div2View,
        textConsumer: TextConsumer? = null
    ): Spanned {
        return buildText(
            textView,
            divText,
            divText.text.evaluate(resolver),
            divText.buildRanges(resolver, path),
            divText.buildImages(resolver, path),
            null,
            resolver,
            divView,
            textConsumer
        )
    }

    fun buildEllipsis(
        textView: TextView,
        divText: DivText,
        ellipsis: DivText.Ellipsis,
        resolver: ExpressionResolver,
        path: DivStatePath,
        divView: Div2View,
        textConsumer: TextConsumer? = null
    ): Spanned {
        return buildText(
            textView,
            divText,
            ellipsis.text.evaluate(resolver),
            ellipsis.buildRanges(resolver, path),
            ellipsis.buildImages(resolver, path),
            ellipsis.actions,
            resolver,
            divView,
            textConsumer,
            inEllipsis = true
        )
    }

    private fun buildText(
        textView: TextView,
        divText: DivText,
        text: String,
        ranges: List<DivTextRangeResult>?,
        images: List<DivTextImageResult>?,
        actions: List<DivAction>?,
        resolver: ExpressionResolver,
        divView: Div2View,
        textConsumer: TextConsumer? = null,
        inEllipsis: Boolean = false,
    ): Spanned {
        val context = textView.context

        // We use zero-width space for empty text to make sure line height span will be applied properly.
        val spannedText = SpannableStringBuilder(text.ifEmpty { ZWSP })
        val textData = createTextData(context, divText, text, resolver)
        val textLength = textData.textLength
        val spans = preprocessSpans(context, textData, ranges)
        val sortedImages = preprocessImages(textData, images)

        if (debugFontMetrics) {
            spannedText.setSpan(LineMetricsSpan(), 0, spannedText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        (textView as? DivLineHeightTextView)?.apply {
            clearImageSpans()
            textRoundedBgHelper?.invalidateSpansCache(inEllipsis)
        }

        spans.forEach { span ->
            addSpan(textView, spannedText, textData, span)
        }

        val hasAdditionalRanges = ranges?.any { (range, _) ->
            range.actions != null
                || range.background != null
                || range.border != null
        } ?: false

        if (hasAdditionalRanges) {
            ranges?.forEach { (range, rangeResolver) ->
                val start = range.start.evaluate(rangeResolver).toIntSafely().coerceAtMost(textLength)
                val end = range.end?.evaluate(rangeResolver)?.toIntSafely()?.coerceAtMost(textLength) ?: textLength
                addActionSpan(textView, spannedText, start, end, range.actions, rangeResolver, divView)
                addDecorationSpan(
                    rangeResolver,
                    textView,
                    spannedText,
                    start,
                    end,
                    range,
                    textData,
                    inEllipsis
                )
            }
        }

        addActionSpan(
            textView,
            spannedText,
            0,
            spannedText.length,
            actions,
            resolver,
            divView,
        )

        for (index in sortedImages.indices.reversed()) {
            val (image, imageResolver) = sortedImages[index]
            val position = imagePosition(textData.textLength, image, imageResolver)
            val prevImagePosition = if (index > 0) {
                val (prevImage, prevResolver) = sortedImages[index - 1]
                imagePosition(textData.textLength, prevImage, prevResolver)
            } else {
                Int.MIN_VALUE
            }
            spannedText.insert(position, IMAGE_PLACEHOLDER)
            val imageSpan = addImageSpan(textView, spannedText, textData, image, imageResolver, divView)

            val nextAfterImage = prevImagePosition + 1 == position
            val nextAfterWord = position > 0 && !spannedText[position - 1].isWhitespace()
            if (!nextAfterImage && nextAfterWord) {
                spannedText.insert(position, WORD_JOINER)
            }

            val reference = imageLoader.loadImage(
                image.url.evaluate(imageResolver).toString(),
                ImageDownloadCallbackImpl(image, imageSpan, spannedText, imageResolver, divView, textConsumer)
            )
            divView.addLoadReference(reference, textView)
        }

        textConsumer?.invoke(spannedText)
        return spannedText
    }

    private fun preprocessSpans(
        context: Context,
        textData: TextData,
        ranges: List<DivTextRangeResult>?,
    ): List<SpanData> {
        if (textData.lineHeight == null && ranges.isNullOrEmpty()) return emptyList()

        val textLength = textData.textLength

        val rangeCount = ranges?.size ?: 0
        val boundSet = sortedSetOf<Int>()
        val overlappingSpans = ArrayList<SpanData>(rangeCount + 1)

        ranges?.forEach { (range, rangeResolver) ->
            val start = range.start.evaluate(rangeResolver).toIntSafely().coerceAtMost(textLength)
            val end = range.end?.evaluate(rangeResolver)?.toIntSafely()?.coerceAtMost(textLength) ?: textLength
            if (start < end) {
                val span = createSpanData(context, rangeResolver, textData, range, start, end)
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
        images: List<DivTextImageResult>?,
    ): List<DivTextImageResult> {
        return images?.filter { it.image.start.evaluate(it.resolver) <= textData.textLength }
            ?.sortedBy { imagePosition(textData.textLength, it.image, it.resolver) } ?: emptyList()
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

        span.mask?.let { mask ->
            when (mask) {
                is MaskData.Particles -> {
                    if (mask.isEnabled) spannedText.setSpan(MaskSpan(mask, textView as? DivLineHeightTextView), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                is MaskData.Solid -> {
                    if (mask.isEnabled) spannedText.setSpan(MaskSpan(mask, textView as? DivLineHeightTextView), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
    }

    private fun addActionSpan(
        textView: TextView,
        spannedText: Spannable,
        start: Int,
        end: Int,
        actions: List<DivAction>?,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        if (actions.isNullOrEmpty()) return

        textView.movementMethod = SelectableLinkMovementMethod
        spannedText.setSpan(
            PerformActionSpan(actions, resolver, divView),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ViewCompat.enableAccessibleClickableSpanSupport(textView)
    }

    private fun addDecorationSpan(
        resolver: ExpressionResolver,
        textView: TextView,
        spannedText: Spannable,
        start: Int,
        end: Int,
        range: DivText.Range,
        textData: TextData,
        inEllipsis: Boolean,
    ) {
        val border = range.border
        val background = range.background
        if (border == null && background == null) return

        val displayMetrics = textView.context.resources.displayMetrics

        val fontSizeValue = range.fontSize?.evaluate(resolver)?.toIntSafely()
        val fontSizeUnit = range.fontSizeUnit.evaluate(resolver)

        val typefaceProvider = typefaceResolver.getTypefaceProvider(
            range.fontFamily?.evaluate(resolver) ?: textData.fontFamily
        )

        val rangeFontWeight = range.fontWeight?.evaluate(resolver)
        val rangeFontWeightValue = range.fontWeightValue?.evaluate(resolver)?.toIntSafely()
        val fontWeightValue = if (rangeFontWeight != null || rangeFontWeightValue != null) {
            getTypefaceValue(rangeFontWeight, rangeFontWeightValue)
        } else {
            getTypefaceValue(textData.fontWeight, textData.fontWeightValue)
        }

        val rangeFontVariationSettings = range.fontVariationSettings?.evaluate(resolver)
        val fontVariationSettings = makeIf(typefaceProvider.isVariable &&
            (rangeFontWeight != null || rangeFontWeightValue != null || rangeFontVariationSettings != null)
        ) {
            getFontVariations(rangeFontWeight, rangeFontWeightValue, rangeFontVariationSettings)
        }

        val backgroundSpan = DivBackgroundSpan(
            border = border,
            background = background,
            baselineOffset = range.baselineOffset.evaluate(resolver).unitToPx(displayMetrics, fontSizeUnit),
            alignmentVertical = range.alignmentVertical?.evaluate(resolver),
            lineHeight = range.lineHeight?.evaluate(resolver)?.unitToPx(displayMetrics, fontSizeUnit),
            fontSize = fontSizeValue?.unitToPx(displayMetrics, fontSizeUnit),
            topOffset = range.topOffset?.evaluate(resolver)?.toIntSafely()?.unitToPx(displayMetrics, fontSizeUnit),
            typeface = getTypeface(fontWeightValue, typefaceProvider),
            fontFeatureSettings = range.fontFeatureSettings?.evaluate(resolver),
            fontVariationSettings = fontVariationSettings,
        )
        if (textView is DivLineHeightTextView &&
            !textView.hasBackgroundSpan(spannedText, backgroundSpan, start, end, resolver)) {
            spannedText.setSpan(backgroundSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.textRoundedBgHelper?.addBackgroundSpan(backgroundSpan, inEllipsis)
        }
    }

    private fun addImageSpan(
        textView: TextView,
        spannedText: Spannable,
        textData: TextData,
        image: DivText.Image,
        resolver: ExpressionResolver,
        divView: Div2View,
    ): ImageSpan {
        val displayMetrics = textView.resources.displayMetrics

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
            val spanActions = getActionsForPosition(spannedText, position, divView)
            val onClickAction = spanActions?.let { actions ->
                ImageSpan.OnAccessibilityClickAction {
                    val actionBinder = divView.div2Component.actionBinder
                    actionBinder.handleTapClick(textView, actions, resolver, divView)
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
        spannedText: Spannable,
        position: Int,
        divView: Div2View,
    ): List<DivAction>? {
        val actionSpans = spannedText.getSpans<PerformActionSpan>(position, position + 1)

        if (actionSpans.size > 1) {
            divView.logWarning(Throwable("Two or more clickable ranges intersect."))
        }

        actionSpans.firstOrNull()?.let { span ->
            return span.actions
        }
        return null
    }

    private fun createTextData(
        context: Context,
        divText: DivText,
        text: String,
        resolver: ExpressionResolver,
    ): TextData {
        val displayMetrics = context.resources.displayMetrics
        val fontSizeValue = divText.fontSize.evaluate(resolver).toIntSafely()
        val fontSizeUnit = divText.fontSizeUnit.evaluate(resolver)
        return TextData(
            text = text,
            fontSize = fontSizeValue.unitToPx(displayMetrics, fontSizeUnit),
            fontSizeValue = fontSizeValue,
            fontSizeUnit = fontSizeUnit,
            fontWeight = divText.fontWeight?.evaluate(resolver),
            fontWeightValue = divText.fontWeightValue?.evaluate(resolver)?.toIntSafely(),
            fontFamily = divText.fontFamily?.evaluate(resolver),
            lineHeight = divText.lineHeight?.evaluate(resolver)?.toIntSafely()?.unitToPx(displayMetrics, fontSizeUnit),
            textColor = divText.textColor.evaluate(resolver)
        )
    }

    private fun createSpanData(
        context: Context,
        resolver: ExpressionResolver,
        textData: TextData,
        range: DivText.Range,
        start: Int,
        end: Int
    ) : SpanData {
        val displayMetrics = context.resources.displayMetrics
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
            mask = createMaskData(context, resolver, range.mask),
            strike = range.strike?.evaluate(resolver),
            textColor = range.textColor?.evaluate(resolver),
            textShadow = createShadowData(context, resolver, range.textShadow, textData.textColor),
            topOffset = range.topOffset?.evaluate(resolver)?.toIntSafely()?.unitToPx(displayMetrics, fontSizeUnit),
            topOffsetStart = if (range.topOffset != null) start else null,
            topOffsetEnd = if (range.topOffset != null) end else null,
            underline = range.underline?.evaluate(resolver)
        )
    }

    private fun createShadowData(
        context: Context,
        resolver: ExpressionResolver,
        shadow: DivShadow?,
        @ColorInt textColor: Int
    ): ShadowData? {
        if (shadow == null) return null

        val displayMetrics = context.resources.displayMetrics
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

    private fun createMaskData(
        context: Context,
        resolver: ExpressionResolver,
        mask: DivTextRangeMask?
    ): MaskData? {
        if (mask == null) return null

        val displayMetrics = context.resources.displayMetrics
        return when (mask) {
            is DivTextRangeMask.Particles -> {
                val value = mask.value
                val particleSizeValue = value.particleSize.value.evaluate(resolver)
                val particleSizeUnit  = value.particleSize.unit.evaluate(resolver)
                MaskData.Particles(
                    color = value.color.evaluate(resolver),
                    density = value.density.evaluate(resolver).toFloat(),
                    isAnimated = value.isAnimated.evaluate(resolver),
                    isEnabled = value.isEnabled.evaluate(resolver),
                    particleSize = particleSizeValue.unitToPxF(displayMetrics, particleSizeUnit)
                )
            }
            is DivTextRangeMask.Solid -> {
                val value = mask.value
                MaskData.Solid(
                    color = value.color.evaluate(resolver),
                    isEnabled = value.isEnabled.evaluate(resolver)
                )
            }
        }
    }

    private fun DivTextAlignmentVertical.toTextVerticalAlignment(): TextVerticalAlignment {
        return when (this) {
            DivTextAlignmentVertical.TOP -> TextVerticalAlignment.TOP
            DivTextAlignmentVertical.CENTER -> TextVerticalAlignment.CENTER
            DivTextAlignmentVertical.BASELINE -> TextVerticalAlignment.BASELINE
            DivTextAlignmentVertical.BOTTOM -> TextVerticalAlignment.BOTTOM
            else -> TextVerticalAlignment.BASELINE
        }
    }

    private class ImageDownloadCallbackImpl(
        private val image: DivText.Image,
        private val imageSpan: ImageSpan,
        private val spannedText: Spanned,
        private val resolver: ExpressionResolver,
        private val divView: Div2View,
        private val textConsumer: TextConsumer? = null
    ) : DivIdLoggingImageDownloadCallback(divView) {

        override fun dispatchDrawable(drawable: Drawable, source: BitmapSource) {
            when (drawable) {
                is PictureDrawable -> onSuccess(drawable, source)
                else -> onSuccess(drawable, source)
            }
        }

        override fun onSuccess(bitmap: Bitmap, source: BitmapSource) {
            val resources = divView.resources
            onSuccess(bitmap.toDrawable(resources), source)
        }

        override fun onSuccess(drawable: Drawable, source: BitmapSource) {
            image.tintColor?.evaluate(resolver)?.let {
                val tintMode = image.tintMode.evaluate(resolver).toPorterDuffMode()
                drawable.colorFilter = PorterDuffColorFilter(it, tintMode)
            }
            setImage(drawable)
        }

        override fun onSuccess(pictureDrawable: PictureDrawable, source: BitmapSource) {
            if (image.isVectorCompatible) {
                setImage(pictureDrawable)
                return
            }
            onSuccess(pictureDrawable.toBitmap(), source)
        }

        private fun setImage(image: Drawable) {
            imageSpan.image = image
            textConsumer?.invoke(spannedText)
        }

        private val DivText.Image.isVectorCompatible get() = tintColor == null
    }

    private companion object {
        private const val IMAGE_PLACEHOLDER = "#"
        private const val WORD_JOINER = "\u2060"
        private const val ZWSP = "\u200B"
    }
}
