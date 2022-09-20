package com.yandex.div.core.view2.animations

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.doOnDetach
import androidx.core.view.drawToBitmap
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import com.yandex.div.R
import com.yandex.div.core.util.Assert
import com.yandex.div.core.view2.divs.widgets.DivImageView

@MainThread
internal fun createOrGetVisualCopy(
    view: View,
    sceneRoot: ViewGroup,
    transition: Transition,
    endPosition: IntArray
): View {
    val tag = view.getTag(R.id.save_overlay_view) as? View

    if (tag != null) return tag

    val copy = ImageView(view.context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        if (ViewCompat.isLaidOut(view)) {
            setImageBitmap(view.drawToBitmap())
        } else {
            Assert.fail("Cannot make full copy, origin not yet laid out!")
        }
    }

    val widthSpec = MeasureSpec.makeMeasureSpec(view.width, MeasureSpec.EXACTLY)
    val heightSpec = MeasureSpec.makeMeasureSpec(view.height, MeasureSpec.EXACTLY)

    copy.measure(widthSpec, heightSpec)
    copy.layout(0, 0, view.width, view.height)

    copy.invalidatePosition(sceneRoot, endPosition)

    view.setTag(R.id.save_overlay_view, copy)

    view.replace(copy, transition, sceneRoot)

    view.setHierarchyImageChangeCallback {
        copy.setImageBitmap(view.drawToBitmap())
    }

    copy.doOnDetach { view.setHierarchyImageChangeCallback(null) }

    return copy
}

private fun View.invalidatePosition(sceneRoot: ViewGroup, endPosition: IntArray) {
    val position = IntArray(2)
    sceneRoot.getLocationOnScreen(position)

    offsetLeftAndRight(endPosition[0] - position[0])
    offsetTopAndBottom(endPosition[1] - position[1])
}

private fun View.replace(viewCopy: View, transition: Transition, sceneRoot: ViewGroup) {
    val overlay = sceneRoot.overlay
    visibility = View.INVISIBLE
    overlay.add(viewCopy)

    transition.addListener(object : TransitionListenerAdapter() {
        override fun onTransitionStart(transition: Transition) {
            visibility = View.INVISIBLE
        }

        override fun onTransitionPause(transition: Transition) {
            overlay.remove(viewCopy)
        }

        override fun onTransitionResume(transition: Transition) {
            if (viewCopy.parent == null) {
                overlay.add(viewCopy)
            }
        }

        override fun onTransitionEnd(transition: Transition) {
            setTag(R.id.save_overlay_view, null)
            visibility = View.VISIBLE
            overlay.remove(viewCopy)
            transition.removeListener(this)
        }
    })
}

fun View.setHierarchyImageChangeCallback(callback: (() -> Unit)? = null) {
    when (this) {
        is DivImageView -> setImageChangeCallback(callback)
        is ViewGroup -> children.forEach { it.setHierarchyImageChangeCallback(callback) }
    }
}
