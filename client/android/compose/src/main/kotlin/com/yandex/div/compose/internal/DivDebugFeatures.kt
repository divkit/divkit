package com.yandex.div.compose.internal

import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.views.DivLocalContext
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import javax.inject.Inject

@DivContextScope
@InternalApi
class DivDebugFeatures @Inject internal constructor(
    private val actionHandler: DivActionHandler
) {
    internal var lastViewLocalContext: DivLocalContext? = null

    val expressionResolver: ExpressionResolver?
        get() = lastViewLocalContext?.expressionResolver

    fun performAction(action: DivAction) {
        lastViewLocalContext?.let {
            actionHandler.handle(context = it.actionHandlingContext, action = action)
        }
    }
}
