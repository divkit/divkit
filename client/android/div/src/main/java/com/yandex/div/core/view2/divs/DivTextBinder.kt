package com.yandex.div.core.view2.divs

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Paint
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.core.view.doOnLayout
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.HYPHENATION_SUPPORT_ENABLED
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.text.DivTextRangesBackgroundHelper
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.spannable.LineHeightWithTopOffsetSpan
import com.yandex.div.core.widget.AdaptiveMaxLines
import com.yandex.div.internal.drawable.LinearGradientDrawable
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.expression.Expression
import com.yandex.div.internal.spannable.BitmapImageSpan
import com.yandex.div.internal.spannable.ImagePlaceholderSpan
import com.yandex.div.internal.spannable.LetterSpacingSpan
import com.yandex.div.internal.spannable.NoStrikethroughSpan
import com.yandex.div.internal.spannable.NoUnderlineSpan
import com.yandex.div.internal.spannable.TypefaceSpan
import com.yandex.div.internal.util.checkHyphenationSupported
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivFontFamily
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivLineStyle
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientFixedCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivRadialGradientRelativeCenter
import com.yandex.div2.DivRadialGradientRelativeRadius
import com.yandex.div2.DivSolidBackground
import com.yandex.div2.DivText
import com.yandex.div2.DivTextGradient
import com.yandex.div2.DivTextRangeBackground
import com.yandex.div2.DivTextRangeBorder
import javax.inject.Inject
import kotlin.math.min

private const val SOFT_HYPHEN = '\u00AD'
private const val LONGEST_WORD_BREAK = 10

/**
 * Class for binding div text to templated view
 */
