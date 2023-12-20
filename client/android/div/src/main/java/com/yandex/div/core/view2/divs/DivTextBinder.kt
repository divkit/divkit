package com.yandex.div.core.view2.divs

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import androidx.core.text.getSpans
import androidx.core.view.ViewCompat
import com.yandex.div.core.DivIdLoggingImageDownloadCallback
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.HYPHENATION_SUPPORT_ENABLED
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.text.DivBackgroundSpan
import com.yandex.div.core.util.text.DivTextRangesBackgroundHelper
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.spannable.LineHeightWithTopOffsetSpan
import com.yandex.div.core.view2.spannable.ShadowSpan
import com.yandex.div.core.view2.spannable.ShadowSpan.ShadowParams
import com.yandex.div.core.widget.AdaptiveMaxLines
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.drawable.LinearGradientDrawable
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.spannable.BitmapImageSpan
import com.yandex.div.internal.spannable.ImagePlaceholderSpan
import com.yandex.div.internal.spannable.LetterSpacingSpan
import com.yandex.div.internal.spannable.NoStrikethroughSpan
import com.yandex.div.internal.spannable.NoUnderlineSpan
import com.yandex.div.internal.spannable.TextColorSpan
import com.yandex.div.internal.spannable.TypefaceSpan
import com.yandex.div.internal.util.checkHyphenationSupported
import com.yandex.div.internal.widget.EllipsizedTextView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivLineStyle
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivRadialGradientRelativeRadius
import com.yandex.div2.DivShadow
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivSolidBackground
import com.yandex.div2.DivText
import com.yandex.div2.DivTextGradient
import javax.inject.Inject
import kotlin.math.min

