package com.yandex.div.core

import com.yandex.div.core.action.DivActionInfo
import com.yandex.div.core.action.toInfo
import com.yandex.div.core.actions.DivTypedActionHandler
import com.yandex.div.core.downloader.DivDownloadActionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivAction

internal object DivActionHandlerInternal {

    @JvmStatic
    fun handleAction(
        action: DivAction,
        view: DivViewFacade,
        divActionInfoHandler: (DivActionInfo) -> Boolean
    ): Boolean {
        return when (action) {
            is DivAction.Default -> {
                val info = action.toInfo(view.expressionResolver)
                when {
                    DivDownloadActionHandler.canHandle(info.url, view) -> {
                        DivDownloadActionHandler.handleAction(action, view as Div2View)
                    }
                    else -> divActionInfoHandler(info)
                }
            }

            else -> DivTypedActionHandler.handleAction(action, view)
        }
    }
}