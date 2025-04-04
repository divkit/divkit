package com.yandex.div.core.view2.divs

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.TextUtils
import android.text.TextUtils.TruncateAt
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.HYPHENATION_SUPPORT_ENABLED
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.observeColorPoint
import com.yandex.div.core.util.toColormap
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.getTypeface
import com.yandex.div.core.view2.spannable.ShadowData
import com.yandex.div.core.view2.spannable.SpannedTextBuilder
import com.yandex.div.core.widget.AdaptiveMaxLines
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.drawable.LinearGradientDrawable
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.graphics.Colormap
import com.yandex.div.internal.graphics.checkIsNotEmpty
import com.yandex.div.internal.util.compareNullableWith
import com.yandex.div.internal.widget.EllipsizedTextView
import com.yandex.div.internal.widget.checkHyphenationSupported
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.Div
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
private const val LONGEST_WORD_BREAK = 10

/**
 * Class for binding div text to templated view
 */
@DivScope
internal class DivTextBinder @Inject constructor(
    baseBinder: DivBaseBinder,
    private val typefaceResolver: DivTypefaceResolver,
    private val spannedTextBuilder: SpannedTextBuilder,
    @ExperimentFlag(HYPHENATION_SUPPORT_ENABLED) private val isHyphenationEnabled: Boolean
) : DivViewBinder<Div.Text, DivText, DivLineHeightTextView>(baseBinder) {
    
    override fun DivLineHeightTextView.bind(bindingContext: BindingContext, div: DivText, oldDiv: DivText?) {
        applyDivActions(
            bindingContext,
            div.action,
            div.actions,
            div.longtapActions,
            div.doubletapActions,
            div.hoverStartActions,
            div.hoverEndActions,
            div.pressStartActions,
            div.pressEndActions,
            div.actionAnimation,
            div.accessibility,
        )

        val expressionResolver = bindingContext.expressionResolver
        bindTypeface(div, oldDiv, expressionResolver)
        bindTextAlignment(div, oldDiv, expressionResolver)
        bindFontSize(div, oldDiv, expressionResolver)
        bindFontFeatureSettings(div, oldDiv, expressionResolver)
        bindTextColor(div, oldDiv, expressionResolver)
        bindUnderline(div, oldDiv, expressionResolver)
        bindStrikethrough(div, oldDiv, expressionResolver)
        bindMaxLines(div, oldDiv, expressionResolver)
        bindText(bindingContext, div, oldDiv)
        bindEllipsis(bindingContext, div, oldDiv)
        bindEllipsize(div, oldDiv, expressionResolver)
        bindTextGradient(bindingContext.divView, div, oldDiv, expressionResolver)
        bindTextShadow(div, oldDiv, expressionResolver)
        bindSelectable(div, oldDiv, expressionResolver)
        bindTightenWidth(div, oldDiv, expressionResolver)
        updateFocusableState(div)
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

    //region Font Feature Settings

    private fun DivLineHeightTextView.bindFontFeatureSettings(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.fontFeatureSettings.equalsToConstant(oldDiv?.fontFeatureSettings)) {
            return
        }

        applyFontFeatureSettings(newDiv.fontFeatureSettings?.evaluate(resolver))

        if (newDiv.fontFeatureSettings.isConstantOrNull()) {
            return
        }

        val callback = { _: Any ->
            applyFontFeatureSettings(newDiv.fontFeatureSettings?.evaluate(resolver))
        }
        addSubscription(newDiv.fontFeatureSettings?.observe(resolver, callback))
    }

    private fun TextView.applyFontFeatureSettings(settings: String?) {
        fontFeatureSettings = settings.takeIf { it?.isNotBlank() == true }
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

        applyTypeface(
            newDiv.fontFamily?.evaluate(resolver),
            newDiv.fontWeight.evaluate(resolver),
            newDiv.fontWeightValue?.evaluate(resolver),
        )

        if (newDiv.fontFamily.isConstantOrNull() && newDiv.fontWeight.isConstant() && newDiv.fontWeightValue.isConstantOrNull()) {
            return
        }

        val callback = { _: Any ->
             applyTypeface(
                newDiv.fontFamily?.evaluate(resolver),
                newDiv.fontWeight.evaluate(resolver),
                newDiv.fontWeightValue?.evaluate(resolver)
            )
        }
        addSubscription(newDiv.fontFamily?.observe(resolver, callback))
        addSubscription(newDiv.fontWeight.observe(resolver, callback))
        addSubscription(newDiv.fontWeightValue?.observe(resolver, callback))
    }

    private fun TextView.applyTypeface(
        fontFamily: String?,
        fontWeight: DivFontWeight,
        fontWeightValue: Long?
    ) {
        typeface = typefaceResolver.getTypeface(fontFamily, fontWeight, fontWeightValue)
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

    //region Tighten Width

    private fun DivLineHeightTextView.bindTightenWidth(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.tightenWidth.equalsToConstant(oldDiv?.tightenWidth)) {
            return
        }

        applyTightenWidth(newDiv.tightenWidth.evaluate(resolver))

        if (newDiv.tightenWidth.isConstant()) {
            return
        }

        addSubscription(
            newDiv.tightenWidth.observe(resolver) { applyTightenWidth(it) }
        )
    }

    private fun DivLineHeightTextView.applyTightenWidth(tight: Boolean) {
        isTightenWidth = tight
    }

    //endregion

    //region Text Gradient

    private fun DivLineHeightTextView.bindTextGradient(
        divView: Div2View,
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) {
        when (val textGradient = newDiv.textGradient) {
            null -> paint.shader = null
            is DivTextGradient.Linear -> bindLinearTextGradient(divView, textGradient.value, oldDiv?.textGradient, resolver)
            is DivTextGradient.Radial -> bindRadialTextGradient(textGradient.value, oldDiv?.textGradient, resolver)
        }
    }

    private fun DivLineHeightTextView.bindLinearTextGradient(
        divView: Div2View,
        newTextGradient: DivLinearGradient,
        oldTextGradient: DivTextGradient?,
        resolver: ExpressionResolver,
    ) {
        if (oldTextGradient is DivTextGradient.Linear
            && newTextGradient.angle.equalsToConstant(oldTextGradient.value.angle)
            && newTextGradient.colors.equalsToConstant(oldTextGradient.value.colors)
            && newTextGradient.colorMap.compareNullableWith(oldTextGradient.value.colorMap) { left, right -> left.equalsToConstant(right)}) {
            return
        }

        applyLinearTextGradientColor(
            newTextGradient.angle.evaluate(resolver),
            newTextGradient.toColormap(resolver).checkIsNotEmpty(divView)
        )

        if (newTextGradient.angle.isConstant()
            && newTextGradient.colors.isConstantOrNull()
            && newTextGradient.colorMap?.all { it.isConstant() } != false) {
            return
        }

        val callback = { _: Any ->
            applyLinearTextGradientColor(
                newTextGradient.angle.evaluate(resolver),
                newTextGradient.toColormap(resolver).checkIsNotEmpty(divView)
            )
        }
        addSubscription(newTextGradient.angle.observe(resolver, callback))
        addSubscription(newTextGradient.colors?.observe(resolver, callback))
        newTextGradient.colorMap?.forEach { observeColorPoint(it, resolver, callback) }
    }

    private fun TextView.applyLinearTextGradientColor(
        angle: Long,
        colormap: Colormap
    ) {
        doOnActualLayout {
            this.paint.shader = LinearGradientDrawable.createLinearGradient(
                angle = angle.toFloat(),
                colors = colormap.colors,
                positions = colormap.positions,
                width = realTextWidth,
                height = height - paddingBottom - paddingTop
            )
            invalidate()
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
            newTextGradient.colors?.evaluate(resolver) ?: emptyList()
        )

        if (newTextGradient.colors.isConstantOrNull()) {
            return
        }

        addSubscription(newTextGradient.colors?.observe(resolver) { colors ->
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
                width = realTextWidth,
                height = height - paddingBottom - paddingTop
            )
            invalidate()
        }
    }

    private val TextView.realTextWidth: Int
        get() = minOf(
            width - paddingRight - paddingLeft,
            paint.measureText(text.toString()).toInt()
        )

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
        bindingContext: BindingContext,
        newDiv: DivText,
        oldDiv: DivText?,
    ) {
        if (newDiv.ranges == null && newDiv.images == null) {
            bindPlainText(bindingContext, newDiv, oldDiv)
        } else {
            bindRichText(bindingContext, newDiv)
        }
    }

    private fun DivLineHeightTextView.bindRichText(
        bindingContext: BindingContext,
        newDiv: DivText,
    ) {
        val resolver = bindingContext.expressionResolver
        applyRichText(bindingContext, newDiv)
        applyHyphenation(newDiv.text.evaluate(resolver))

        addSubscription(
            newDiv.text.observe(resolver) { text ->
                applyRichText(bindingContext, newDiv)
                applyHyphenation(text)
            }
        )

        val callback = { _: Any -> applyRichText(bindingContext, newDiv) }

        addSubscription(newDiv.fontSize.observe(resolver, callback))
        addSubscription(newDiv.fontSizeUnit.observe(resolver, callback))
        addSubscription(newDiv.fontFamily?.observe(resolver, callback))
        addSubscription(newDiv.lineHeight?.observe(resolver, callback))

        newDiv.ranges?.forEach { range ->
            addSubscription(range.start.observe(resolver, callback))
            addSubscription(range.end?.observe(resolver, callback))
            addSubscription(range.alignmentVertical?.observe(resolver, callback))
            addSubscription(range.fontSize?.observe(resolver, callback))
            addSubscription(range.fontSizeUnit.observe(resolver, callback))
            addSubscription(range.fontWeight?.observe(resolver, callback))
            addSubscription(range.fontWeightValue?.observe(resolver, callback))
            addSubscription(range.letterSpacing?.observe(resolver, callback))
            addSubscription(range.lineHeight?.observe(resolver, callback))
            addSubscription(range.strike?.observe(resolver, callback))
            addSubscription(range.textColor?.observe(resolver, callback))
            addSubscription(range.topOffset?.observe(resolver, callback))
            addSubscription(range.underline?.observe(resolver, callback))
        }
        newDiv.images?.forEach { image ->
            addSubscription(image.start.observe(resolver, callback))
            addSubscription(image.indexingDirection.observe(resolver, callback))
            addSubscription(image.url.observe(resolver, callback))
            addSubscription(image.alignmentVertical.observe(resolver, callback))
            addSubscription(image.tintColor?.observe(resolver, callback))
            addSubscription(image.width.value.observe(resolver, callback))
            addSubscription(image.width.unit.observe(resolver, callback))
        }
    }

    private fun TextView.applyRichText(
        bindingContext: BindingContext,
        div: DivText
    ) {
        spannedTextBuilder.buildText(bindingContext, this, div) { spannedText ->
            setText(spannedText, TextView.BufferType.NORMAL)
        }
    }

    /**
     * Used in most common case, where there is no [DivText.ranges] or [DivText.images]
     * to bind [DivText.text] as quick as possible.
     */
    private fun DivLineHeightTextView.bindPlainText(
        bindingContext: BindingContext,
        newDiv: DivText,
        oldDiv: DivText?
    ) {
        if (newDiv.text.equalsToConstant(oldDiv?.text)
            && newDiv.lineHeight.equalsToConstant(oldDiv?.lineHeight)
            && newDiv.fontSizeUnit.equalsToConstant(oldDiv?.fontSizeUnit)
            && newDiv.extensions.compareNullableWith(oldDiv?.extensions) { new, old -> new.id == old.id }) {
            return
        }

        val resolver = bindingContext.expressionResolver

        val text = newDiv.text.evaluate(resolver)
        applyPlainText(bindingContext, newDiv)
        applyHyphenation(text)

        if (newDiv.text.isConstant()
            && newDiv.lineHeight.isConstantOrNull()
            && newDiv.fontSizeUnit.isConstantOrNull()) {
            return
        }

        val callback = { _: Any ->
            val newText = newDiv.text.evaluate(resolver)
            applyPlainText(bindingContext, newDiv)
            applyHyphenation(newText)
        }

        addSubscription(newDiv.text.observe(resolver, callback))
        addSubscription(newDiv.lineHeight?.observe(resolver, callback))
        addSubscription(newDiv.fontSizeUnit.observe(resolver, callback))
    }

    private fun TextView.applyPlainText(bindingContext: BindingContext, divText: DivText) {
        this.text = spannedTextBuilder.buildPlainText(bindingContext, this, divText)
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
        bindingContext: BindingContext,
        newDiv: DivText,
        oldDiv: DivText?,
    ) {
        val ellipsis = newDiv.ellipsis
        if (ellipsis?.ranges == null && ellipsis?.images == null && ellipsis?.actions == null) {
            bindPlainEllipsis(newDiv.ellipsis, oldDiv?.ellipsis, bindingContext.expressionResolver)
        } else {
            bindRichEllipsis(bindingContext, newDiv)
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
        bindingContext: BindingContext,
        newDiv: DivText,
    ) {
        applyRichEllipsis(bindingContext, newDiv)

        val ellipsis = newDiv.ellipsis ?: return

        val resolver = bindingContext.expressionResolver
        val callback = { _: Any -> applyRichEllipsis(bindingContext, newDiv) }
        addSubscription(ellipsis.text.observe(resolver, callback))
        ellipsis.ranges?.forEach { range ->
            addSubscription(range.start.observe(resolver, callback))
            addSubscription(range.end?.observe(resolver, callback))
            addSubscription(range.fontSize?.observe(resolver, callback))
            addSubscription(range.fontSizeUnit.observe(resolver, callback))
            addSubscription(range.fontWeight?.observe(resolver, callback))
            addSubscription(range.fontWeightValue?.observe(resolver, callback))
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
        bindingContext: BindingContext,
        newDiv: DivText,
    ) {
        val ellipsis = newDiv.ellipsis
        if (ellipsis == null) {
            this.ellipsis = EllipsizedTextView.DEFAULT_ELLIPSIS
            return
        }

        spannedTextBuilder.buildEllipsis(
            bindingContext = bindingContext,
            textView = this,
            divText = newDiv,
            ellipsis
        ) { ellipsis ->
            this.ellipsis = ellipsis
        }
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
        applyTextShadow(shadow?.getShadowData(resolver, displayMetrics, newDiv.textColor.evaluate(resolver)))

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
            applyTextShadow(shadow?.getShadowData(resolver, displayMetrics, newDiv.textColor.evaluate(resolver)))
        }

        addSubscription(shadow?.alpha?.observe(resolver, callback))
        addSubscription(shadow?.color?.observe(resolver, callback))
        addSubscription(shadow?.blur?.observe(resolver, callback))
        addSubscription(shadow?.offset?.x?.value?.observe(resolver, callback))
        addSubscription(shadow?.offset?.x?.unit?.observe(resolver, callback))
        addSubscription(shadow?.offset?.y?.value?.observe(resolver, callback))
        addSubscription(shadow?.offset?.y?.unit?.observe(resolver, callback))
    }

    private fun TextView.applyTextShadow(shadowParams: ShadowData?) {
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

    //region Ellipsize

    private fun DivLineHeightTextView.bindEllipsize(
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.autoEllipsize.equalsToConstant(oldDiv?.autoEllipsize) &&
            newDiv.truncate.equalsToConstant(oldDiv?.truncate)) {
            return
        }

        applyEllipsize(newDiv, resolver)

        if (newDiv.autoEllipsize.isConstantOrNull() && newDiv.truncate.isConstant()) return

        val callback = { _: Any -> applyEllipsize(newDiv, resolver) }
        newDiv.autoEllipsize?.let { addSubscription(it.observe(resolver, callback))}
        addSubscription(newDiv.truncate.observe(resolver, callback))
    }

    private fun DivLineHeightTextView.applyEllipsize(div: DivText, resolver: ExpressionResolver) {
        val location = div.truncate.evaluate(resolver)
        ellipsisLocation = when (location) {
            DivText.Truncate.NONE -> null
            DivText.Truncate.START -> TruncateAt.START
            DivText.Truncate.MIDDLE -> TruncateAt.MIDDLE
            DivText.Truncate.END -> TruncateAt.END
        }
        autoEllipsize = location != DivText.Truncate.NONE && div.autoEllipsize?.evaluate(resolver) ?: false
    }

    //endregion

    private fun View.updateFocusableState(div: DivText) {
        isFocusable = isFocusable || (div.focusedTextColor != null)
    }

    private fun DivShadow.getShadowData(
        resolver: ExpressionResolver,
        displayMetrics: DisplayMetrics,
        fontColor: Int
    ): ShadowData {
        val fontAlpha = fontColor ushr 24

        val radius = blur.evaluate(resolver).dpToPxF(displayMetrics)
        val offsetX = offset.x.toPx(displayMetrics, resolver).toFloat()
        val offsetY = offset.y.toPx(displayMetrics, resolver).toFloat()
        val color = Paint().apply {
            color = this@getShadowData.color.evaluate(resolver)
            alpha = (this@getShadowData.alpha.evaluate(resolver) * fontAlpha).toInt()
        }.color

        return ShadowData(offsetX, offsetY, radius, color)
    }
}
