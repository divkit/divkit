package com.yandex.divkit.regression

import android.app.Activity
import android.view.View
import android.view.ViewGroup

fun interface ScenarioViewCreator {
    fun createView(
        activity: Activity,
        scenarioPath: String,
        parent: ViewGroup,
        logDelegate: ScenarioLogDelegate,
        onBound: (View) -> Unit,
    )
}
