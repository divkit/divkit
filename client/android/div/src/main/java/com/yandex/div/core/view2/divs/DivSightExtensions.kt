package com.yandex.div.core.view2.divs

import com.yandex.div.internal.KAssert
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivVisibilityAction

internal val DivSightAction.duration: Expression<Long>
    get() {
        return when (this) {
            is DivVisibilityAction -> this.visibilityDuration
            is DivDisappearAction -> this.disappearDuration
            else -> {
                KAssert.fail { "Trying to get duration field for unsupported DivSightAction class" }
                Expression.constant(0)
            }
        }
    }
