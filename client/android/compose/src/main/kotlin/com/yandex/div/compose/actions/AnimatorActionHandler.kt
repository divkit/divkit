package com.yandex.div.compose.actions

import com.yandex.div.compose.animation.AnimatorStorage
import com.yandex.div2.DivActionAnimatorStart
import com.yandex.div2.DivActionAnimatorStop
import javax.inject.Inject

internal class AnimatorActionHandler @Inject constructor(
    private val animatorStorage: AnimatorStorage,
) {
    fun handle(scopeId: String?, action: DivActionAnimatorStart) {
        if (scopeId != null) {
            animatorStorage.start(scopeId, action)
        }
    }

    fun handle(scopeId: String?, action: DivActionAnimatorStop) {
        if (scopeId != null) {
            animatorStorage.stop(scopeId, action.animatorId)
        }
    }
}

