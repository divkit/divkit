package com.yandex.div.compose

import android.util.AttributeSet
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.ComposeView
import com.yandex.div.compose.host.LocalDivViewHost
import com.yandex.div.compose.visibility.CheckVisibilityCallback
import com.yandex.div2.DivData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * [androidx.compose.ui.platform.ComposeView] host for [DivView].
 *
 * Use [setContent] on this host — not on [composeView] — so that
 * visibility actions work correctly and resources start loading.
 * If [composeView] is inside a scrolling parent
 * ([androidx.core.widget.NestedScrollView], [android.widget.ScrollView], etc.),
 * call [onVisibleBoundsChanged] when the scroll position changes.
 *
 * ```
 * val host = DivViewHost(divContext)
 * host.setContent(data)
 *
 * // or wrap DivView:
 * host.setContent(data) {
 *     Box { DivView(data) }
 * }
 *
 * // or custom content without DivData:
 * host.setContent {
 *     MyComposable()
 * }
 *
 * nestedScrollView.setOnScrollChangeListener { _, _, _, _, _ ->
 *     host.onVisibleBoundsChanged()
 * }
 * ```
 */
@Suppress("TooManyFunctions")
class DivViewHost(
    internal val divContext: DivContext,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) {
    val composeView = ComposeView(divContext, attrs, defStyleAttr)

    private val callbacks = mutableSetOf<CheckVisibilityCallback>()
    private val contentUnboundCallbacks = mutableSetOf<() -> Unit>()

    private var layoutListener: View.OnLayoutChangeListener? = null

    private var preloadJob: Job? = null

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
     * and resource preloading require content to be set through this host.
     *
     * @param preloadMode how to preload resources for [data]. Defaults to [PreloadMode.DISABLED].
     */
    fun setContent(
        data: DivData,
        preloadMode: PreloadMode = PreloadMode.DISABLED,
        content: @Composable () -> Unit = { DivView(data) },
    ) {
        cancelPreload()
        if (preloadMode != PreloadMode.DISABLED) {
            preloadJob = divContext.component.mainCoroutineScope.launch {
                divContext.component.preloader.preload(data, preloadMode)
            }
        }
        setHostContent(content)
    }

    /**
     * Sets Compose content for this host without [DivData].
     *
     * Use for custom content when the host is only needed for visibility tracking.
     *
     * Do not call [ComposeView.setContent] on [composeView] directly — visibility actions
     * require content to be set through this host.
     */
    fun setContent(content: @Composable () -> Unit) {
        cancelPreload()
        setHostContent(content)
    }

    private fun setHostContent(content: @Composable () -> Unit) {
        composeView.setContent {
            CompositionLocalProvider(LocalDivViewHost provides this) {
                content()
            }
        }
    }

    private fun cancelPreload() {
        preloadJob?.cancel()
        preloadJob = null
    }

    fun onVisibleBoundsChanged() {
        trackVisibility()
    }

    /**
     * Notifies the hosted content that it is about to be permanently unbound.
     *
     * Pending visibility actions are cancelled and eligible disappear actions are dispatched
     * synchronously. This method does not dispose the composition or remove its content.
     */
    fun unbind() {
        cancelPreload()
        val callbacksSnapshot = callbacks.toList()
        val contentUnboundCallbacksSnapshot = contentUnboundCallbacks.toList()
        callbacksSnapshot.forEach { it.onContentUnbound() }
        contentUnboundCallbacksSnapshot.forEach { it() }
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

    internal fun addContentUnboundCallback(callback: () -> Unit) {
        contentUnboundCallbacks.add(callback)
    }

    internal fun removeContentUnboundCallback(callback: () -> Unit) {
        contentUnboundCallbacks.remove(callback)
    }

    internal fun trackVisibility() {
        callbacks.toList().forEach { it.checkVisibility() }
    }

    internal fun getVisibleRectInWindow(): Rect? {
        val globalRect = android.graphics.Rect()
        return if (composeView.getGlobalVisibleRect(globalRect)) {
            globalRect.toComposeRect()
        } else {
            null
        }
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
