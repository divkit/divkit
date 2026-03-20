package com.yandex.div.core.view2.debugview

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Listens to window insets on [root], computes a top margin when the overlay would intersect
 * the status bar, and applies that margin to the currently set target view.
 */
internal class SafeAreaTopMarginController(
    private val root: ViewGroup,
) {
    private val rootLocationInWindow = IntArray(2)
    private var topMarginPx: Int = 0
    private val attachStateChangeListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = tryStart()
        override fun onViewDetachedFromWindow(v: View) = Unit
    }

    var targetView: View? = null
        set(value) {
            field = value
            value?.tryApplyTopMargin()
        }

    init {
        if (root.isAttachedToWindow) {
            tryStart()
        } else {
            root.addOnAttachStateChangeListener(attachStateChangeListener)
        }
    }

    private fun tryStart() {
        if (!isStatusBarOverlappingActivityWindow()) {
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            updateTopMarginFromInsets(insets)
            insets
        }
        ViewCompat.getRootWindowInsets(root)?.let { insets ->
            updateTopMarginFromInsets(insets)
        }
    }

    private fun updateTopMarginFromInsets(insets: WindowInsetsCompat) {
        val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        root.getLocationInWindow(rootLocationInWindow)
        val rootTopInWindow = rootLocationInWindow[1]
        topMarginPx = if (topInset > 0 && rootTopInWindow < topInset) {
            topInset - rootTopInWindow
        } else {
            0
        }
        targetView?.tryApplyTopMargin()
    }

    /**
     * Tries to guess if the window is configured so that the status bar
     * can overlap its content. Note that this method won't work
     * under configurations that enable oveloapping of status bar with
     * [WindowCompat.setDecorFitsSystemWindows] cause sdk does not provide
     * a getter.
     */
    private fun isStatusBarOverlappingActivityWindow(): Boolean {
        val activity = findActivityContext(root) ?: return false
        val window = activity.window
        val flags = window.attributes.flags

        // Theme/window flag: android:windowTranslucentStatus="true"
        @Suppress("DEPRECATION")
        if ((flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0) {
            return true
        }

        // Other ways a window can lay out under the status bar
        if ((flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
            return true
        }

        @Suppress("DEPRECATION")
        if ((window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) != 0) {
            return true
        }

        return false
    }

    private fun findActivityContext(view: View): Activity? {
        var current: View? = view
        while (current != null) {
            (current.context as? Activity)?.let { return it }
            current = current.parent as? View
        }
        return null
    }


    private fun View.tryApplyTopMargin() {
        val params = (layoutParams as? ViewGroup.MarginLayoutParams) ?: return
        if (params.topMargin == topMarginPx) {
            return
        }

        params.topMargin = topMarginPx
        requestLayout()
    }

    fun close() {
        root.removeOnAttachStateChangeListener(attachStateChangeListener)
        targetView = null
    }
}
