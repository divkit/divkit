package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.util.mask.MaskHelper
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivFixedLengthInputMask
import com.yandex.div2.DivInput
import com.yandex.div2.DivSizeUnit
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
        val maxLinesExpr = div.maxVisibleLines ?: return

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
            DivInput.KeyboardType.NUMBER -> InputType.TYPE_CLASS_NUMBER
            DivInput.KeyboardType.PHONE -> InputType.TYPE_CLASS_PHONE
            DivInput.KeyboardType.DECIMAL -> InputType.TYPE_CLASS_NUMBER or
             InputType.TYPE_NUMBER_FLAG_DECIMAL
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

        var maskHelper: MaskHelper? = null

        observeMask(div, resolver, divView) {
            maskHelper = it

            maskHelper?.let { mask ->
                setText(mask.value)
                setSelection(mask.cursorPosition)
            }
        }

        val callbacks = object : TwoWayStringVariableBinder.Callbacks {
            override fun onVariableChanged(value: String?) {
                val valueToSet = maskHelper?.let {
                    it.applyChangeFrom(value ?: "")

                    it.value
                } ?: value

                setText(valueToSet)
            }

            override fun setViewStateChangeListener(valueUpdater: (String) -> Unit) {
                setBoundVariableChangeAction { editable ->
                    val fieldValue = editable?.toString() ?: ""

                    maskHelper?.apply {
                        if (value != fieldValue) {
                            applyChangeFrom(text?.toString() ?: "")

                            setText(value)
                            setSelection(cursorPosition)
                        }
                    }

                    val valueToUpdate = maskHelper?.rawValue ?: fieldValue

                    valueUpdater(valueToUpdate)
                }
            }
        }

        addSubscription(variableBinder.bindVariable(divView, div.textVariable, callbacks))
    }

    private fun DivInputView.observeMask(
        div: DivInput,
        resolver: ExpressionResolver,
        divView: Div2View,
        onMaskUpdate: (MaskHelper?) -> Unit
    ) {
        var maskHelper: MaskHelper? = null

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        val updateMaskData = { _: Any ->
            val inputMask = div.mask?.value()

            maskHelper = when (inputMask) {
                null -> null
                is DivFixedLengthInputMask -> {
                    val maskData = MaskHelper.MaskData(
                        inputMask.pattern.evaluate(resolver),
                        inputMask.patternElements.map {
                            MaskHelper.MaskKey(
                                key = it.key.evaluate(resolver).first(),
                                filter = it.regex?.evaluate(resolver),
                                placeholder = it.placeholder.evaluate(resolver).first()
                            )
                        },
                        inputMask.alwaysVisible.evaluate(resolver)
                    )

                    maskHelper?.apply { updateMaskData(maskData) } ?: MaskHelper(maskData) {
                        when (it) {
                            is PatternSyntaxException -> errorCollector.logError(
                                IllegalArgumentException("Invalid regex pattern '${it.pattern}'.")
                            )
                        }
                    }
                }
                else -> null
            }

            onMaskUpdate(maskHelper)
        }

        when (val inputMask = div.mask?.value()) {
            null -> Unit
            is DivFixedLengthInputMask -> {
                addSubscription(inputMask.pattern.observeAndGet(resolver, updateMaskData))
                inputMask.patternElements.forEach { patternElement ->
                    addSubscription(patternElement.key.observe(resolver, updateMaskData))
                    patternElement.regex?.let { addSubscription(it.observe(resolver, updateMaskData)) }
                    addSubscription(patternElement.placeholder.observe(resolver, updateMaskData))
                }
                addSubscription(inputMask.alwaysVisible.observe(resolver, updateMaskData))
            }
            else -> Unit
        }
    }
}
