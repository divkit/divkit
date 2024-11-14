package com.yandex.div.core.view2.divs

import android.graphics.Color
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.core.widget.doAfterTextChanged
import com.yandex.div.core.actions.closeKeyboard
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.mask.BaseInputMask
import com.yandex.div.core.util.mask.CurrencyInputMask
import com.yandex.div.core.util.mask.DEFAULT_MASK_DATA
import com.yandex.div.core.util.mask.FixedLengthInputMask
import com.yandex.div.core.util.mask.PhoneInputMask
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.util.validator.ExpressionValidator
import com.yandex.div.core.util.validator.RegexValidator
import com.yandex.div.core.util.validator.ValidatorItemData
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_ENTER
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivCurrencyInputMask
import com.yandex.div2.DivFixedLengthInputMask
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputValidator
import com.yandex.div2.DivPhoneInputMask
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
    private val actionBinder: DivActionBinder,
    private val accessibilityStateProvider: AccessibilityStateProvider,
    private val errorCollectors: ErrorCollectors
) : DivViewBinder<DivInput, DivInputView> {

    override fun bindView(context: BindingContext, view: DivInputView, div: DivInput, path: DivStatePath) {
        val oldDiv = view.div
        if (div === oldDiv) return

        val expressionResolver = context.expressionResolver

        baseBinder.bindView(context, view, div, oldDiv)

        view.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
            accessibilityEnabled = accessibilityStateProvider.isAccessibilityEnabled(view.context)

            observeBackground(context, div, oldDiv, expressionResolver)

            observeFontSize(div, expressionResolver)
            observeTypeface(div, expressionResolver)
            observeTextColor(div, expressionResolver)
            observeTextAlignment(div.textAlignmentHorizontal, div.textAlignmentVertical, expressionResolver)
            observeLineHeight(div, expressionResolver)
            observeMaxVisibleLines(div, expressionResolver)
            observeMaxLength(div, expressionResolver)

            observeHintText(div, expressionResolver)
            observeHintColor(div, expressionResolver)
            observeHighlightColor(div, expressionResolver)

            observeKeyboardTypeAndCapitalization(div, expressionResolver)
            observeEnterTypeAndActions(div, context, expressionResolver)
            observeSelectAllOnFocus(div, expressionResolver)
            observeIsEnabled(div, expressionResolver)

            observeText(div, context, path)

            focusTracker = context.divView.inputFocusTracker
            focusTracker?.requestFocusIfNeeded(view)
        }
    }

    private fun DivInputView.observeTextAlignment(
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

    private fun DivInputView.applyTextAlignment(
        horizontalAlignment: DivAlignmentHorizontal?,
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

    private fun DivInputView.observeBackground(
        bindingContext: BindingContext,
        newDiv: DivInput,
        oldDiv: DivInput?,
        resolver: ExpressionResolver
    ) {
        if (newDiv.nativeInterface.equalsToConstant(oldDiv?.nativeInterface)) {
            return
        }

        applyNativeBackgroundColor(bindingContext, newDiv, oldDiv)

        if (newDiv.nativeInterface.isConstant()) {
            return
        }

        addSubscription(
            newDiv.nativeInterface?.color?.observeAndGet(resolver) {
                applyNativeBackgroundColor(bindingContext, newDiv, oldDiv)
            }
        )
    }

    private fun DivInputView.applyNativeBackgroundColor(
        bindingContext: BindingContext,
        newDiv: DivInput,
        oldDiv: DivInput?
    ) {
        val resolver = bindingContext.expressionResolver
        val nativeBackgroundColor = newDiv.nativeInterface?.color?.evaluate(resolver) ?: Color.TRANSPARENT
        val background = if (nativeBackgroundColor == Color.TRANSPARENT) {
            null
        } else {
            nativeBackground?.apply { setTint(nativeBackgroundColor) }
        }
        baseBinder.bindBackground(bindingContext, this, newDiv, oldDiv, expressionSubscriber, background)
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
        applyTypeface(div, resolver)
        val callback = { _: Any ->  applyTypeface(div, resolver) }
        div.fontFamily?.observeAndGet(resolver, callback)?.let { addSubscription(it) }
        addSubscription(div.fontWeight.observe(resolver, callback))
        addSubscription(div.fontWeightValue?.observe(resolver, callback))
    }

    private fun DivInputView.applyTypeface(div: DivInput, resolver: ExpressionResolver) {
        typeface = typefaceResolver.getTypeface(
            div.fontFamily?.evaluate(resolver),
            div.fontWeight.evaluate(resolver),
            div.fontWeightValue?.evaluate(resolver)
        )
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

    private fun DivInputView.observeMaxVisibleLines(div: DivInput, resolver: ExpressionResolver) {
        val maxLinesExpr = div.maxVisibleLines ?: return

        val callback = { _: Any -> maxLines = maxLinesExpr.evaluate(resolver).toIntSafely() }
        addSubscription(maxLinesExpr.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeMaxLength(div: DivInput, resolver: ExpressionResolver) {
        val maxLengthExpr = div.maxLength ?: return

        val callback = { _: Any ->
            filters = arrayOf(InputFilter.LengthFilter(maxLengthExpr.evaluate(resolver).toIntSafely()))
        }
        addSubscription(maxLengthExpr.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeHintText(div: DivInput, resolver: ExpressionResolver) {
        val hintTextExpr = div.hintText ?: return

        val callback = { _: Any -> setInputHint(hintTextExpr.evaluate(resolver)) }
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

    private fun DivInputView.observeKeyboardTypeAndCapitalization(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any ->
            val type = div.keyboardType.evaluate(resolver)
            inputType = getKeyboardType(type) or getCapitalization(div, resolver)
            setHorizontallyScrolling(type != DivInput.KeyboardType.MULTI_LINE_TEXT)
        }
        addSubscription(div.keyboardType.observe(resolver, callback))
        addSubscription(div.autocapitalization.observeAndGet(resolver, callback))
    }

    private fun getKeyboardType(type: DivInput.KeyboardType): Int {
        return when(type) {
            DivInput.KeyboardType.SINGLE_LINE_TEXT -> InputType.TYPE_CLASS_TEXT
            DivInput.KeyboardType.MULTI_LINE_TEXT -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE
            DivInput.KeyboardType.EMAIL -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            DivInput.KeyboardType.URI -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_URI
            DivInput.KeyboardType.NUMBER -> InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            DivInput.KeyboardType.PHONE -> InputType.TYPE_CLASS_PHONE
            DivInput.KeyboardType.PASSWORD -> InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun DivInputView.observeEnterTypeAndActions(
        div: DivInput,
        bindingContext: BindingContext,
        resolver: ExpressionResolver
    ) {
        val callback = { _: Any ->
            val enterKeyType = div.enterKeyType.evaluate(resolver)
            this.imeOptions = getImeAction(enterKeyType)

            val actions = div.enterKeyActions
            if (!actions.isNullOrEmpty()) {
                this.setOnEditorActionListener { _, actionId, _ ->
                    if ((actionId and EditorInfo.IME_MASK_ACTION) != 0) {
                        actionBinder.handleBulkActions(bindingContext, this, actions, LOG_ENTER)
                    }

                    false
                }
            } else {
                this.setOnEditorActionListener(null)
            }
        }
        addSubscription(div.enterKeyType.observeAndGet(resolver, callback))
    }

    private fun getImeAction(type: DivInput.EnterKeyType): Int {
        return when (type) {
            DivInput.EnterKeyType.DEFAULT -> EditorInfo.IME_NULL
            DivInput.EnterKeyType.SEND -> EditorInfo.IME_ACTION_SEND
            DivInput.EnterKeyType.DONE -> EditorInfo.IME_ACTION_DONE
            DivInput.EnterKeyType.SEARCH -> EditorInfo.IME_ACTION_SEARCH
            DivInput.EnterKeyType.GO -> EditorInfo.IME_ACTION_GO
        }
    }

    private fun DivInputView.observeSelectAllOnFocus(div: DivInput, resolver: ExpressionResolver) {
        val callback = { _: Any -> setSelectAllOnFocus(div.selectAllOnFocus.evaluate(resolver)) }
        addSubscription(div.selectAllOnFocus.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeIsEnabled(div: DivInput, resolver: ExpressionResolver) {
        val callback = { isEnabled: Boolean ->
            if (!isEnabled && isFocused) closeKeyboard()
            enabled = isEnabled
        }
        addSubscription(div.isEnabled.observeAndGet(resolver, callback))
    }

    private fun DivInputView.observeText(
        div: DivInput,
        bindingContext: BindingContext,
        path: DivStatePath,
    ) {
        val divView = bindingContext.divView
        removeAfterTextChangeListener()

        var inputMask: BaseInputMask? = null

        observeMask(div, bindingContext.expressionResolver, divView) {
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
                addAfterTextChangeAction { editable ->
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

        addSubscription(variableBinder.bindVariable(bindingContext, primaryVariable, callbacks, path))

        observeValidators(div, bindingContext.expressionResolver, divView)
    }

    private fun DivInputView.observeValidators(
        div: DivInput,
        resolver: ExpressionResolver,
        divView: Div2View
    ) {
        val validators: MutableList<ValidatorItemData> = mutableListOf()

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        val revalidateExpressionValidator = { index: Int  ->
            validators[index].validate(text.toString(), this, divView, resolver)
        }

        doAfterTextChanged { editable ->
            if (editable != null) {
                validators.forEach { it.validate(text.toString(), this, divView, resolver) }
            }
        }

        val callback = { _: Any ->
            validators.clear()

            val divValidators = div.validators

            if (divValidators != null) {
                divValidators.forEach { divInputValidator ->
                    val validatorItemData = divInputValidator
                        .toValidatorDataItem(resolver, errorCollector)

                    if (validatorItemData != null) {
                        validators += validatorItemData
                    }
                }

                validators.forEach { it.validate(text.toString(), this, divView, resolver) }
            }
        }

        div.validators?.forEachIndexed { index, divInputValidator ->
            return@forEachIndexed when (divInputValidator) {
                is DivInputValidator.Regex -> {
                    addSubscription(divInputValidator.value.pattern.observe(resolver, callback))
                    addSubscription(divInputValidator.value.labelId.observe(resolver, callback))
                    addSubscription(divInputValidator.value.allowEmpty.observe(resolver, callback))
                }
                is DivInputValidator.Expression -> {
                    addSubscription(divInputValidator.value.condition.observe(resolver) {
                        revalidateExpressionValidator(index)
                    })
                    addSubscription(divInputValidator.value.labelId.observe(resolver, callback))
                    addSubscription(divInputValidator.value.allowEmpty.observe(resolver, callback))
                }
            }
        }

        callback(Unit)
    }

    private fun DivInputValidator.toValidatorDataItem(
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ): ValidatorItemData? {
        return when (this) {
            is DivInputValidator.Regex -> {
                val regexValidator = value

                try {
                    val regex = Regex(regexValidator.pattern.evaluate(resolver))

                    ValidatorItemData(
                        validator = RegexValidator(
                            regex,
                            regexValidator.allowEmpty.evaluate(resolver)
                        ),
                        variableName = regexValidator.variable,
                        labelId = regexValidator.labelId.evaluate(resolver)
                    )
                } catch (exception: PatternSyntaxException) {
                    errorCollector.logError(
                        IllegalArgumentException("Invalid regex pattern '${exception.pattern}'", exception)
                    )

                    return null
                }
            }
            is DivInputValidator.Expression -> {
                val expressionValidator = value

                ValidatorItemData(
                    validator = ExpressionValidator(
                        expressionValidator.allowEmpty.evaluate(resolver)
                    ) {
                        expressionValidator.condition.evaluate(resolver)
                    },
                    variableName = expressionValidator.variable,
                    labelId = expressionValidator.labelId.evaluate(resolver)
                )
            }
        }
    }

    private fun ValidatorItemData.validate(
        newValue: String,
        view: DivInputView,
        divView: Div2View,
        resolver: ExpressionResolver,
    ) {
        val isValid = validator.validate(newValue)

        VariableMutationHandler.setVariable(divView, variableName, isValid.toString(), resolver)

        attachAccessibility(divView, view, isValid)
    }

    private fun ValidatorItemData.attachAccessibility(
        divView: Div2View,
        view: DivInputView,
        isValid: Boolean
    ) {
        val exception = IllegalArgumentException("Can't find label with id '$labelId'")

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)
        val viewIdProvider = divView.viewComponent.viewIdProvider

        view.doOnLayout {
            val labelId = viewIdProvider.getViewId(labelId)

            if (labelId != View.NO_ID) {
                val label = view.rootView.findViewById<View>(labelId)

                if (label != null) {
                    label.labelFor = if (isValid) View.NO_ID else view.id
                } else {
                    errorCollector.logError(exception)
                }
            } else {
                errorCollector.logError(exception)
            }
        }
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
                                // Empty char if placeholder string is empty
                                placeholder = it.placeholder.evaluate(resolver).firstOrNull() ?: Char.MIN_VALUE
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
                is DivPhoneInputMask -> {
                    keyListener = DigitsKeyListener.getInstance("1234567890")

                    inputMask?.apply { updateMaskData(DEFAULT_MASK_DATA) } ?: PhoneInputMask {
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

    private fun getCapitalization(
        div: DivInput,
        resolver: ExpressionResolver,
    ): Int {
        return when (div.autocapitalization.evaluate(resolver)) {
            DivInput.Autocapitalization.SENTENCES -> InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            DivInput.Autocapitalization.WORDS -> InputType.TYPE_TEXT_FLAG_CAP_WORDS
            DivInput.Autocapitalization.ALL_CHARACTERS -> InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            else -> 0
        }
    }
}
