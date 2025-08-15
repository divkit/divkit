package com.yandex.div.core.util.mask

internal open class FixedLengthInputMask(
    initialMaskData: MaskData,
    private val onError: ((Exception) -> Unit)
) : BaseInputMask(initialMaskData) {
    override fun onException(exception: Exception) {
        onError.invoke(exception)
    }
}
