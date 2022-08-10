package com.yandex.div.core.view2.divs.widgets

import android.view.ViewGroup

interface DivAnimator {

    fun startDivAnimation() {
        if (this is ViewGroup) {
            for(i in 0..childCount) {
                (getChildAt(i) as? DivAnimator)?.startDivAnimation()
            }
        }
    }

    fun stopDivAnimation() {
        if (this is ViewGroup) {
            for(i in 0..childCount) {
                (getChildAt(i) as? DivAnimator)?.stopDivAnimation()
            }
        }
    }
}