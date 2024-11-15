package com.yandex.div.core.view2.divs.widgets

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

internal abstract class DivViewVisitor {
    open fun defaultVisit(view: DivHolderView<*>) = Unit
    open fun visit(view: DivWrapLayout) = defaultVisit(view)
    open fun visit(view: DivFrameLayout) = defaultVisit(view)
    open fun visit(view: DivGifImageView) = defaultVisit(view)
    open fun visit(view: DivGridLayout) = defaultVisit(view)
    open fun visit(view: DivImageView) = defaultVisit(view)
    open fun visit(view: DivLinearLayout) = defaultVisit(view)
    open fun visit(view: DivLineHeightTextView) = defaultVisit(view)
    open fun visit(view: DivPagerIndicatorView) = defaultVisit(view)
    open fun visit(view: DivPagerView) = defaultVisit(view)
    open fun visit(view: DivRecyclerView) = defaultVisit(view)
    open fun visit(view: DivSeparatorView) = defaultVisit(view)
    open fun visit(view: DivStateLayout) = defaultVisit(view)
    open fun visit(view: DivTabsLayout) = defaultVisit(view)
    open fun visit(view: DivSliderView) = defaultVisit(view)
    open fun visit(view: DivSelectView) = defaultVisit(view)
    open fun visit(view: DivVideoView) = defaultVisit(view)
    open fun visit(view: DivCustomWrapper) = defaultVisit(view)
    open fun visit(view: DivSwitchView) = defaultVisit(view)
    open fun visit(view: View) = Unit
}

internal fun DivViewVisitor.visitViewTree(view: View) {
    when (view) {
        is DivWrapLayout -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivFrameLayout -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivGridLayout -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivLinearLayout -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivPagerView -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivRecyclerView -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivStateLayout -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivTabsLayout -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivCustomWrapper -> {
            view.visitChild(this::visitViewTree)
            visit(view)
        }
        is DivSeparatorView -> visit(view)
        is DivGifImageView -> visit(view)
        is DivImageView -> visit(view)
        is DivLineHeightTextView -> visit(view)
        is DivPagerIndicatorView -> visit(view)
        is DivSliderView -> visit(view)
        is DivSelectView -> visit(view)
        is DivVideoView -> visit(view)
        is DivSwitchView -> visit(view)
        else -> {
            if (view is ViewGroup) {
                view.visitChild(this::visitViewTree)
            }
            visit(view)
        }
    }
}

private inline fun ViewGroup.visitChild(func: (childView: View) -> Unit) = children.forEach { func(it) }
