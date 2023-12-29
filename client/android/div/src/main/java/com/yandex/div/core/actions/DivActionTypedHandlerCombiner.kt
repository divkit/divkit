package com.yandex.div.core.actions

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KLog
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedHandlerCombiner @Inject constructor(
    private val handlers: Set<@JvmSuppressWildcards DivActionTypedHandler>
) {

    fun handleAction(action: DivActionTyped, div2View: Div2View): Boolean {
        val wasHandled = handlers.find { it.handleAction(action, div2View) } != null
        if (!wasHandled) {
            KLog.d(TAG) { "Unexpected ${action::class.java} was not handled" }
        }
        return wasHandled
    }

    private companion object {
        const val TAG = "DivTypedActionHandlerCombiner"
    }
}
