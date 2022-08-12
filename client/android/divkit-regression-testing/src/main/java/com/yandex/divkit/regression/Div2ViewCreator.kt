package com.yandex.divkit.regression

import android.app.Activity
import android.view.ViewGroup
import com.yandex.div.core.view2.Div2View

interface Div2ViewCreator {

    fun createDiv2View(
        activity: Activity,
        scenarioPath: String,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate
    ): Div2View
}
