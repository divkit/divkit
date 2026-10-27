package com.yandex.div.compose.views.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.coerceIn
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.focus.trackInputFocus
import com.yandex.div.compose.text.observeBaseTextStyle
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.toAlignment
import com.yandex.div.compose.views.input.mask.InputMask
import com.yandex.div.compose.views.input.mask.PhoneInputMask
import com.yandex.div.compose.views.input.mask.rememberCurrencyInputMask
import com.yandex.div.compose.views.input.mask.rememberFixedLengthInputMask
import com.yandex.div.compose.views.modifiers.bindFocusTarget
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask

@Composable
internal fun DivInputView(modifier: Modifier, data: DivInput) {
    val mask = data.mask?.rememberInputMask()
    val config = data.rememberInputConfig(mask)
    val state = data.rememberInputState(config)
    val validators = data.validators
    val errorMessage = if (validators.isNullOrEmpty()) null else {
        // The View renderer validates the text the field shows: the formatted text of a masked input.
        val failed = validators.validate(state.shownText(config))
        validators.observeErrorMessage(failed)
    }

    InputField(modifier, data, state, config, errorMessage)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InputField(
    modifier: Modifier,
    data: DivInput,
    state: DivInputState,
    config: InputConfig,
    errorMessage: String?,
) {
    val textAlignmentHorizontal = data.textAlignmentHorizontal.observedValue()
    val textAlignmentVertical = data.textAlignmentVertical.observedValue()
    val textStyle = data.observeTextStyle(textAlignmentHorizontal)
    val keyboardType = data.keyboardType.observedValue()
    val keyboard = data.rememberInputKeyboard(keyboardType)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPassword = keyboardType == DivInput.KeyboardType.PASSWORD
    val singleLine = keyboardType != DivInput.KeyboardType.MULTI_LINE_TEXT
    val maxLines = if (singleLine) 1 else {
        data.maxVisibleLines?.observedIntValue()?.coerceAtLeast(1) ?: Int.MAX_VALUE
    }
    // The secure field has no output transformation, so it shows no placeholders of a mask.
    val showsPlaceholders = config.showsPlaceholders && !isPassword
    val decorator = data.hintDecorator(
        field = state.field,
        showsPlaceholders = showsPlaceholders,
        textStyle = textStyle,
        textAlignmentHorizontal = textAlignmentHorizontal,
        maxLines = maxLines,
    )

    Box(
        modifier = modifier
            .trackInputFocus()
            .onPreviewKeyEvent { it.nativeKeyEvent.isEnterDown() && keyboard.onEnter() },
        contentAlignment = toAlignment(textAlignmentHorizontal, textAlignmentVertical),
    ) {
        InterceptPlatformTextInput(keyboard.interceptor) {
            SelectionColors(data) {
                InputTextField(
                    state = state,
                    config = config,
                    data = data,
                    isPassword = isPassword,
                    keyboard = keyboard,
                    textStyle = textStyle,
                    lineLimits = if (singleLine) TextFieldLineLimits.SingleLine else {
                        TextFieldLineLimits.MultiLine(maxHeightInLines = maxLines)
                    },
                    interactionSource = interactionSource,
                    isFocused = isFocused,
                    decorator = decorator,
                    errorMessage = errorMessage,
                )
            }
        }
    }
}

@Composable
private fun DivInput.observeTextStyle(textAlignmentHorizontal: DivAlignmentHorizontal): TextStyle {
    return observeBaseTextStyle(
        fontSize = fontSize.observedIntValue(),
        fontSizeUnit = fontSizeUnit.observedValue(),
        textColor = textColor,
        fontWeight = fontWeight,
        fontWeightValue = fontWeightValue,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        lineHeight = lineHeight?.observedIntValue(),
        textAlignmentHorizontal = textAlignmentHorizontal,
        fontVariationSettings = fontVariationSettings
    )
}

@Composable
private fun InputTextField(
    state: DivInputState,
    config: InputConfig,
    data: DivInput,
    isPassword: Boolean,
    keyboard: InputKeyboard,
    textStyle: TextStyle,
    lineLimits: TextFieldLineLimits,
    interactionSource: MutableInteractionSource,
    isFocused: Boolean,
    decorator: TextFieldDecorator,
    errorMessage: String?,
) {
    val field = state.field
    val enabled = data.isEnabled.observedValue()
    KeepFocusAcrossPasswordSwitch(field, isPassword, isFocused, enabled, state.focusRequester)
    SelectAllOnFocus(state, isFocused, data.selectAllOnFocus.observedValue())

    val fieldModifier = Modifier
        .fillMaxWidth()
        .onFocusChanged { state.trackFocus(it.isFocused) }
        .bindFocusTarget(data.id, state.focusRequester, input = state)
        // Accessibility services announce the error of the field: the labels of the failed validators.
        .applyIfNotNull(errorMessage) { semantics { error(it) } }
    if (isPassword) {
        BasicSecureTextField(
            state = field,
            modifier = fieldModifier,
            enabled = enabled,
            inputTransformation = config.inputTransformation,
            textStyle = textStyle,
            keyboardOptions = keyboard.options,
            onKeyboardAction = keyboard.onKeyboardAction,
            interactionSource = interactionSource,
            decorator = decorator,
            // Every character stays hidden, as it did with PasswordVisualTransformation.
            textObfuscationMode = TextObfuscationMode.Hidden,
        )
    } else {
        BasicTextField(
            state = field,
            modifier = fieldModifier,
            enabled = enabled,
            inputTransformation = config.inputTransformation,
            textStyle = textStyle,
            keyboardOptions = keyboard.options,
            onKeyboardAction = keyboard.onKeyboardAction,
            lineLimits = lineLimits,
            interactionSource = interactionSource,
            outputTransformation = config.mask?.outputTransformation,
            decorator = decorator,
        )
    }
}

/** The password field is another composable: a focused input keeps its focus and selection across the switch. */
@Composable
private fun KeepFocusAcrossPasswordSwitch(
    field: TextFieldState,
    isPassword: Boolean,
    isFocused: Boolean,
    enabled: Boolean,
    focusRequester: FocusRequester,
) {
    val focusedSelection = remember(isPassword) { if (isFocused) field.selection else null }
    LaunchedEffect(isPassword) {
        if (focusedSelection != null && enabled) {
            focusRequester.requestFocus()
            field.edit { selection = focusedSelection.coerceIn(0, length) }
        }
    }
}

@Composable
private fun SelectAllOnFocus(state: DivInputState, isFocused: Boolean, selectAllOnFocus: Boolean) {
    // A change of the property applies on the next focus, as in the View renderer.
    val currentSelectAllOnFocus by rememberUpdatedState(selectAllOnFocus)
    LaunchedEffect(isFocused) { state.onFocusChanged(isFocused, currentSelectAllOnFocus) }
}

@Composable
private fun SelectionColors(data: DivInput, content: @Composable () -> Unit) {
    val highlightColor = data.highlightColor?.observedColorValue() ?: return content()
    val defaults = LocalTextSelectionColors.current
    val colors = remember(highlightColor, defaults) {
        TextSelectionColors(handleColor = defaults.handleColor, backgroundColor = highlightColor)
    }
    CompositionLocalProvider(LocalTextSelectionColors provides colors, content)
}

@Composable
private fun DivInput.hintDecorator(
    field: TextFieldState,
    showsPlaceholders: Boolean,
    textStyle: TextStyle,
    textAlignmentHorizontal: DivAlignmentHorizontal,
    maxLines: Int,
): TextFieldDecorator {
    val hintText = hintText?.observedValue()
    val hintColor = hintColor.observedColorValue()
    return TextFieldDecorator { innerTextField ->
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = toAlignment(textAlignmentHorizontal, DivAlignmentVertical.TOP),
        ) {
            if (hintText != null && field.text.isEmpty() && !showsPlaceholders) {
                BasicText(
                    text = hintText,
                    style = textStyle.copy(color = hintColor),
                    maxLines = maxLines,
                )
            }
            innerTextField()
        }
    }
}

@Composable
private fun DivInputMask.rememberInputMask(): InputMask = when (this) {
    is DivInputMask.FixedLength -> value.rememberFixedLengthInputMask()
    is DivInputMask.Phone -> PhoneInputMask
    is DivInputMask.Currency -> value.rememberCurrencyInputMask()
}