@DivScope
internal class DivTextBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val typefaceResolver: DivTypefaceResolver,
    private val imageLoader: DivImageLoader,
    @ExperimentFlag(HYPHENATION_SUPPORT_ENABLED) private val isHyphenationEnabled: Boolean
) : DivViewBinder<DivText, DivLineHeightTextView> {

    override fun bindView(view: DivLineHeightTextView, div: DivText, divView: Div2View) {
        val oldDiv = view.div
        if (div == oldDiv) return

        val expressionResolver = divView.expressionResolver
        view.closeAllSubscription()
        view.textRoundedBgHelper = DivTextRangesBackgroundHelper(view, expressionResolver)

        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, oldDiv, divView)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        view.observeTypeface(div, expressionResolver)
        view.observeTextAlignment(div.textAlignmentHorizontal, div.textAlignmentVertical, expressionResolver)
        view.observeFontSize(expressionResolver, div)
        view.observeLineHeight(expressionResolver, div)
        view.observeTextColor(div, expressionResolver)
        view.addSubscription(
            div.underline.observeAndGet(expressionResolver) { underline -> view.applyUnderline(underline) }
        )
        view.addSubscription(
            div.strike.observeAndGet(expressionResolver) { strike -> view.applyStrike(strike) }
        )
        view.observeMaxLines(expressionResolver, div.maxLines, div.minHiddenLines)
        view.observeText(divView, expressionResolver, div)
        view.observeEllipsis(divView, expressionResolver, div)
        view.observeAutoEllipsize(expressionResolver, div.autoEllipsize)
        view.observeTextGradient(expressionResolver, div.textGradient)
        view.addSubscription(
            div.selectable.observeAndGet(expressionResolver) { selectable -> view.applySelectable(selectable) }
        )
        view.updateFocusableState(div)
    }

    private fun TextView.applyHyphenation(resolver: ExpressionResolver, div: DivText) {
        if (!checkHyphenationSupported()) {
            return
        }
        val oldHyphenFreq = hyphenationFrequency
        val newHyphenFreq = when {
            !isHyphenationEnabled -> Layout.HYPHENATION_FREQUENCY_NONE
            TextUtils.indexOf(
                div.text.evaluate(resolver),
                SOFT_HYPHEN,
                0,
                min(div.text.evaluate(resolver).length, LONGEST_WORD_BREAK)
            ) > 0 -> {
                // This enables word break not only on soft hyphens, but on dashes and hyphens.
                // See all characters that lead to word break https://cs.android.com/android/platform/superproject/+/master:frameworks/minikin/libs/minikin/Hyphenator.cpp;l=146
                Layout.HYPHENATION_FREQUENCY_NORMAL
            }

            else -> Layout.HYPHENATION_FREQUENCY_NONE
        }
        if (oldHyphenFreq != newHyphenFreq) {
            hyphenationFrequency = newHyphenFreq
        }
    }

    private fun DivLineHeightTextView.observeTextAlignment(
        horizontalAlignment: Expression<DivAlignmentHorizontal>,
        verticalAlignment: Expression<DivAlignmentVertical>,
        resolver: ExpressionResolver
    ) {
        applyTextAlignment(horizontalAlignment.evaluate(resolver), verticalAlignment.evaluate(resolver))

        val callback = { _: Any ->
            applyTextAlignment(horizontalAlignment.evaluate(resolver), verticalAlignment.evaluate(resolver))
        }
        addSubscription(horizontalAlignment.observe(resolver, callback))
        addSubscription(verticalAlignment.observe(resolver, callback))
    }

    private fun TextView.applyTextAlignment(
        horizontalAlignment: DivAlignmentHorizontal,
        verticalAlignment: DivAlignmentVertical
    ) {
        gravity = evaluateGravity(horizontalAlignment, verticalAlignment)
        textAlignment = when (horizontalAlignment) {
            DivAlignmentHorizontal.LEFT -> TextView.TEXT_ALIGNMENT_VIEW_START
            DivAlignmentHorizontal.CENTER -> TextView.TEXT_ALIGNMENT_CENTER
            DivAlignmentHorizontal.RIGHT -> TextView.TEXT_ALIGNMENT_VIEW_END
            else -> TextView.TEXT_ALIGNMENT_VIEW_START
        }
    }

    private fun DivLineHeightTextView.observeMaxLines(
        resolver: ExpressionResolver,
        maxLines: Expression<Int>?,
        minHiddenLines: Expression<Int>?
    ) {
        applyMaxLines(resolver, maxLines, minHiddenLines)

        val callback = { _: Any -> applyMaxLines(resolver, maxLines, minHiddenLines) }
        addSubscription(div?.maxLines?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(div?.minHiddenLines?.observe(resolver, callback) ?: Disposable.NULL)
    }

    private fun DivLineHeightTextView.applyMaxLines(
        resolver: ExpressionResolver,
        maxLinesExpr: Expression<Int>?,
        minHiddenLinesExpr: Expression<Int>?
    ) {
        adaptiveMaxLines?.reset()

        val maxLines = maxLinesExpr?.evaluate(resolver)
        val minHiddenLines = minHiddenLinesExpr?.evaluate(resolver)
        if (maxLines != null && minHiddenLines != null) {
            adaptiveMaxLines = AdaptiveMaxLines(this).also {
                it.apply(AdaptiveMaxLines.Params(maxLines = maxLines, minHiddenLines = minHiddenLines))
            }
        } else {
            this.maxLines = maxLines ?: Integer.MAX_VALUE
        }
    }

    private fun DivLineHeightTextView.observeFontSize(resolver: ExpressionResolver, div: DivText) {
        applyFontSize(resolver, div)

        val callback = { _: Any -> applyFontSize(resolver, div) }
        addSubscription(div.fontSize.observe(resolver, callback))
        addSubscription(div.letterSpacing.observe(resolver, callback))
    }

    private fun DivLineHeightTextView.applyFontSize(resolver: ExpressionResolver, div: DivText) {
        val fontSize = div.fontSize.evaluate(resolver)
        applyFontSize(fontSize, div.fontSizeUnit.evaluate(resolver))
        applyLetterSpacing(div.letterSpacing.evaluate(resolver), fontSize)
    }

    private fun DivLineHeightTextView.observeTypeface(
        div: DivText,
        resolver: ExpressionResolver,
    ) {
        applyTypeface(div.fontFamily.evaluate(resolver), div.fontWeight.evaluate(resolver))

        val callback = { _: Any ->
            applyTypeface(div.fontFamily.evaluate(resolver), div.fontWeight.evaluate(resolver))
        }
        addSubscription(div.fontFamily.observe(resolver, callback))
        addSubscription(div.fontWeight.observe(resolver, callback))
    }

    private fun TextView.applyTypeface(
        fontFamily: DivFontFamily,
        fontWeight: DivFontWeight,
    ) {
        typeface = typefaceResolver.getTypeface(fontFamily, fontWeight)
    }

    private fun DivLineHeightTextView.observeLineHeight(resolver: ExpressionResolver, div: DivText) {
        val lineHeightExpr = div.lineHeight
        if (lineHeightExpr == null) {
            applyLineHeight(null, div.fontSizeUnit.evaluate(resolver))
            return
        }

        addSubscription(
            lineHeightExpr.observeAndGet(resolver) { lineHeight -> applyLineHeight(lineHeight, div.fontSizeUnit.evaluate(resolver)) }
        )
    }

    private fun TextView.observeTextColor(
        div: DivText,
        expressionResolver: ExpressionResolver
    ) {
        var defaultColor = div.textColor.evaluate(expressionResolver)
        var focusedColor = div.focusedTextColor?.evaluate(expressionResolver)

        val updateTextColor = {
            setTextColor(
                ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_focused),
                        intArrayOf(),
                    ),
                    intArrayOf(
                        focusedColor ?: defaultColor,
                        defaultColor,
                    ),
                )
            )
        }

        updateTextColor()

        div.textColor.observe(expressionResolver) { newUnfocusedColor ->
            defaultColor = newUnfocusedColor
            updateTextColor()
        }

        div.focusedTextColor?.observe(expressionResolver) { newFocusedColor ->
            focusedColor = newFocusedColor
            updateTextColor()
        }
    }

    private fun TextView.applyUnderline(underline: DivLineStyle) {
        when(underline) {
            DivLineStyle.SINGLE -> paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            DivLineStyle.NONE -> paintFlags = paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            else -> Unit
        }
    }

    private fun TextView.applyStrike(strike: DivLineStyle) {
        when(strike) {
            DivLineStyle.SINGLE -> paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            DivLineStyle.NONE -> paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            else -> Unit
        }
    }

    private fun TextView.applySelectable(selectable: Boolean) {
        setTextIsSelectable(selectable)
    }

    private fun DivLineHeightTextView.observeTextGradient(
        resolver: ExpressionResolver,
        textGradient: DivTextGradient?
    ) {
        applyTextGradientColor(resolver, textGradient)
        if (textGradient == null) return

        val callback = { _: Any -> applyTextGradientColor(resolver, textGradient) }
        when (val gradient = textGradient.value()) {
            is DivLinearGradient -> {
                addSubscription(gradient.angle.observe(resolver, callback))
            }
            is DivRadialGradient -> {
                gradient.centerX.observe(resolver, this, callback)
                gradient.centerY.observe(resolver, this, callback)
                gradient.radius.observe(resolver, this, callback)
            }
        }
    }

    private fun TextView.applyTextGradientColor(
        resolver: ExpressionResolver,
        gradient: DivTextGradient?
    ) {
        val metrics = resources.displayMetrics
        doOnLayout {
            this.paint.shader = when (val gradientBackground = gradient?.value()) {
                is DivLinearGradient -> LinearGradientDrawable.createLinearGradient(
                    angle = gradientBackground.angle.evaluate(resolver).toFloat(),
                    colors = gradientBackground.colors.evaluate(resolver).toIntArray(),
                    width = width,
                    height = height
                )
                is DivRadialGradient -> RadialGradientDrawable.createRadialGradient(
                    radius = gradientBackground.radius.toRadialGradientDrawableRadius(metrics, resolver)!!,
                    centerX = gradientBackground.centerX.toRadialGradientDrawableCenter(metrics, resolver)!!,
                    centerY = gradientBackground.centerY.toRadialGradientDrawableCenter(metrics, resolver)!!,
                    colors = gradientBackground.colors.evaluate(resolver).toIntArray(),
                    width = width,
                    height = height
                )
                else -> null
            }
        }
    }

    private fun DivRadialGradientRadius.toRadialGradientDrawableRadius(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ) = when (val radius = this.value()) {
        is DivFixedSize -> RadialGradientDrawable.Radius.Fixed(
            radius.value.evaluate(resolver).dpToPxF(metrics)
        )
        is DivRadialGradientRelativeRadius -> RadialGradientDrawable.Radius.Relative(
            when (radius.value.evaluate(resolver)) {
                DivRadialGradientRelativeRadius.Value.FARTHEST_CORNER -> RadialGradientDrawable.Radius.Relative.Type.FARTHEST_CORNER
                DivRadialGradientRelativeRadius.Value.NEAREST_CORNER -> RadialGradientDrawable.Radius.Relative.Type.NEAREST_CORNER
                DivRadialGradientRelativeRadius.Value.FARTHEST_SIDE -> RadialGradientDrawable.Radius.Relative.Type.FARTHEST_SIDE
                DivRadialGradientRelativeRadius.Value.NEAREST_SIDE -> RadialGradientDrawable.Radius.Relative.Type.NEAREST_SIDE
            }
        )
        else -> null
    }

    private fun DivRadialGradientCenter.toRadialGradientDrawableCenter(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ) = when (val center = this.value()) {
        is DivRadialGradientFixedCenter -> RadialGradientDrawable.Center.Fixed(
            center.value.evaluate(resolver).dpToPxF(metrics)
        )
        is DivRadialGradientRelativeCenter -> RadialGradientDrawable.Center.Relative(
            center.value.evaluate(resolver).toFloat()
        )
        else -> null
    }

    private fun DivLineHeightTextView.observeText(
        divView: Div2View,
        resolver: ExpressionResolver,
        div: DivText
    ) {
        if (div.ranges == null && div.images == null) {
            observeTextOnly(resolver, div)
            return
        }

        applyText(divView, resolver, div)
        applyHyphenation(resolver, div)

        addSubscription(
            div.text.observe(resolver) {
                applyText(divView, resolver, div)
                applyHyphenation(resolver, div)
            }
        )

        val callback = { _: Any -> applyText(divView, resolver, div) }
        div.ranges?.forEach { range ->
            addSubscription(range.start.observe(resolver, callback))
            addSubscription(range.end.observe(resolver, callback))
            addSubscription(range.fontSize?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.fontSizeUnit.observe(resolver, callback))
            addSubscription(range.fontWeight?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.letterSpacing?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.lineHeight?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.strike?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.textColor?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.topOffset?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.underline?.observe(resolver, callback) ?: Disposable.NULL)
        }
        div.images?.forEach { image ->
            addSubscription(image.start.observe(resolver, callback))
            addSubscription(image.url.observe(resolver, callback))
            addSubscription(image.tintColor?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(image.width.value.observe(resolver, callback))
            addSubscription(image.width.unit.observe(resolver, callback))
        }
    }

    /**
     * Used in most common case, where there is no [DivText.ranges] or [DivText.images]
     * to bind [DivText.text] as quick as possible.
     */
    private fun DivLineHeightTextView.observeTextOnly(
        resolver: ExpressionResolver,
        div: DivText
    ) {
        applyTextOnly(resolver, div)
        applyHyphenation(resolver, div)

        addSubscription(
            div.text.observe(resolver) {
                applyTextOnly(resolver, div)
                applyHyphenation(resolver, div)
            }
        )
    }

    private fun DivLineHeightTextView.applyText(
        divView: Div2View,
        resolver: ExpressionResolver,
        div: DivText
    ) {
        val ranger = DivTextRanger(
            divView,
            this,
            resolver,
            div.text.evaluate(resolver),
            div.fontSize.evaluate(resolver),
            div.fontFamily.evaluate(resolver),
            div.ranges,
            null,
            div.images
        )
        ranger.onTextChanged { text ->
            setText(text, TextView.BufferType.NORMAL)
        }
        ranger.run()
    }

    private fun TextView.applyTextOnly(
        resolver: ExpressionResolver,
        div: DivText
    ) {
        text = div.text.evaluate(resolver)
    }

    private fun DivLineHeightTextView.observeEllipsis(
        divView: Div2View,
        resolver: ExpressionResolver,
        div: DivText
    ) {
        applyEllipsis(divView, resolver, div)

        val ellipsis = div.ellipsis ?: return
        val callback = { _: Any -> applyEllipsis(divView, resolver, div) }
        addSubscription(ellipsis.text.observe(resolver, callback))
        ellipsis.ranges?.forEach { range ->
            addSubscription(range.start.observe(resolver, callback))
            addSubscription(range.end.observe(resolver, callback))
            addSubscription(range.fontSize?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.fontSizeUnit.observe(resolver, callback))
            addSubscription(range.fontWeight?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.letterSpacing?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.lineHeight?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.strike?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.textColor?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.topOffset?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.underline?.observe(resolver, callback) ?: Disposable.NULL)
            when (val background = range.background?.value()) {
                is DivSolidBackground -> addSubscription(background.color.observe(resolver, callback))
            }
            addSubscription(range.border?.stroke?.color?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(range.border?.stroke?.width?.observe(resolver, callback) ?: Disposable.NULL)
        }
        ellipsis.images?.forEach { image ->
            addSubscription(image.start.observe(resolver, callback))
            addSubscription(image.url.observe(resolver, callback))
            addSubscription(image.tintColor?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(image.width.value.observe(resolver, callback))
            addSubscription(image.width.unit.observe(resolver, callback))
        }
    }

    private fun DivLineHeightTextView.applyEllipsis(
        divView: Div2View,
        resolver: ExpressionResolver,
        div: DivText
    ) {
        val divEllipsis = div.ellipsis ?: return
        val ranger = DivTextRanger(
            divView,
            this,
            resolver,
            divEllipsis.text.evaluate(resolver),
            div.fontSize.evaluate(resolver),
            div.fontFamily.evaluate(resolver),
            divEllipsis.ranges,
            divEllipsis.actions,
            divEllipsis.images
        )
        ranger.onTextChanged { text ->
            ellipsis = text
        }
        ranger.run()
    }

    private fun DivLineHeightTextView.observeAutoEllipsize(
        resolver: ExpressionResolver,
        autoEllipsizeExpr: Expression<Boolean>?
    ) {
        if (autoEllipsizeExpr == null) {
            autoEllipsize = false
            return
        }

        autoEllipsize = autoEllipsizeExpr.evaluate(resolver)
    }

    private inner class DivTextRanger(
        private val divView: Div2View,
        private val textView: DivLineHeightTextView,
        private val resolver: ExpressionResolver,
        private val text: String,
        private val fontSize: Int,
        private val fontFamily: DivFontFamily,
        private val ranges: List<DivText.Range>?,
        private val actions: List<DivAction>?,
        images: List<DivText.Image>?
    ) {

        private val context = divView.context
        private val metrics = divView.resources.displayMetrics
        private val sb: SpannableStringBuilder = SpannableStringBuilder(text)
        private val images = images?.filter { it.start.evaluate(resolver) <= text.length }?.sortedBy { it.start.evaluate(resolver) } ?: emptyList()

        private var textObserver: ((CharSequence) -> Unit)? = null

        fun onTextChanged(action: (CharSequence) -> Unit) {
            textObserver = action
        }

        fun run() {
            if (ranges.isNullOrEmpty() && images.isNullOrEmpty()) {
                textObserver?.invoke(text)
                return
            }

            ranges?.forEach { item -> sb.addTextRange(item) }
            images.reversed().forEach {
                sb.insert(it.start.evaluate(resolver), "#")
            }

            images.forEachIndexed { index, image ->
                val width = image.width.toPx(metrics, resolver)
                val height = image.height.toPx(metrics, resolver)

                val offsetY = if (sb.isNotEmpty()) {
                    val charIndex = if (image.start.evaluate(resolver) == 0) 0 else image.start.evaluate(resolver) - 1
                    val sizeSpans = sb.getSpans(charIndex, charIndex + 1, AbsoluteSizeSpan::class.java)
                    val paint = textView.paint
                    val scale = if (sizeSpans != null && sizeSpans.isNotEmpty()) {
                        sizeSpans[0].size / textView.textSize
                    } else 1.0F
                    val textMidY = (paint.ascent() + paint.descent()) / 2 * scale
                    val desiredMidY = -height.toFloat() / 2

                    textMidY - desiredMidY
                } else 0.0F

                val span = ImagePlaceholderSpan(width, height, offsetY)

                val start = image.start.evaluate(resolver) + index
                sb.setSpan(span, start, start + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }

            actions?.let {
                textView.movementMethod = LinkMovementMethod.getInstance()
                sb.setSpan(DivClickableSpan(it), 0, sb.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }

            textObserver?.invoke(sb)

            images.forEachIndexed { index, image ->
                val reference = imageLoader.loadImage(image.url.evaluate(resolver).toString(), ImageCallback(index))
                divView.addLoadReference(reference, textView)
            }
        }

        private fun SpannableStringBuilder.addTextRange(range: DivText.Range) {
            val start = range.start.evaluate(resolver).coerceAtMost(text.length)
            val end = range.end.evaluate(resolver).coerceAtMost(text.length)
            if (start > end) return

            range.fontSize?.evaluate(resolver)?.let {
                setSpan(AbsoluteSizeSpan(it.unitToPx(metrics, range.fontSizeUnit.evaluate(resolver))), start, end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            range.textColor?.evaluate(resolver)?.let {
                setSpan(ForegroundColorSpan(it), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            range.letterSpacing?.evaluate(resolver)?.let {
                val fontSize = range.fontSize?.evaluate(resolver) ?: fontSize
                val letterSpacingEm = it.toFloat() / fontSize
                setSpan(LetterSpacingSpan(letterSpacingEm), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            range.strike?.let {
                when(it.evaluate(resolver)) {
                    DivLineStyle.SINGLE -> setSpan(StrikethroughSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    DivLineStyle.NONE -> setSpan(NoStrikethroughSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    else -> Unit
                }
            }
            range.underline?.let {
                when(it.evaluate(resolver)) {
                    DivLineStyle.SINGLE -> setSpan(UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    DivLineStyle.NONE -> setSpan(NoUnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    else -> Unit
                }
            }
            range.fontWeight?.let {
                setSpan(TypefaceSpan(typefaceResolver.getTypeface(fontFamily, it.evaluate(resolver))), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            range.actions?.let {
                textView.movementMethod = LinkMovementMethod.getInstance()
                setSpan(DivClickableSpan(it), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            if (range.border != null || range.background != null) {
                val span = DivBackgroundSpan(range.border, range.background)
                if (!textView.hasSuchSpan(span, start, end)) {
                    setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
            if (range.lineHeight != null || range.topOffset != null) {
                val offset = range.topOffset?.evaluate(resolver).unitToPx(metrics, range.fontSizeUnit.evaluate(resolver))
                val lineHeight = range.lineHeight?.evaluate(resolver).unitToPx(metrics, range.fontSizeUnit.evaluate(resolver))
                setSpan(LineHeightWithTopOffsetSpan(offset, lineHeight), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }

        private fun TextView.hasSuchSpan(backgroundSpan: DivBackgroundSpan, start: Int, end: Int): Boolean {
            val spannable = text as? Spannable ?: return false
            val spans = spannable.getSpans(0, text.length, DivBackgroundSpan::class.java)
            return spans.any { span ->
                    span.border == backgroundSpan.border && span.background == backgroundSpan.background
                    && end == spannable.getSpanEnd(span) && start == spannable.getSpanStart(span)
            }
        }

        private fun SpannableStringBuilder.makeImageSpan(
            range: DivText.Image,
            bitmap: Bitmap
        ): BitmapImageSpan {
            val imageHeight = range.height.toPx(metrics, resolver)
            val offsetY = if (!isEmpty()) {
                val charIndex = if (range.start.evaluate(resolver) == 0) 0 else range.start.evaluate(resolver) - 1
                val sizeSpans = getSpans(charIndex, charIndex + 1, AbsoluteSizeSpan::class.java)
                val paint = textView.paint
                val scale = if (sizeSpans != null && sizeSpans.isNotEmpty()) {
                    sizeSpans[0].size / textView.textSize
                } else 1.0F
                val textMidY = (paint.ascent() + paint.descent()) / 2 * scale
                val desiredMidY = -imageHeight.toFloat() / 2
                textMidY - desiredMidY
            } else 0.0F

            return BitmapImageSpan(
                context,
                bitmap,
                offsetY,
                range.width.toPx(metrics, resolver),
                imageHeight,
                range.tintColor?.evaluate(resolver),
                range.tintMode.evaluate(resolver).toPorterDuffMode(),
                isSquare = false,
                anchorPoint = BitmapImageSpan.AnchorPoint.BASELINE
            )
        }

        private inner class ImageCallback(private val index: Int) : DivIdLoggingImageDownloadCallback(divView) {

            override fun onSuccess(cachedBitmap: CachedBitmap) {
                super.onSuccess(cachedBitmap)
                val image = images[index]
                val span = sb.makeImageSpan(image, cachedBitmap.bitmap)
                val start = image.start.evaluate(resolver) + index
                sb.getSpans<ImagePlaceholderSpan>(start, start + 1).forEach { sb.removeSpan(it) }
                sb.setSpan(span, start, start + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                textObserver?.invoke(sb)
            }
        }

        private inner class DivClickableSpan(private val actions: List<DivAction>) : ClickableSpan() {
            override fun onClick(p0: View) {
                val actionBinder = divView.div2Component.actionBinder
                actionBinder.handleTapClick(divView, p0, actions)
            }

            override fun updateDrawState(ds: TextPaint) {
                // don't call super
            }
        }
    }

    private fun View.updateFocusableState(div: DivText) {
        isFocusable = isFocusable || (div.focusedTextColor != null)
    }
}

internal class DivBackgroundSpan(val border: DivTextRangeBorder?,
                        val background: DivTextRangeBackground?) : UnderlineSpan() {
    var cache: Cache? = null

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
    }

    class Cache(
        val startLine: Int,
        val endLine: Int,
        val startOffset: Int,
        val endOffset: Int
    )
}
