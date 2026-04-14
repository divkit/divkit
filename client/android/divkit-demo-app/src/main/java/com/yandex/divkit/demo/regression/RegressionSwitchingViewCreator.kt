package com.yandex.divkit.demo.regression

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.yandex.divkit.demo.Container
import com.yandex.divkit.regression.ScenarioLogDelegate
import com.yandex.divkit.regression.ScenarioViewCreator

class RegressionSwitchingViewCreator(
    private val div2ViewCreator: RegressionDiv2ViewCreator,
    private val composeViewCreator: RegressionComposeViewCreator,
) : ScenarioViewCreator {

    override fun createView(
        activity: Activity,
        scenarioPath: String,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate,
        onBound: (View) -> Unit,
    ) {
        if (Container.preferences.useComposeRenderer) {
            composeViewCreator.createView(activity, scenarioPath, onBound)
        } else {
            div2ViewCreator.createDiv2ViewByConfig(activity, scenarioPath, parent, logDelegate, onBound)
        }
    }
}
