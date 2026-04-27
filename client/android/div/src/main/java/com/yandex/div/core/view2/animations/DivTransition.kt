package com.yandex.div.core.view2.animations

import androidx.transition.Visibility
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivChangeTransition

internal sealed class DivTransition {
    class Appearance(val value: DivAppearanceTransition, @field:Visibility.Mode val mode: Int) : DivTransition()
    class Change(val value: DivChangeTransition) : DivTransition()
}