private const val SOFT_HYPHEN = '\u00AD'
private const val WORD_JOINER = "\u2060"
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

        baseBinder.bindView(view, div, oldDiv, divView)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        val expressionResolver = divView.expressionResolver
        view.bindTypeface(div, oldDiv, expressionResolver)
        view.bindTextAlignment(div, oldDiv, expressionResolver)
        view.bindFontSize(div, oldDiv, expressionResolver)
        view.bindLineHeight(div, oldDiv, expressionResolver)
        view.bindTextColor(div, oldDiv, expressionResolver)
        view.bindUnderline(div, oldDiv, expressionResolver)
        view.bindStrikethrough(div, oldDiv, expressionResolver)
        view.bindMaxLines(div, oldDiv, expressionResolver)
        view.bindText(divView, div, oldDiv, expressionResolver)
        view.bindEllipsis(divView, div, oldDiv, expressionResolver)
        view.bindAutoEllipsize(div, oldDiv, expressionResolver)
        view.bindTextGradient(div, oldDiv, expressionResolver)
        view.bindTextShadow(div, oldDiv, expressionResolver)
        view.bindSelectable(div, oldDiv, expressionResolver)
        view.updateFocusableState(div)
    }

    //region Text Alignment

    private fun DivLineHeightTextView.bindTextAlignment(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.textAlignmentHorizontal.equalsToConstant(oldDiv?.textAlignmentHorizontal)
            && newDiv.textAlignmentVertical.equalsToConstant(oldDiv?.textAlignmentVertical)) {
            return
        }

        applyTextAlignment(
            newDiv.textAlignmentHorizontal.evaluate(resolver),
            newDiv.textAlignmentVertical.evaluate(resolver)
        )

        if (newDiv.textAlignmentHorizontal.isConstant() && newDiv.textAlignmentVertical.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyTextAlignment(
                newDiv.textAlignmentHorizontal.evaluate(resolver),
                newDiv.textAlignmentVertical.evaluate(resolver)
            )
        }
        addSubscription(newDiv.textAlignmentHorizontal.observe(resolver, callback))
        addSubscription(newDiv.textAlignmentVertical.observe(resolver, callback))
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
            DivAlignmentHorizontal.START -> TextView.TEXT_ALIGNMENT_VIEW_START
            DivAlignmentHorizontal.END -> TextView.TEXT_ALIGNMENT_VIEW_END
            else -> TextView.TEXT_ALIGNMENT_VIEW_START
        }
    }

    //endregion

    //region Max Lines

    private fun DivLineHeightTextView.bindMaxLines(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.maxLines.equalsToConstant(oldDiv?.maxLines)
            && newDiv.minHiddenLines.equalsToConstant(oldDiv?.minHiddenLines)) {
            return
        }

        applyMaxLines(newDiv.maxLines?.evaluate(resolver), newDiv.minHiddenLines?.evaluate(resolver))

        if (newDiv.maxLines.isConstantOrNull() && newDiv.minHiddenLines.isConstantOrNull()) {
            return
        }

        val callback = { _: Any ->
            applyMaxLines(newDiv.maxLines?.evaluate(resolver), newDiv.minHiddenLines?.evaluate(resolver))
        }
        addSubscription(newDiv.maxLines?.observe(resolver, callback))
        addSubscription(newDiv.minHiddenLines?.observe(resolver, callback))
    }

    private fun DivLineHeightTextView.applyMaxLines(
        maxLines: Long?,
        minHiddenLines: Long?
    ) {
        adaptiveMaxLines?.reset()

        if (maxLines != null && minHiddenLines != null) {
            adaptiveMaxLines = AdaptiveMaxLines(this).also {
                it.apply(AdaptiveMaxLines.Params(
                        maxLines = maxLines.toIntSafely(),
                        minHiddenLines = minHiddenLines.toIntSafely()
                ))
            }
        } else {
            this.maxLines = maxLines?.toIntSafely() ?: Integer.MAX_VALUE
        }
    }

    //endregion

    //region Font Size

    private fun DivLineHeightTextView.bindFontSize(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.fontSize.equalsToConstant(oldDiv?.fontSize)
            && newDiv.fontSizeUnit.equalsToConstant(oldDiv?.fontSizeUnit)
            && newDiv.letterSpacing.equalsToConstant(oldDiv?.letterSpacing)) {
            return
        }

        applyFontSize(
            newDiv.fontSize.evaluate(resolver),
            newDiv.fontSizeUnit.evaluate(resolver),
            newDiv.letterSpacing.evaluate(resolver)
        )

        if (newDiv.fontSize.isConstant()
            && newDiv.fontSizeUnit.isConstant()
            && newDiv.letterSpacing.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyFontSize(
                newDiv.fontSize.evaluate(resolver),
                newDiv.fontSizeUnit.evaluate(resolver),
                newDiv.letterSpacing.evaluate(resolver)
            )
        }
        addSubscription(newDiv.fontSize.observe(resolver, callback))
        addSubscription(newDiv.fontSizeUnit.observe(resolver, callback))
        addSubscription(newDiv.letterSpacing.observe(resolver, callback))
    }

    private fun TextView.applyFontSize(size: Long, unit: DivSizeUnit, letterSpacing: Double) {
        val fontSize = size.toIntSafely()
        applyFontSize(fontSize, unit)
        applyLetterSpacing(letterSpacing, fontSize)
    }

    //endregion

    //region Typeface

    private fun DivLineHeightTextView.bindTypeface(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.fontFamily.equalsToConstant(oldDiv?.fontFamily)
            && newDiv.fontWeight.equalsToConstant(oldDiv?.fontWeight)) {
            return
        }

        applyTypeface(newDiv.fontFamily?.evaluate(resolver), newDiv.fontWeight.evaluate(resolver))

        if (newDiv.fontFamily.isConstantOrNull() && newDiv.fontWeight.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyTypeface(newDiv.fontFamily?.evaluate(resolver), newDiv.fontWeight.evaluate(resolver))
        }
        addSubscription(newDiv.fontFamily?.observe(resolver, callback))
        addSubscription(newDiv.fontWeight.observe(resolver, callback))
    }

    private fun TextView.applyTypeface(
        fontFamily: String?,
        fontWeight: DivFontWeight,
    ) {
        typeface = typefaceResolver.getTypeface(fontFamily, fontWeight)
    }

    //endregion

    //region Line Height

    private fun DivLineHeightTextView.bindLineHeight(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.lineHeight.equalsToConstant(oldDiv?.lineHeight)
            && newDiv.fontSizeUnit.equalsToConstant(oldDiv?.fontSizeUnit)) {
            return
        }

        applyLineHeight(newDiv.lineHeight?.evaluate(resolver), newDiv.fontSizeUnit.evaluate(resolver))

        if (newDiv.lineHeight.isConstantOrNull() && newDiv.fontSizeUnit.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyLineHeight(newDiv.lineHeight?.evaluate(resolver), newDiv.fontSizeUnit.evaluate(resolver))
        }
        addSubscription(newDiv.lineHeight?.observe(resolver, callback))
        addSubscription(newDiv.fontSizeUnit.observe(resolver, callback))
    }

    //endregion

    //region Text Color

    private fun DivLineHeightTextView.bindTextColor(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.textColor.equalsToConstant(oldDiv?.textColor)
            && newDiv.focusedTextColor.equalsToConstant(oldDiv?.focusedTextColor)) {
            return
        }

        applyTextColor(newDiv.textColor.evaluate(resolver), newDiv.focusedTextColor?.evaluate(resolver))

        if (newDiv.textColor.isConstant() && newDiv.focusedTextColor.isConstantOrNull()) {
            return
        }

        val callback = { _: Any ->
            applyTextColor(newDiv.textColor.evaluate(resolver), newDiv.focusedTextColor?.evaluate(resolver))
        }
        addSubscription(newDiv.textColor.observe(resolver, callback))
        addSubscription(newDiv.focusedTextColor?.observe(resolver, callback))
    }

    private fun TextView.applyTextColor(textColor: Int, focusedTextColor: Int?) {
        setTextColor(
            ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_focused), intArrayOf()),  // TODO: use static constant
                intArrayOf(focusedTextColor ?: textColor, textColor),
            )
        )
    }

    //endregion

    //region Underline

    private fun DivLineHeightTextView.bindUnderline(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.underline.equalsToConstant(oldDiv?.underline)) {
            return
        }

        applyUnderline(newDiv.underline.evaluate(resolver))

        if (newDiv.underline.isConstant()) {
            return
        }

        addSubscription(
            newDiv.underline.observe(resolver) { underline -> applyUnderline(underline) }
        )
    }

    private fun TextView.applyUnderline(underline: DivLineStyle) {
        when (underline) {
            DivLineStyle.SINGLE -> paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            DivLineStyle.NONE -> paintFlags = paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            else -> Unit
        }
    }

    //endregion

    //region Strikethrough

    private fun DivLineHeightTextView.bindStrikethrough(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.strike.equalsToConstant(oldDiv?.strike)) {
            return
        }

        applyStrikethrough(newDiv.strike.evaluate(resolver))

        if (newDiv.strike.isConstant()) {
            return
        }

        addSubscription(
            newDiv.strike.observe(resolver) { strikethrough -> applyStrikethrough(strikethrough) }
        )
    }

    private fun TextView.applyStrikethrough(strikethrough: DivLineStyle) {
        when (strikethrough) {
            DivLineStyle.SINGLE -> paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            DivLineStyle.NONE -> paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            else -> Unit
        }
    }

    //endregion

    //region Selectable

    private fun DivLineHeightTextView.bindSelectable(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.selectable.equalsToConstant(oldDiv?.selectable)) {
            return
        }

        applySelectable(newDiv.selectable.evaluate(resolver))

        if (newDiv.selectable.isConstant()) {
            return
        }

        addSubscription(
            newDiv.selectable.observe(resolver) { selectable -> applySelectable(selectable) }
        )
    }

    private fun TextView.applySelectable(selectable: Boolean) {
        setTextIsSelectable(selectable)
    }

    //endregion

    //region Text Gradient

    private fun DivLineHeightTextView.bindTextGradient(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        when (val textGradient = newDiv.textGradient) {
            null -> Unit
            is DivTextGradient.Linear -> bindLinearTextGradient(textGradient.value, oldDiv?.textGradient, resolver)
            is DivTextGradient.Radial -> bindRadialTextGradient(textGradient.value, oldDiv?.textGradient, resolver)
        }
    }

    private fun DivLineHeightTextView.bindLinearTextGradient(
        newTextGradient: DivLinearGradient,
        oldTextGradient: DivTextGradient?,
        resolver: ExpressionResolver,
    ) {
        if (oldTextGradient is DivTextGradient.Linear
            && newTextGradient.angle.equalsToConstant(oldTextGradient.value.angle)
            && newTextGradient.colors.equalsToConstant(oldTextGradient.value.colors)) {
            return
        }

        applyLinearTextGradientColor(
            newTextGradient.angle.evaluate(resolver),
            newTextGradient.colors.evaluate(resolver)
        )

        if (newTextGradient.angle.isConstant() && newTextGradient.colors.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyLinearTextGradientColor(
                newTextGradient.angle.evaluate(resolver),
                newTextGradient.colors.evaluate(resolver)
            )
        }
        addSubscription(newTextGradient.angle.observe(resolver, callback))
        addSubscription(newTextGradient.colors.observe(resolver, callback))
    }

    private fun TextView.applyLinearTextGradientColor(
        angle: Long,
        colors: List<Int>
    ) {
        doOnActualLayout {
            this.paint.shader = LinearGradientDrawable.createLinearGradient(
                    angle = angle.toFloat(),
                    colors = colors.toIntArray(),
                    width = width,
                    height = height
            )
        }
    }

    private fun DivLineHeightTextView.bindRadialTextGradient(
        newTextGradient: DivRadialGradient,
        oldTextGradient: DivTextGradient?,
        resolver: ExpressionResolver,
    ) {
        // TODO: compare radius and center in a proper way
        if (oldTextGradient is DivTextGradient.Radial
            && newTextGradient.radius == oldTextGradient.value.radius
            && newTextGradient.centerX == oldTextGradient.value.centerX
            && newTextGradient.centerY == oldTextGradient.value.centerY
            && newTextGradient.colors.equalsToConstant(oldTextGradient.value.colors)) {
            return
        }

        val displayMetrics = resources.displayMetrics
        applyRadialTextGradientColor(
            newTextGradient.radius.toRadialGradientDrawableRadius(displayMetrics, resolver),
            newTextGradient.centerX.toRadialGradientDrawableCenter(displayMetrics, resolver),
            newTextGradient.centerY.toRadialGradientDrawableCenter(displayMetrics, resolver),
            newTextGradient.colors.evaluate(resolver)
        )

        if (newTextGradient.colors.isConstant()) {
            return
        }

        addSubscription(newTextGradient.colors.observe(resolver) { colors ->
            applyRadialTextGradientColor(
                newTextGradient.radius.toRadialGradientDrawableRadius(displayMetrics, resolver),
                newTextGradient.centerX.toRadialGradientDrawableCenter(displayMetrics, resolver),
                newTextGradient.centerY.toRadialGradientDrawableCenter(displayMetrics, resolver),
                colors
            )
        })
    }

    private fun TextView.applyRadialTextGradientColor(
        radius: RadialGradientDrawable.Radius,
        centerX: RadialGradientDrawable.Center,
        centerY: RadialGradientDrawable.Center,
        colors: List<Int>
    ) {
        doOnActualLayout {
            this.paint.shader = RadialGradientDrawable.createRadialGradient(
                    radius = radius,
                    centerX = centerX,
                    centerY = centerY,
                    colors = colors.toIntArray(),
                    width = width,
                    height = height
            )
        }
    }

    private fun DivRadialGradientRadius.toRadialGradientDrawableRadius(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ): RadialGradientDrawable.Radius {
        return when (this) {
            is DivRadialGradientRadius.FixedSize -> {
                RadialGradientDrawable.Radius.Fixed(
                    value.value.evaluate(resolver).dpToPxF(metrics)
                )
            }

            is DivRadialGradientRadius.Relative -> {
                RadialGradientDrawable.Radius.Relative(
                    when (value.value.evaluate(resolver)) {
                        DivRadialGradientRelativeRadius.Value.FARTHEST_CORNER -> RadialGradientDrawable.Radius.Relative.Type.FARTHEST_CORNER
                        DivRadialGradientRelativeRadius.Value.NEAREST_CORNER -> RadialGradientDrawable.Radius.Relative.Type.NEAREST_CORNER
                        DivRadialGradientRelativeRadius.Value.FARTHEST_SIDE -> RadialGradientDrawable.Radius.Relative.Type.FARTHEST_SIDE
                        DivRadialGradientRelativeRadius.Value.NEAREST_SIDE -> RadialGradientDrawable.Radius.Relative.Type.NEAREST_SIDE
                    }
                )
            }
        }
    }

    private fun DivRadialGradientCenter.toRadialGradientDrawableCenter(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver
    ): RadialGradientDrawable.Center {
        return when (this) {
            is DivRadialGradientCenter.Fixed -> {
                RadialGradientDrawable.Center.Fixed(
                    value.value.evaluate(resolver).dpToPxF(metrics)
                )
            }

            is DivRadialGradientCenter.Relative -> {
                RadialGradientDrawable.Center.Relative(
                    value.value.evaluate(resolver).toFloat()
                )
            }
        }
    }

    //endregion

    //region Text

    private fun DivLineHeightTextView.bindText(
        divView: Div2View,
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.ranges == null && newDiv.images == null) {
            bindPlainText(newDiv, oldDiv, resolver)
        } else {
            bindRichText(divView, newDiv, resolver)
        }
    }

    private fun DivLineHeightTextView.bindRichText(
        divView: Div2View,
        newDiv: DivText,
        resolver: ExpressionResolver
    ) {
        applyRichText(divView, resolver, newDiv)
        applyHyphenation(newDiv.text.evaluate(resolver))

        addSubscription(
            newDiv.text.observe(resolver) { text ->
                applyRichText(divView, resolver, newDiv)
                applyHyphenation(text)
            }
        )

        val callback = { _: Any -> applyRichText(divView, resolver, newDiv) }
        newDiv.ranges?.forEach { range ->
            addSubscription(range.start.observe(resolver, callback))
            addSubscription(range.end.observe(resolver, callback))
            addSubscription(range.fontSize?.observe(resolver, callback))
            addSubscription(range.fontSizeUnit.observe(resolver, callback))
            addSubscription(range.fontWeight?.observe(resolver, callback))
            addSubscription(range.letterSpacing?.observe(resolver, callback))
            addSubscription(range.lineHeight?.observe(resolver, callback))
            addSubscription(range.strike?.observe(resolver, callback))
            addSubscription(range.textColor?.observe(resolver, callback))
            addSubscription(range.topOffset?.observe(resolver, callback))
            addSubscription(range.underline?.observe(resolver, callback))
        }
        newDiv.images?.forEach { image ->
            addSubscription(image.start.observe(resolver, callback))
            addSubscription(image.url.observe(resolver, callback))
            addSubscription(image.tintColor?.observe(resolver, callback))
            addSubscription(image.width.value.observe(resolver, callback))
            addSubscription(image.width.unit.observe(resolver, callback))
        }
    }

    private fun TextView.applyRichText(
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
            div.fontFamily?.evaluate(resolver),
            div.ranges,
            null,
            div.images
        )
        ranger.onTextChanged { text ->
            setText(text, TextView.BufferType.NORMAL)
        }
        ranger.run()
    }

    /**
     * Used in most common case, where there is no [DivText.ranges] or [DivText.images]
     * to bind [DivText.text] as quick as possible.
     */
    private fun DivLineHeightTextView.bindPlainText(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.text.equalsToConstant(oldDiv?.text)) {
            return
        }

        applyPlainText(newDiv.text.evaluate(resolver))
        applyHyphenation(newDiv.text.evaluate(resolver))

        if (newDiv.text.isConstant() && newDiv.text.isConstant()) {
            return
        }

        addSubscription(
            newDiv.text.observe(resolver) { text ->
                applyPlainText(text)
                applyHyphenation(text)
            }
        )
    }

    private fun TextView.applyPlainText(text: String) {
        this.text = text
    }

    private fun TextView.applyHyphenation(text: String) {
        if (!checkHyphenationSupported()) {
            return
        }
        val oldHyphenFreq = hyphenationFrequency
        val newHyphenFreq = when {
            !isHyphenationEnabled -> Layout.HYPHENATION_FREQUENCY_NONE
            TextUtils.indexOf(
                text,
                SOFT_HYPHEN,
                0,
                min(text.length, LONGEST_WORD_BREAK)
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

    //endregion

    //region Ellipsis

    private fun DivLineHeightTextView.bindEllipsis(
        divView: Div2View,
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        val ellipsis = newDiv.ellipsis
        if (ellipsis?.ranges == null && ellipsis?.images == null && ellipsis?.actions == null) {
            bindPlainEllipsis(newDiv.ellipsis, oldDiv?.ellipsis, resolver)
        } else {
            bindRichEllipsis(divView, newDiv, resolver)
        }
    }

    private fun DivLineHeightTextView.bindPlainEllipsis(
        newEllipsis: DivText.Ellipsis?,
        oldEllipsis: DivText.Ellipsis?,
        resolver: ExpressionResolver
    ) {
        if (newEllipsis?.text.equalsToConstant(oldEllipsis?.text)) {
            return
        }

        applyPlainEllipsis(newEllipsis?.text?.evaluate(resolver))

        if (newEllipsis?.text.isConstantOrNull() && newEllipsis?.text.isConstantOrNull()) {
            return
        }

        addSubscription(
            newEllipsis?.text?.observe(resolver) { ellipsis -> applyPlainEllipsis(ellipsis) }
        )
    }

    private fun DivLineHeightTextView.applyPlainEllipsis(ellipsis: String?) {
        this.ellipsis = ellipsis ?: EllipsizedTextView.DEFAULT_ELLIPSIS
    }

    private fun DivLineHeightTextView.bindRichEllipsis(
        divView: Div2View,
        newDiv: DivText,
        resolver: ExpressionResolver
    ) {
        applyRichEllipsis(divView, newDiv, resolver)

        val ellipsis = newDiv.ellipsis ?: return

        val callback = { _: Any -> applyRichEllipsis(divView, newDiv, resolver) }
        addSubscription(ellipsis.text.observe(resolver, callback))
        ellipsis.ranges?.forEach { range ->
            addSubscription(range.start.observe(resolver, callback))
            addSubscription(range.end.observe(resolver, callback))
            addSubscription(range.fontSize?.observe(resolver, callback))
            addSubscription(range.fontSizeUnit.observe(resolver, callback))
            addSubscription(range.fontWeight?.observe(resolver, callback))
            addSubscription(range.letterSpacing?.observe(resolver, callback))
            addSubscription(range.lineHeight?.observe(resolver, callback))
            addSubscription(range.strike?.observe(resolver, callback))
            addSubscription(range.textColor?.observe(resolver, callback))
            addSubscription(range.topOffset?.observe(resolver, callback))
            addSubscription(range.underline?.observe(resolver, callback))
            when (val background = range.background?.value()) {
                is DivSolidBackground -> addSubscription(background.color.observe(resolver, callback))
            }
            addSubscription(range.border?.stroke?.color?.observe(resolver, callback))
            addSubscription(range.border?.stroke?.width?.observe(resolver, callback))
        }
        ellipsis.images?.forEach { image ->
            addSubscription(image.start.observe(resolver, callback))
            addSubscription(image.url.observe(resolver, callback))
            addSubscription(image.tintColor?.observe(resolver, callback))
            addSubscription(image.width.value.observe(resolver, callback))
            addSubscription(image.width.unit.observe(resolver, callback))
        }
    }

    private fun EllipsizedTextView.applyRichEllipsis(
        divView: Div2View,
        newDiv: DivText,
        resolver: ExpressionResolver
    ) {
        val ellipsis = newDiv.ellipsis
        if (ellipsis == null) {
            this.ellipsis = EllipsizedTextView.DEFAULT_ELLIPSIS
            return
        }

        val ranger = DivTextRanger(
            divView,
            this,
            resolver,
            ellipsis.text.evaluate(resolver),
            newDiv.fontSize.evaluate(resolver),
            newDiv.fontFamily?.evaluate(resolver),
            ellipsis.ranges,
            ellipsis.actions,
            ellipsis.images
        )
        ranger.onTextChanged { text ->
            this.ellipsis = text
        }
        ranger.run()
    }

    //endregion

    //region Text Shadow

    private fun DivLineHeightTextView.bindTextShadow(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.textShadow?.alpha.equalsToConstant(oldDiv?.textShadow?.alpha)
            && newDiv.textShadow?.blur.equalsToConstant(oldDiv?.textShadow?.blur)
            && newDiv.textShadow?.color.equalsToConstant(oldDiv?.textShadow?.color)
            && newDiv.textShadow?.offset?.x?.value.equalsToConstant(oldDiv?.textShadow?.offset?.x?.value)
            && newDiv.textShadow?.offset?.x?.unit.equalsToConstant(oldDiv?.textShadow?.offset?.x?.unit)
            && newDiv.textShadow?.offset?.y?.value.equalsToConstant(oldDiv?.textShadow?.offset?.y?.value)
            && newDiv.textShadow?.offset?.y?.unit.equalsToConstant(oldDiv?.textShadow?.offset?.y?.unit)) {
            return
        }

        val shadow = newDiv.textShadow
        val displayMetrics = resources.displayMetrics
        applyTextShadow(shadow?.getShadowParams(resolver, displayMetrics, newDiv.textColor.evaluate(resolver)))

        if (newDiv.textShadow?.alpha.isConstantOrNull()
            && newDiv.textShadow?.blur.isConstantOrNull()
            && newDiv.textShadow?.color.isConstantOrNull()
            && newDiv.textShadow?.offset?.x?.value.isConstantOrNull()
            && newDiv.textShadow?.offset?.x?.unit.isConstantOrNull()
            && newDiv.textShadow?.offset?.y?.value.isConstantOrNull()
            && newDiv.textShadow?.offset?.y?.unit.isConstantOrNull()) {
            return
        }

        val callback = { _: Any ->
            applyTextShadow(shadow?.getShadowParams(resolver, displayMetrics, newDiv.textColor.evaluate(resolver)))
        }

        addSubscription(shadow?.alpha?.observe(resolver, callback))
        addSubscription(shadow?.color?.observe(resolver, callback))
        addSubscription(shadow?.blur?.observe(resolver, callback))
        addSubscription(shadow?.offset?.x?.value?.observe(resolver, callback))
        addSubscription(shadow?.offset?.x?.unit?.observe(resolver, callback))
        addSubscription(shadow?.offset?.y?.value?.observe(resolver, callback))
        addSubscription(shadow?.offset?.y?.unit?.observe(resolver, callback))
    }

    private fun TextView.applyTextShadow(shadowParams: ShadowParams?) {
        if (shadowParams == null) {
            (parent as? DivViewWrapper)?.let {
                it.clipChildren = true
                it.clipToPadding = true
            }
            clipToOutline = true
            setShadowLayer(0.0f, 0.0f, 0.0f, Color.TRANSPARENT)
        } else {
            (parent as? DivViewWrapper)?.let {
                it.clipChildren = false
                it.clipToPadding = false
            }
            clipToOutline = false
            with(shadowParams) {
                setShadowLayer(radius, offsetX, offsetY, color)
            }
        }
    }

    //endregion

    //region Auto Ellipsize

    private fun DivLineHeightTextView.bindAutoEllipsize(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.autoEllipsize.equalsToConstant(oldDiv?.autoEllipsize)) {
            return
        }

        applyAutoEllipsize(newDiv.autoEllipsize?.evaluate(resolver) ?: false)
    }

    private fun DivLineHeightTextView.applyAutoEllipsize(ellipsize: Boolean) {
        autoEllipsize = ellipsize
    }

    //endregion

    // TODO: refactor to SpannedTextBuilder scoped by div context.
    private inner class DivTextRanger(
        private val divView: Div2View,
        private val textView: TextView,
        private val resolver: ExpressionResolver,
        private val text: String,
        private val fontSize: Long,
        private val fontFamily: String?,
        private val ranges: List<DivText.Range>?,
        private val actions: List<DivAction>?,
        images: List<DivText.Image>?
    ) {

        private val context = divView.context
        private val metrics = divView.resources.displayMetrics
        private val sb: SpannableStringBuilder = SpannableStringBuilder(text)
        private val images = images?.filter { it.start.evaluate(resolver) <= text.length }?.sortedBy { it.start.evaluate(resolver) } ?: emptyList()

        private var additionalCharsBeforeImage: IntArray? = null

        private var textObserver: ((CharSequence) -> Unit)? = null

        fun onTextChanged(action: (CharSequence) -> Unit) {
            textObserver = action
        }

        fun run() {
            if (ranges.isNullOrEmpty() && images.isEmpty()) {
                textObserver?.invoke(text)
                return
            }

            if (textView is DivLineHeightTextView) textView.textRoundedBgHelper?.invalidateSpansCache()
            ranges?.forEach { item -> sb.addTextRange(item) }
            images.reversed().forEach {
                sb.insert(it.start.evaluate(resolver).toIntSafely(), "#")
            }

            images.foldIndexed(Int.MIN_VALUE) { index, prevImageStart, image ->
                additionalCharsBeforeImage?.takeIf { index > 0 }?.let { chars ->
                    chars[index] = chars[index - 1]
                }
                val rawStart = image.start.evaluate(resolver).toIntSafely()
                val actualStart = rawStart + index + additionalCharsBeforeImage.getOrZero(index)
                val notWhitespaceBefore = actualStart > 0 && !sb[actualStart - 1].isWhitespace()
                val textBeforeImage = actualStart != prevImageStart + 1 && notWhitespaceBefore
                if (textBeforeImage) {
                    sb.insert(actualStart, WORD_JOINER)
                    val charsBeforeImage = additionalCharsBeforeImage
                            ?: IntArray(this.images.size).also { additionalCharsBeforeImage = it }
                    charsBeforeImage[index]++
                }
                rawStart + index + additionalCharsBeforeImage.getOrZero(index)
            }

            images.forEachIndexed { index, image ->
                val width = image.width.toPx(metrics, resolver)
                val height = image.height.toPx(metrics, resolver)

                val offsetY = if (sb.isNotEmpty()) {
                    val start = image.start.evaluate(resolver).toIntSafely()
                    val charIndex = if (start == 0) 0 else start - 1
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

                val start = image.start.evaluate(resolver).toIntSafely() + index +
                        additionalCharsBeforeImage.getOrZero(index)
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

        private fun IntArray?.getOrZero(index: Int) = this?.get(index) ?: 0

        private fun SpannableStringBuilder.addTextRange(range: DivText.Range) {
            val start = range.start.evaluate(resolver).toIntSafely().coerceAtMost(text.length)
            val end = range.end.evaluate(resolver).toIntSafely().coerceAtMost(text.length)
            if (start > end) return

            range.fontSize?.evaluate(resolver)?.let {
                setSpan(AbsoluteSizeSpan(it.unitToPx(metrics, range.fontSizeUnit.evaluate(resolver))), start, end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            range.textColor?.evaluate(resolver)?.let {
                setSpan(TextColorSpan(it), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            range.letterSpacing?.evaluate(resolver)?.let {
                val fontSize = range.fontSize?.evaluate(resolver) ?: fontSize
                val letterSpacingEm = it.toFloat() / fontSize
                setSpan(LetterSpacingSpan(letterSpacingEm), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            range.strike?.let {
                when (it.evaluate(resolver)) {
                    DivLineStyle.SINGLE -> setSpan(StrikethroughSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    DivLineStyle.NONE -> setSpan(NoStrikethroughSpan(), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    else -> Unit
                }
            }
            range.underline?.let {
                when (it.evaluate(resolver)) {
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
                ViewCompat.enableAccessibleClickableSpanSupport(textView)
            }
            if (range.border != null || range.background != null) {
                val span = DivBackgroundSpan(range.border, range.background)
                if (textView is DivLineHeightTextView && !textView.hasSuchSpan(this, span, start, end)) {
                    setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    textView.textRoundedBgHelper?.addBackgroundSpan(span)
                }
            }
            if (range.lineHeight != null || range.topOffset != null) {
                val offset = range.topOffset?.evaluate(resolver).unitToPx(metrics, range.fontSizeUnit.evaluate(resolver))
                val lineHeight = range.lineHeight?.evaluate(resolver).unitToPx(metrics, range.fontSizeUnit.evaluate(resolver))
                setSpan(LineHeightWithTopOffsetSpan(offset, lineHeight), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            range.textShadow?.let {
                val shadowParams = it.getShadowParams(
                    resolver,
                    textView.resources.displayMetrics,
                    range.textColor?.evaluate(resolver) ?: textView.currentTextColor
                )
                setSpan(ShadowSpan(shadowParams), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }

        private fun DivLineHeightTextView.hasSuchSpan(sb: SpannableStringBuilder, backgroundSpan: DivBackgroundSpan, start: Int, end: Int): Boolean {
            if (textRoundedBgHelper == null) {
                textRoundedBgHelper = DivTextRangesBackgroundHelper(this, resolver)
                return false
            }
            return textRoundedBgHelper!!.hasSameSpan(sb, backgroundSpan, start, end)
        }

        private fun SpannableStringBuilder.makeImageSpan(
            range: DivText.Image,
            bitmap: Bitmap
        ): BitmapImageSpan {
            val imageHeight = range.height.toPx(metrics, resolver)
            val offsetY = if (isNotEmpty()) {
                val start = range.start.evaluate(resolver).toIntSafely()
                val charIndex = if (start == 0) 0 else start - 1
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
                val start = image.start.evaluate(resolver).toIntSafely() + index +
                        additionalCharsBeforeImage.getOrZero(index)
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

    private fun DivShadow.getShadowParams(
        resolver: ExpressionResolver,
        displayMetrics: DisplayMetrics,
        fontColor: Int
    ): ShadowParams {
        val fontAlpha = fontColor ushr 24

        val radius = blur.evaluate(resolver).dpToPxF(displayMetrics)
        val offsetX = offset.x.toPx(displayMetrics, resolver).toFloat()
        val offsetY = offset.y.toPx(displayMetrics, resolver).toFloat()
        val color = Paint().apply {
            color = this@getShadowParams.color.evaluate(resolver)
            alpha = (this@getShadowParams.alpha.evaluate(resolver) * fontAlpha).toInt()
        }.color

        return ShadowParams(offsetX, offsetY, radius, color)
    }
}
