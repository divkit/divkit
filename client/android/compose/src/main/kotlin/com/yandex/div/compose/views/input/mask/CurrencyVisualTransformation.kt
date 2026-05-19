package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.variables.mutableStateFromVariable
import com.yandex.div.compose.views.input.DivInputState
import com.yandex.div2.DivCurrencyInputMask
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun DivCurrencyInputMask.rememberCurrencyInputState(
    textVariable: String,
): DivInputState {
    val raw = mutableStateFromVariable(rawTextVariable, defaultValue = "")
    val display = mutableStateFromVariable(textVariable, defaultValue = "")
    val params = rememberCurrencyMaskParams()
    val transformation = remember(params) { CurrencyVisualTransformation(params) }

    SyncMaskedRaw(raw, display, sanitize = params::transformRaw, format = params::format)

    return remember(raw, params) {
        DivInputState(
            source = raw,
            visualTransformation = transformation,
            sanitize = params::transformRaw,
        )
    }
}

@Composable
private fun DivCurrencyInputMask.rememberCurrencyMaskParams(): CurrencyMaskParams {
    val localeTag = locale?.observedValue()
    val reporter = LocalComponent.current.reporter

    return remember(localeTag) {
        val locale = if (localeTag != null) {
            Locale.forLanguageTag(localeTag).also {
                val finalTag = it.toLanguageTag()
                if (finalTag != localeTag) {
                    reporter.reportWarning(
                        "Original locale tag '$localeTag' is not equals to final one '$finalTag'"
                    )
                }
            }
        } else {
            Locale.getDefault()
        }
        CurrencyMaskParams(locale)
    }
}

private class CurrencyVisualTransformation(
    private val params: CurrencyMaskParams,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val pattern = params.computeDisplayPattern(raw)
        return buildMaskedText(
            raw = raw,
            pattern = pattern,
            isDynamic = { it == DIGIT_PATTERN || it == params.decimalSeparator },
        )
    }
}

private class CurrencyMaskParams(locale: Locale) {
    private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(locale).also { fmt ->
        if (fmt is DecimalFormat) {
            val pattern = fmt.toPattern().filter { it != CURRENCY_KEY }.trim()
            fmt.applyPattern(pattern)
        }
    }
    private val symbols: DecimalFormatSymbols = (formatter as DecimalFormat).decimalFormatSymbols

    val decimalSeparator: Char = symbols.decimalSeparator
    val groupingSeparator: Char = symbols.groupingSeparator
    val maxFractionDigits: Int = formatter.maximumFractionDigits

    fun transformRaw(raw: String): String {
        val sb = StringBuilder()
        var sawDecimal = false
        var fracDigits = 0
        for (char in raw) {
            when {
                char == decimalSeparator && !sawDecimal && maxFractionDigits > 0 -> {
                    sawDecimal = true
                    sb.append(char)
                }
                char.isDigit() -> {
                    if (sawDecimal) {
                        if (fracDigits < maxFractionDigits) {
                            sb.append(char)
                            fracDigits++
                        }
                    } else {
                        sb.append(char)
                    }
                }
            }
        }
        return sb.toString()
    }

    fun computeDisplayPattern(raw: String): String {
        val decIdx = raw.indexOf(decimalSeparator)
        val intLen = if (decIdx == -1) raw.length else decIdx
        val fracLen = if (decIdx == -1) 0 else raw.length - decIdx - 1

        val sb = StringBuilder()
        for (i in 0 until intLen) {
            sb.append(DIGIT_PATTERN)
            if (i < intLen - 1 && (intLen - i - 1) % 3 == 0) {
                sb.append(groupingSeparator)
            }
        }
        if (decIdx != -1) {
            sb.append(decimalSeparator)
            repeat(fracLen) { sb.append(DIGIT_PATTERN) }
        }
        return sb.toString()
    }

    fun format(raw: String): String = buildMaskedText(
        raw,
        computeDisplayPattern(raw),
        isDynamic = { it == DIGIT_PATTERN || it == decimalSeparator },
    ).text.text
}

private const val DIGIT_PATTERN = '#'
private const val CURRENCY_KEY = '¤'
