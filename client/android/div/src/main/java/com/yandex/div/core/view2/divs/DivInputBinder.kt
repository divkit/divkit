package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.util.mask.BaseInputMask
import com.yandex.div.core.util.mask.CurrencyInputMask
import com.yandex.div.core.util.mask.FixedLengthInputMask
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivCurrencyInputMask
import com.yandex.div2.DivFixedLengthInputMask
import com.yandex.div2.DivInput
import com.yandex.div2.DivSizeUnit
import java.util.Locale
import java.util.regex.PatternSyntaxException
import javax.inject.Inject

/**
 * Class for binding div input to templated view
 */
@DivScope
internal class DivInputBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val typefaceResolver: DivTypefaceResolver,
    private val variableBinder: TwoWayStringVariableBinder,
    private val errorCollectors: ErrorCollectors
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
            textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START

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

            observeText(div, expressionResolver, divView)
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
        addSubscription(div.fontSizeUnit.observe(resolver, callback))
    }

    private fun DivInputView.applyFontSize(div: DivInput, resolver: ExpressionResolver) {
        val fontSize = div.fontSize.evaluate(resolver).toIntSafely()
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

    private fun DivInputView.applyLineHeight(lineHeight: Long?, unit: DivSizeUnit) {
        val height = lineHeight?.unitToPx(resources.displayMetrics, unit)
        setFixedLineHeight(height)
        (this as TextView).applyLineHeight(lineHeight, unit)
    }

    private fun DivInputView.observeMaxVisibleLines(div: DivInput, resolver: ExpressionResolver) {
        val maxLinesExpr = div.maxVisibleLines ?: return

        val callback = { _: Any -> maxLines = maxLinesExpr.evaluate(resolver).toIntSafely() }
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
        val callback = { type: DivInput.KeyboardType ->
            applyKeyboardType(type)
            setHorizontallyScrolling(type != DivInput.KeyboardType.MULTI_LINE_TEXT)
        }
        addSubscription(div.keyboardType.observeAndGet(resolver, callback))
    }

    private fun EditText.applyKeyboardType(type: DivInput.KeyboardType) {
        inputType = when(type) {
            DivInput.KeyboardType.SINGLE_LINE_TEXT -> InputType.TYPE_CLASS_TEXT
            DivInput.KeyboardType.MULTI_LINE_TEXT -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
            DivInput.KeyboardType.EMAIL -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            DivInput.KeyboardType.URI -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_URI
            DivInput.KeyboardType.NUMBER -> InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            DivInput.KeyboardType.PHONE -> InputType.TYPE_CLASS_PHONE
        }
    }

    private fun DivInputView.observeSelectAllOnFocus(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any -> setSelectAllOnFocus(div.selectAllOnFocus.evaluate(resolver)) }
        addSubscription(div.selectAllOnFocus.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeText(
        div: DivInput,
        resolver: ExpressionResolver,
        divView: Div2View
    ) {
        removeBoundVariableChangeAction()

        var inputMask: BaseInputMask? = null

        observeMask(div, resolver, divView) {
            inputMask = it

            inputMask?.let { mask ->
                setText(mask.value)
                setSelection(mask.cursorPosition)
            }
        }

        val primaryVariable: String?
        var secondaryVariable: String? = null

        if (div.mask != null) {
            val rawTextVariable = div.mask?.value()?.rawTextVariable ?: return

            primaryVariable = rawTextVariable
            secondaryVariable = div.textVariable
        } else {
            primaryVariable = div.textVariable
        }

        val setSecondVariable = { value: String ->
            if (secondaryVariable != null) divView.setVariable(secondaryVariable, value)
        }

        val callbacks = object : TwoWayStringVariableBinder.Callbacks {
            override fun onVariableChanged(value: String?) {
                val valueToSet = inputMask?.let {
                    it.overrideRawValue(value ?: "")

                    setSecondVariable(it.value)

                    it.value
                } ?: value

                setText(valueToSet)
            }

            override fun setViewStateChangeListener(valueUpdater: (String) -> Unit) {
                setBoundVariableChangeAction { editable ->
                    val fieldValue = editable?.toString() ?: ""

                    inputMask?.apply {
                        if (value != fieldValue) {
                            applyChangeFrom(text?.toString() ?: "", selectionStart)

                            setText(value)
                            setSelection(cursorPosition)

                            setSecondVariable(value)
                        }
                    }

                    val valueToUpdate = inputMask?.rawValue?.replace(',', '.') ?: fieldValue

                    valueUpdater(valueToUpdate)
                }
            }
        }

        addSubscription(variableBinder.bindVariable(divView, primaryVariable, callbacks))
    }

    private fun DivInputView.observeMask(
        div: DivInput,
        resolver: ExpressionResolver,
        divView: Div2View,
        onMaskUpdate: (BaseInputMask?) -> Unit
    ) {
        var inputMask: BaseInputMask? = null

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        val defaultKeyListener = keyListener

        val catchCommonMaskException = { exception: Exception, other: () -> Unit ->
            when (exception) {
                is PatternSyntaxException -> errorCollector.logError(
                    IllegalArgumentException("Invalid regex pattern '${exception.pattern}'.")
                )
                else -> other()
            }
        }

        val updateMaskData = { _: Any ->
            val divInputMask = div.mask?.value()

            inputMask = when (divInputMask) {
                is DivFixedLengthInputMask -> {
                    keyListener = defaultKeyListener

                    val maskData = BaseInputMask.MaskData(
                        divInputMask.pattern.evaluate(resolver),
                        divInputMask.patternElements.map {
                            BaseInputMask.MaskKey(
                                key = it.key.evaluate(resolver).first(),
                                filter = it.regex?.evaluate(resolver),
                                placeholder = it.placeholder.evaluate(resolver).first()
                            )
                        },
                        divInputMask.alwaysVisible.evaluate(resolver)
                    )

                    inputMask?.apply { updateMaskData(maskData) } ?: FixedLengthInputMask(maskData) {
                        catchCommonMaskException(it) { }
                    }
                }
                is DivCurrencyInputMask -> {
                    val evaluatedLocaleTag = divInputMask.locale?.evaluate(resolver)

                    val locale = if (evaluatedLocaleTag != null) {
                        Locale.forLanguageTag(evaluatedLocaleTag)
                            .apply {
                                val finalLanguageTag = toLanguageTag()

                                if (finalLanguageTag != evaluatedLocaleTag) {
                                    val exception = IllegalArgumentException("Original locale tag '$evaluatedLocaleTag' is not equals to final one '$finalLanguageTag'")

                                    errorCollector.logWarning(exception)
                                }
                            }
                    } else {
                        Locale.getDefault()
                    }

                    keyListener = DigitsKeyListener.getInstance("1234567890.,")

                    inputMask?.apply {
                        (inputMask as CurrencyInputMask)
                            .updateCurrencyParams(locale)
                    } ?: CurrencyInputMask(locale) {
                        catchCommonMaskException(it) { }
                    }
                }
                else -> {
                    keyListener = defaultKeyListener

                    null
                }
            }

            onMaskUpdate(inputMask)
        }

        when (val inputMask = div.mask?.value()) {
            is DivFixedLengthInputMask -> {
                addSubscription(inputMask.pattern.observe(resolver, updateMaskData))
                inputMask.patternElements.forEach { patternElement ->
                    addSubscription(patternElement.key.observe(resolver, updateMaskData))
                    patternElement.regex?.let { addSubscription(it.observe(resolver, updateMaskData)) }
                    addSubscription(patternElement.placeholder.observe(resolver, updateMaskData))
                }
                addSubscription(inputMask.alwaysVisible.observe(resolver, updateMaskData))
            }
            is DivCurrencyInputMask -> {
                inputMask.locale?.observe(resolver, updateMaskData)?.let { addSubscription(it) }
            }
            else -> Unit
        }

        updateMaskData(Unit)
    }
}
