package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivInput
import com.yandex.div2.DivSizeUnit
import javax.inject.Inject

/**
 * Class for binding div input to templated view
 */
@DivScope
internal class DivInputBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val typefaceResolver: DivTypefaceResolver,
    private val variableBinder: TwoWayStringVariableBinder
) : DivViewBinder<DivInput, DivInputView> {

    override fun bindView(view: DivInputView, div: DivInput, divView: Div2View) {
        val oldDiv = view.div
        if (div == oldDiv) return

        val expressionResolver = divView.expressionResolver
        view.closeAllSubscription()

        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)

        val nativeBackground = view.background

        baseBinder.bindView(view, div, oldDiv, divView)

        view.apply {
            isFocusable = true
            isFocusableInTouchMode = true

            observeBackground(div, divView, expressionResolver, nativeBackground)

            observeFontSize(div, expressionResolver)
            observeTypeface(div, expressionResolver)
            observeTextColor(div, expressionResolver)
            observeLineHeight(div, expressionResolver)
            observeMaxVisibleLines(div, expressionResolver)

            observeHintText(div, expressionResolver)
            observeHintColor(div, expressionResolver)
            observeHighlightColor(div, expressionResolver)

            observeKeyboardType(div, expressionResolver)
            observeSelectAllOnFocus(div, expressionResolver)

            observeText(div, divView)
        }
    }

    private fun DivInputView.observeBackground(
        div: DivInput,
        divView: Div2View,
        resolver: ExpressionResolver,
        nativeBackground: Drawable?
    ) {
        nativeBackground ?: return
        val nativeBackgroundColorVariable = div.nativeInterface?.color ?: return

        val callback = { color: Int ->
            applyNativeBackgroundColor(color, div, divView, resolver, nativeBackground)
        }
        addSubscription(nativeBackgroundColorVariable.observeAndGet(resolver, callback))
    }

    private fun View.applyNativeBackgroundColor(
        color: Int,
        div: DivInput,
        divView: Div2View,
        resolver: ExpressionResolver,
        nativeBackground: Drawable
    ) {
        nativeBackground.setTint(color)
        baseBinder.bindBackground(this, div, divView, resolver, nativeBackground)
    }

    private fun DivInputView.observeFontSize(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any -> applyFontSize(div, resolver) }
        addSubscription(div.fontSize.observeAndGet(resolver, callback))
        addSubscription(div.letterSpacing.observe(resolver, callback))
    }

    private fun DivInputView.applyFontSize(div: DivInput, resolver: ExpressionResolver) {
        val fontSize = div.fontSize.evaluate(resolver)
        applyFontSize(fontSize, div.fontSizeUnit.evaluate(resolver))
        applyLetterSpacing(div.letterSpacing.evaluate(resolver), fontSize)
    }

    private fun DivInputView.observeTypeface(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any -> typeface = typefaceResolver.getTypeface(
            div.fontFamily.evaluate(resolver),
            div.fontWeight.evaluate(resolver)
        ) }
        addSubscription(div.fontFamily.observeAndGet(resolver, callback))
        addSubscription(div.fontWeight.observe(resolver, callback))
    }

    private fun DivInputView.observeTextColor(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any -> setTextColor(div.textColor.evaluate(resolver)) }
        addSubscription(div.textColor.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeLineHeight(div: DivInput, resolver: ExpressionResolver) {
        val fontSizeUnit = div.fontSizeUnit.evaluate(resolver)
        val lineHeightExpr = div.lineHeight ?: run {
            applyLineHeight(null, fontSizeUnit)
            return
        }

        val callback = { _: Any ->
            applyLineHeight(lineHeightExpr.evaluate(resolver), fontSizeUnit)
        }
        addSubscription(lineHeightExpr.observeAndGet(resolver, callback))
    }

    private fun DivInputView.applyLineHeight(lineHeight: Int?, unit: DivSizeUnit) {
        val height = lineHeight?.unitToPx(resources.displayMetrics, unit)
        setFixedLineHeight(height)
        (this as TextView).applyLineHeight(lineHeight, unit)
    }

    private fun DivInputView.observeMaxVisibleLines(div: DivInput, resolver: ExpressionResolver) {
        val maxLinesExpr = div.maxLines ?: return

        val callback = { _: Any -> maxLines = maxLinesExpr.evaluate(resolver) }
        addSubscription(maxLinesExpr.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeHintText(div: DivInput, resolver: ExpressionResolver) {
        val hintTextExpr = div.hintText ?: return

        val callback = { _: Any -> hint = hintTextExpr.evaluate(resolver) }
        addSubscription(hintTextExpr.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeHintColor(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any -> setHintTextColor(div.hintColor.evaluate(resolver)) }
        addSubscription(div.hintColor.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeHighlightColor(div: DivInput, resolver: ExpressionResolver) {
        val highlightColorExpr = div.highlightColor ?: return

        val callback = { _: Any -> highlightColor = highlightColorExpr.evaluate(resolver) }
        addSubscription(highlightColorExpr.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeKeyboardType(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any -> applyKeyboardType(div, resolver) }
        addSubscription(div.keyboardType.observeAndGet(resolver, callback))
    }

    private fun EditText.applyKeyboardType(div: DivInput, resolver: ExpressionResolver) {
        inputType = when(div.keyboardType.evaluate(resolver)) {
            DivInput.KeyboardType.DATE -> InputType.TYPE_CLASS_DATETIME
            DivInput.KeyboardType.EMAIL -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            DivInput.KeyboardType.NUMBER -> InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            DivInput.KeyboardType.PHONE -> InputType.TYPE_CLASS_PHONE
            DivInput.KeyboardType.URI -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_URI
            else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
    }

    private fun DivInputView.observeSelectAllOnFocus(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any -> setSelectAllOnFocus(div.selectAllOnFocus.evaluate(resolver)) }
        addSubscription(div.selectAllOnFocus.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeText(div: DivInput, divView: Div2View) {
        removeBoundVariableChangeAction()

        val callbacks = object : TwoWayStringVariableBinder.Callbacks {
            override fun onVariableChanged(value: String?) {
                text = Editable.Factory.getInstance().newEditable(value)
            }

            override fun setViewStateChangeListener(valueUpdater: (String) -> Unit) {
                setBoundVariableChangeAction { editable ->
                    valueUpdater(editable?.toString() ?: "")
                }
            }
        }

        addSubscription(variableBinder.bindVariable(divView, div.textVariable, callbacks))
    }
}
