package com.yandex.div.core.view2.state

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.StateConflictException
import com.yandex.div.core.view2.BindingContext
import com.yandex.div2.DivData

internal interface DivStateSwitcher {
    @Throws(StateConflictException::class)
    fun switchStates(bindingContext: BindingContext, state: DivData.State, paths: List<DivStatePath>)
}
