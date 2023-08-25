package com.yandex.div.core.actions

import com.yandex.div.core.DivViewFacade
import com.yandex.div2.DivAction

internal object DivTypedActionHandler {

    @JvmStatic
    fun handleAction(action: DivAction, view: DivViewFacade): Boolean {
        when (action) {
            is DivAction.Default -> return false
        }
    }
}