package com.yandex.div.compose.views.input

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.PlatformTextInputInterceptor
import androidx.compose.ui.text.input.ImeAction
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.observedEnabledActions
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.di.handleActions
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivInput
import com.yandex.div2.DivInputMask

/**
 * The keyboard of an input and what its Enter key does.
 */
@OptIn(ExperimentalComposeUiApi::class)
internal class InputKeyboard(
    val options: KeyboardOptions,
    val onKeyboardAction: KeyboardActionHandler?,
    val interceptor: PlatformTextInputInterceptor,

    /**
     * Performs the action of the Enter key of a multi-line input; false when the key inserts
     * a newline.
     */
    val onEnter: () -> Boolean,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun DivInput.rememberInputKeyboard(keyboardType: DivInput.KeyboardType): InputKeyboard {
    val enterKeyType = enterKeyType.observedValue()
    val autocapitalization = autocapitalization.observedValue()
    val options = remember(keyboardType, enterKeyType, autocapitalization) {
        keyboardOptions(keyboardType, enterKeyType, autocapitalization)
            .copy(autoCorrectEnabled = false)
    }

    val component = LocalComponent.current
    val enterKeyActions = enterKeyActions.observedEnabledActions()
    val onKeyboardAction = remember(enterKeyActions, options.imeAction) {
        when {
            enterKeyActions.isNotEmpty() -> KeyboardActionHandler {
                component.handleActions(enterKeyActions, DivActionSource.KEYBOARD)
            }
            options.imeAction == ImeAction.Default -> null
            // A specific Enter key without actions does nothing, as in the View renderer.
            else -> KeyboardActionHandler {}
        }
    }

    val hasDigitsMask = mask is DivInputMask.Currency || mask is DivInputMask.Phone
    val multiline = keyboardType == DivInput.KeyboardType.MULTI_LINE_TEXT && !hasDigitsMask
    // Enter key actions replace the newline, as the schema defines them, even for the default Enter key.
    val consumeEnter = multiline && (options.imeAction != ImeAction.Default || enterKeyActions.isNotEmpty())
    val onEnter = remember(consumeEnter, onKeyboardAction) {
        {
            if (consumeEnter) onKeyboardAction?.onKeyboardAction {}
            consumeEnter
        }
    }
    // The interceptor restarts the keyboard session when it changes: it reads the current Enter handling instead.
    val currentOnEnter = rememberUpdatedState(onEnter)
    val interceptor = remember(keyboardType, hasDigitsMask) {
        EnterInputInterceptor(
            signedDecimal = keyboardType == DivInput.KeyboardType.NUMBER && !hasDigitsMask,
            onMultilineEnter = if (multiline) { { currentOnEnter.value() } } else null,
        )
    }
    return remember(options, onKeyboardAction, interceptor, onEnter) {
        InputKeyboard(options, onKeyboardAction, interceptor, onEnter)
    }
}
