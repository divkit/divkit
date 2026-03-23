package com.yandex.div.compose.internal

import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div2.DivAction
import javax.inject.Inject

@DivContextScope
@InternalApi
class DivActionPerformer @Inject internal constructor(
    private val actionHandler: DivActionHandler
) {
    internal var actionHandlingContext: DivActionHandlingContext? = null

    fun perform(action: DivAction) {
        actionHandlingContext?.let {
            actionHandler.handle(context = it, action = action)
        }
    }
}
