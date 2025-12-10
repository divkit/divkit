package com.yandex.divkit.regression

import android.app.Activity
import android.view.ViewGroup
import com.yandex.div.core.view2.Div2View

interface Div2ViewCreator {

    fun createDiv2ViewSync(
        activity: Activity,
        scenarioPath: String,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate,
    ): Div2View

    fun createDiv2ViewByConfig(
        activity: Activity,
        scenarioPath: String,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate,
        onBound: (Div2View) -> Unit,
    )
}
