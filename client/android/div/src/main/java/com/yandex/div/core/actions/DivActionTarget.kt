package com.yandex.div.core.actions

import com.yandex.div.core.util.ViewDivBlockSearchEnvironment
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlockLocator
import com.yandex.div.json.expressions.ExpressionResolver

internal fun Div2View.actionTargetBlockLocator(): DivBlockLocator? {
    val environment = ViewDivBlockSearchEnvironment(this)
    environment.rootBlock() ?: return null
    return DivBlockLocator(environment)
}

internal fun Div2View.findScopeResolver(
    scopeId: String?,
    resolver: ExpressionResolver,
): ExpressionResolver {
    scopeId ?: return resolver
    var isScopeMissing = false
    val scope = actionTargetBlockLocator()?.findTarget(
        divId = scopeId,
        scopeId = scopeId,
        actionResolver = resolver,
        matches = { true },
        reportWarning = {
            isScopeMissing = true
            logError(it)
        },
    )?.onFailure { if (!isScopeMissing) logError(it) }?.getOrNull()
    return scope?.expressionResolver ?: resolver
}
