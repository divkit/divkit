package com.yandex.div.core.view2.divs.widgets

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.yandex.div.internal.widget.tabs.TabsLayout

internal abstract class DivViewVisitor {
    open fun visit(view: DivWrapLayout) {}
    open fun visit(view: DivFrameLayout) {}
    open fun visit(view: DivGifImageView) {}
    open fun visit(view: DivGridLayout) {}
    open fun visit(view: DivImageView) {}
    open fun visit(view: DivLinearLayout) {}
    open fun visit(view: DivLineHeightTextView) {}
    open fun visit(view: DivPagerIndicatorView) {}
    open fun visit(view: DivPagerView) {}
    open fun visit(view: DivRecyclerView) {}
    open fun visit(view: DivSeparatorView) {}
    open fun visit(view: DivSnappyRecyclerView) {}
    open fun visit(view: DivStateLayout) {}
    open fun visit(view: TabsLayout) {}
    open fun visit(view: DivSliderView) {}
    open fun visit(view: View) {}
}

internal fun DivViewVisitor.visitViewTree(view: View) {
    when (view) {
        is DivWrapLayout -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is DivFrameLayout -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is DivGridLayout -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is DivLinearLayout -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is DivPagerView -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is DivRecyclerView -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is DivSnappyRecyclerView -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is DivStateLayout -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is TabsLayout -> {
            visit(view)
            view.visitChild(this::visitViewTree)
        }
        is DivSeparatorView -> visit(view)
        is DivGifImageView -> visit(view)
        is DivImageView -> visit(view)
        is DivLineHeightTextView -> visit(view)
        is DivPagerIndicatorView -> visit(view)
        is DivSliderView -> visit(view)
        else -> {
            visit(view)
            if (view is ViewGroup) {
                view.visitChild(this::visitViewTree)
            }
        }
    }
}

private inline fun ViewGroup.visitChild(func: (childView: View) -> Unit) = children.forEach { func(it) }
