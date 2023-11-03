package com.yandex.div.core.view2.state

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData

internal interface DivStateSwitcher {

    fun switchStates(state: DivData.State, paths: List<DivStatePath>, resolver: ExpressionResolver)
}
