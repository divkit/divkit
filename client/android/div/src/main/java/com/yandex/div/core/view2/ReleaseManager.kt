package com.yandex.div.core.view2

import android.view.View
import androidx.core.view.doOnAttach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.yandex.div.core.Div2Context
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.internal.Log
import java.util.WeakHashMap
import javax.inject.Inject

/**
 * The class responsible for releasing views when the corresponding LifecycleOwner is destroyed.
 */
@DivScope
@Mockable
internal class ReleaseManager @Inject constructor() {
    private val viewToRelease = hashMapOf<LifecycleOwner, MutableSet<Releasable>>()
    private val divViewOwnerMap = WeakHashMap<Div2View, LifecycleOwner>()
    private val monitor = Any()

    private val observer = LifecycleEventObserver { source, event ->
        synchronized(monitor) {
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    viewToRelease[source]?.forEach {
                        it.release()
                    }
                    viewToRelease.remove(source)
                }
                else -> Unit
            }
        }
    }

    /**
     * Attaches [release()][Releasable.release] method call of the [view] to the
     * [LifecycleOwner]'s [onDestroy()][Lifecycle.State.DESTROYED] event found in [Div2Context]
     * or manually when view attached to the window.
     */
    fun observeViewLifecycle(divView: Div2View, view: Releasable) = synchronized(monitor) {
        val lifecycleOwner = divView.context.lifecycleOwner ?: divViewOwnerMap[divView]

        if (lifecycleOwner != null) {
            addLifecycleListener(lifecycleOwner, view)
            return@synchronized
        }

        if (view !is View) {
            Log.w(TAG, NOT_ATTACHED_TO_LIFECYCLE_WARNING)
            return@synchronized
        }

        view.doOnAttach {
            synchronized(monitor) {
                divViewOwnerMap[divView]?.let {
                    addLifecycleListener(it, view)
                    return@doOnAttach
                }

                val owner = ViewTreeLifecycleOwner.get(view)
                if (owner != null) {
                    divViewOwnerMap[divView] = owner
                    addLifecycleListener(owner, view)
                } else {
                    Log.w(TAG, NOT_ATTACHED_TO_LIFECYCLE_WARNING)
                }
            }
        }
    }

    private fun addLifecycleListener(lifecycleOwner: LifecycleOwner, view: Releasable) {
        if (viewToRelease.containsKey(lifecycleOwner)) {
            viewToRelease[lifecycleOwner]?.add(view)
        } else {
            viewToRelease[lifecycleOwner] = mutableSetOf(view)
            lifecycleOwner.lifecycle.addObserver(observer)
        }
    }

    /**
     * Removes view from lifecycle observing list. Use if view was released externally.
     */
    fun stopObservingView(divView: Div2View, view: Releasable) = synchronized(monitor) {
        val lifecycleOwner = divView.context.lifecycleOwner ?: divViewOwnerMap[divView] ?: return@synchronized

        viewToRelease[lifecycleOwner]?.remove(view)

        if (viewToRelease[lifecycleOwner]?.size == 0) {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewToRelease.remove(lifecycleOwner)
        }
    }

    companion object {
        const val TAG = "ReleaseManager"
        const val NOT_ATTACHED_TO_LIFECYCLE_WARNING =
            "Attempt to bind a view (that needs to be released) via Div2View, which has no LifecycleOwner. " +
                "Release event will not be caught! If you're using some long-lived resources, " +
                "like a video player, call cleanup explicitly when you don't need Div2View anymore"
    }
}
