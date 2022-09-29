package com.yandex.div.core.view2.animations

import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues

internal fun Transition.getViewForAnimate(
    view: View,
    sceneRoot: ViewGroup,
    values: TransitionValues,
    positionKey: String
): View {
    return if (view.isLaidOut) {
        createOrGetVisualCopy(view, sceneRoot, this, values.values[positionKey] as IntArray)
    } else {
        view
    }
}

internal fun capturePosition(transitionValues: TransitionValues, savePosition: (IntArray) -> Unit) {
    val view = transitionValues.view
    val position = IntArray(2)
    view.getLocationOnScreen(position)
    savePosition(position)
}
