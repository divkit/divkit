package com.yandex.div.core.actions

import com.yandex.div.core.util.ViewDivBlockSearchEnvironment
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlockLocator

internal fun Div2View.actionTargetBlockLocator(): DivBlockLocator? {
    val environment = ViewDivBlockSearchEnvironment(this)
    environment.rootBlock() ?: return null
    return DivBlockLocator(environment)
}
