package com.yandex.div.steps

import android.view.ViewGroup
import com.yandex.div.core.Div2Context
import com.yandex.test.util.StepsDsl

internal fun divView(block: DivViewSteps.() -> Unit) = block(DivViewSteps())

@StepsDsl
class DivViewSteps: DivTestAssetSteps() {

    fun Div2Context.buildContainer(tag: String): Unit = ru.tinkoff.allure.step("Build container") {
        buildContainer(
            width = ViewGroup.LayoutParams.MATCH_PARENT,
            height = ViewGroup.LayoutParams.MATCH_PARENT,
            tag = tag
        )
    }

    fun cleanUp() = runOnMainSync {
        div2View.cleanup()
    }

    fun attachToParent() = runOnMainSync {
        container.addView(div2View)
    }

    fun detachFromParent() = runOnMainSync {
        container.removeView(div2View)
    }
}
