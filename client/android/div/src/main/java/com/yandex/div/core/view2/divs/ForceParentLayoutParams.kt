package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat

internal class ForceParentLayoutParams(
    private val wrappedParams: ViewGroup.LayoutParams
) {
    private var widthFromParent: Int? = null
    private var widthFromChild: Int? = null
    private var heightFromParent: Int? = null
    private var heightFromChild: Int? = null

    private fun setSizeFromParent(w: Int?, h: Int?) {
        widthFromParent = w
        heightFromParent = h

        (w ?: widthFromChild)?.let {
            wrappedParams.width = it
        }
        (h ?: heightFromChild)?.let {
            wrappedParams.height = it
        }
    }

    private fun setSizeFromChild(w: Int?, h: Int?) {
        widthFromChild = w
        heightFromChild = h
        if (widthFromParent == null && w != null) {
            wrappedParams.width = w
        }
        if (heightFromParent == null && h != null) {
            wrappedParams.height = h
        }
    }

    class Linear(source: LinearLayout.LayoutParams) : LinearLayoutCompat.LayoutParams(source) {

        val parentParams = ForceParentLayoutParams(this)

        init {
            weight = source.weight
            gravity = source.gravity
        }
    }

    class Frame(source: FrameLayout.LayoutParams) : FrameLayout.LayoutParams(source) {
        val parentParams = ForceParentLayoutParams(this)
    }

    companion object {
        fun setSizeFromParent(view: View, w: Int? = null, h: Int? = null) {
            val params = when (val lp = view.layoutParams) {
                is Linear -> lp.parentParams
                is Frame -> lp.parentParams
                is LinearLayout.LayoutParams -> {
                    val linear = Linear(lp)
                    view.layoutParams = linear
                    linear.parentParams
                }
                is FrameLayout.LayoutParams -> {
                    val linear = Frame(lp)
                    view.layoutParams = linear
                    linear.parentParams
                }
                else -> null
            }
            if (params != null) {
                params.setSizeFromParent(w, h)
            } else {
                setDefaultParams(view, w, h)
            }
            view.requestLayout()
        }

        fun setSizeFromChild(view: View, w: Int? = null, h: Int? = null) {
            val params = when (val lp = view.layoutParams) {
                is Linear -> lp.parentParams
                is Frame -> lp.parentParams
                else -> null
            }
            if (params != null) {
                params.setSizeFromChild(w, h)
            } else {
                setDefaultParams(view, w, h)
                view.requestLayout()
            }
        }

        private fun setDefaultParams(view: View, w: Int?, h: Int?) {
            if (w != null) {
                view.layoutParams.width = w
            }
            if (h != null) {
                view.layoutParams.height = h
            }
        }
    }
}
