package com.yandex.div.core.actions

import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionHaptic
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedHapticHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean {
        if (action !is DivActionTyped.Haptic) return false
        val feedback = action.value.feedback.evaluate(resolver)
        ViewCompat.performHapticFeedback(view, feedback.toHapticFeedbackConstant())
        return true
    }
}

private fun DivActionHaptic.Feedback.toHapticFeedbackConstant(): Int = when (this) {
    DivActionHaptic.Feedback.LIGHT -> HapticFeedbackConstantsCompat.SEGMENT_FREQUENT_TICK
    DivActionHaptic.Feedback.MEDIUM -> HapticFeedbackConstantsCompat.CONTEXT_CLICK
    DivActionHaptic.Feedback.HEAVY -> HapticFeedbackConstantsCompat.LONG_PRESS
    DivActionHaptic.Feedback.SUCCESS -> HapticFeedbackConstantsCompat.CONFIRM
    DivActionHaptic.Feedback.ERROR -> HapticFeedbackConstantsCompat.REJECT
}
