package com.yandex.div.test.data

import androidx.core.net.toUri
import com.yandex.div2.DivActionTyped
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivVisibilityAction

fun visibilityAction(
    delayMs: Long = 800,
    isEnabled: Boolean = true,
    limit: Int = 1,
    percentage: Int = 50,
    typed: DivActionTyped? = null,
    url: String? = null,
): DivVisibilityAction {
    return DivVisibilityAction(
        isEnabled = constant(isEnabled),
        logId = constant("test"),
        logLimit = constant(limit.toLong()),
        typed = typed,
        url = url?.let { constant(it.toUri()) },
        visibilityDuration = constant(delayMs),
        visibilityPercentage = constant(percentage.toLong())
    )
}

fun disappearAction(
    delayMs: Long = 800,
    id: String = "test",
    isEnabled: Boolean = true,
    limit: Int = 1,
    typed: DivActionTyped? = null,
    url: String? = null,
): DivDisappearAction {
    return DivDisappearAction(
        disappearDuration = constant(delayMs),
        isEnabled = constant(isEnabled),
        logId = constant(id),
        logLimit = constant(limit.toLong()),
        typed = typed,
        url = url?.let { constant(it.toUri()) }
    )
}
