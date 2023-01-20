@file:Suppress("NOTHING_TO_INLINE")

package com.yandex.div.core.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup

@DslMarker
@Target(AnnotationTarget.TYPE)
internal annotation class GridContainerDslMarker

internal inline fun Context.gridContainer(
    init: @GridContainerDslMarker GridContainer.() -> Unit = {}
): GridContainer {
    val gridContainer = GridContainer(this)
    gridContainer.init()
    return gridContainer
}

internal inline fun ViewGroup.view(
    init: @GridContainerDslMarker View.() -> Unit = {}
): View {
    val child = View(context)
    child.init()
    addView(child)
    return child
}

internal inline fun View.layoutParams(
    width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    init: DivLayoutParams.() -> Unit = {}
): DivLayoutParams {
    val params = DivLayoutParams(width, height)
    params.init()
    return params.also { layoutParams = it }
}
