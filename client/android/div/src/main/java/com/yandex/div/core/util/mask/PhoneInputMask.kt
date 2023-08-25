package com.yandex.div.core.util.mask

import com.yandex.div2.PhoneMasks
import org.json.JSONObject

internal class PhoneInputMask(
    private val onError: ((Exception) -> Unit)
) : BaseInputMask(DEFAULT_MASK_DATA) {

    override fun overrideRawValue(newRawValue: String) {
        tryInvalidateMaskDataWith(newRawValue)
        super.overrideRawValue(newRawValue)
    }

    override fun applyChangeFrom(newValue: String, position: Int?) {
        val textDiff = TextDiff.build(value, newValue).let { initialDiff ->
            if (position == null) return@let initialDiff
            TextDiff(
                start = (position - initialDiff.added).coerceAtLeast(0),
                added = initialDiff.added,
                removed = initialDiff.removed
            )
        }
        val oldRawValue = rawValue

        val tailStart = replaceBodyTail(textDiff, newValue)

        val newRawValue = rawValue
        val newPattern = newMaskPatternFor(newRawValue)

        if (newPattern == null) {
            calculateCursorPosition(textDiff, tailStart)
            return
        }

        updateMaskDataWith(newPattern)
        replaceChars(newRawValue, 0)

        val rawValueDiff = TextDiff.build(oldRawValue, newRawValue)
        val dynamicDestination = rawValueDiff.start + rawValueDiff.added
        calculateCursorPositionBy(dynamicDestination)
    }

    private fun calculateCursorPositionBy(dynamicDestination: Int) {
        var index = 0
        var dynamicCounter = 0

        while (index < destructedValue.size && dynamicCounter < dynamicDestination) {
            if (destructedValue[index++] is MaskChar.Dynamic) dynamicCounter++
        }

        cursorPosition = firstHolderAfter(index)
    }

    private fun tryInvalidateMaskDataWith(rawValue: String) =
        newMaskPatternFor(rawValue)?.let { newPattern -> updateMaskDataWith(newPattern) }

    private fun newMaskPatternFor(rawValue: String): String? {
        val newPattern = rawValue.phoneMaskPattern
        val currentPattern = maskData.pattern
        return if (newPattern != currentPattern) newPattern else null
    }

    private fun updateMaskDataWith(newPattern: String) = updateMaskData(
        newMaskData = MaskData(
            pattern = newPattern,
            decoding = DEFAULT_DECODING_MASK_KEY,
            alwaysVisible = maskData.alwaysVisible
        ),
        restoreValue = false
    )

    override fun onException(exception: Exception) {
        onError.invoke(exception)
    }
}

internal val DEFAULT_DECODING_MASK_KEY = listOf(
    BaseInputMask.MaskKey(
        key = '0',
        filter = "\\d",
        placeholder = '_'
    )
)

internal val DEFAULT_MASK_DATA = BaseInputMask.MaskData(
    pattern = "".phoneMaskPattern,
    decoding = DEFAULT_DECODING_MASK_KEY,
    alwaysVisible = false
)

private const val COUNTRY_CODE_END_MARKER = "*"
private const val UNIVERSAL_MASK = "000000000000000"

internal val String.phoneMaskPattern: String
    get() {
        if (isBlank()) return UNIVERSAL_MASK
        var current: JSONObject = PhoneMasks.VALUE_DEFAULT_VALUE
        var countryCodeInd = 0
        while (!current.has("value")) {
            if (countryCodeInd >= length) {
                current = current.get(COUNTRY_CODE_END_MARKER) as JSONObject
                break
            }
            val digit = get(countryCodeInd++).toString()
            current = current.get(
                if (current.has(digit)) digit else COUNTRY_CODE_END_MARKER
            ) as JSONObject
        }
        return current.getString("value") + PhoneMasks.EXTRA_NUMBERS
    }
