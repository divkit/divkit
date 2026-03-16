package com.yandex.div.compose.actions

import com.yandex.div.core.annotations.PublicApi

@PublicApi
interface DivCustomActionHandler {
    fun handle(context: DivActionHandlingContext, action: DivActionData)
}
