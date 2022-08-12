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
    width: Int = GridContainer.LayoutParams.WRAP_CONTENT,
    height: Int = GridContainer.LayoutParams.WRAP_CONTENT,
    init: GridContainer.LayoutParams.() -> Unit = {}
): GridContainer.LayoutParams {
    val params = GridContainer.LayoutParams()
    params.width = width
    params.height = height
    params.init()
    return params.also { layoutParams = it }
}
