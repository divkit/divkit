package com.yandex.div.core.action

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction

internal fun DivAction.toInfo(resolver: ExpressionResolver): DivActionInfo = when (this) {
    is DivAction.Default -> toInfo(resolver)
}

internal fun DivAction.Default.toInfo(resolver: ExpressionResolver) = DivActionInfo(
    downloadCallbacks = value.downloadCallbacks,
    logId = value.logId,
    logUrl = value.logUrl?.evaluate(resolver),
    menuItems = value.menuItems,
    payload = value.payload,
    referer = value.referer?.evaluate(resolver),
    target = value.target?.evaluate(resolver),
    url = value.url?.evaluate(resolver)
)

