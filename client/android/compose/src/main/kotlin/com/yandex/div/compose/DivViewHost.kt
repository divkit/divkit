package com.yandex.div.compose

import android.util.AttributeSet
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import com.yandex.div.compose.host.CheckVisibilityCallback
import com.yandex.div.compose.host.LocalDivViewHost
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * [androidx.compose.ui.platform.ComposeView] host for [DivView].
 *
 * Use [setContent] on this host — not on [composeView] — so that
 * visibility actions work correctly. If [composeView] is inside a scrolling parent
 * ([androidx.core.widget.NestedScrollView], [android.widget.ScrollView], etc.),
 * call [onVisibleBoundsChanged] when the scroll position changes.
 *
 * ```
 * val host = DivViewHost(divContext)
 * host.setContent { DivView(data) }
 *
 * nestedScrollView.setOnScrollChangeListener { _, _, _, _, _ ->
 *     host.onVisibleBoundsChanged()
 * }
 * ```
 */
@ExperimentalApi
class DivViewHost(
    internal val divContext: DivContext,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) {
    val composeView = ComposeView(divContext, attrs, defStyleAttr)

    private val callbacks = mutableSetOf<CheckVisibilityCallback>()

    private var layoutListener: View.OnLayoutChangeListener? = null

    private val attachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) = addOnLayoutChangeListener()
        override fun onViewDetachedFromWindow(v: View) = removeOnLayoutChangeListener()
    }

    init {
        composeView.addOnAttachStateChangeListener(attachListener)
    }

    /**
     * Sets Compose content for this host.
     *
     * Do not call [ComposeView.setContent] on [composeView] directly — visibility actions
     * require content to be set through this method.
     */
    fun setContent(content: @Composable () -> Unit) = composeView.setContent {
        CompositionLocalProvider(LocalDivViewHost provides this) {
            content()
        }
    }

    fun onVisibleBoundsChanged() {
        trackVisibility()
    }

    internal fun addCallback(callback: CheckVisibilityCallback) {
        callbacks.add(callback)
        addOnLayoutChangeListener()
    }

    internal fun removeCallback(callback: CheckVisibilityCallback) {
        callbacks.remove(callback)
        if (callbacks.isEmpty()) {
            removeOnLayoutChangeListener()
        }
    }

    internal fun trackVisibility() {
        callbacks.forEach { it.checkVisibility() }
    }

    private fun addOnLayoutChangeListener() {
        if (layoutListener != null || callbacks.isEmpty()) return
        layoutListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            trackVisibility()
        }.also { composeView.addOnLayoutChangeListener(it) }
    }

    private fun removeOnLayoutChangeListener() {
        layoutListener?.let { composeView.removeOnLayoutChangeListener(it) }
        layoutListener = null
    }
}
