package com.yandex.div.core.view2

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.core.Div2Context
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.internal.Log
import javax.inject.Inject

/**
 * The class responsible for releasing views when the corresponding LifecycleOwner is destroyed.
 */
@DivScope
@Mockable
internal class ReleaseManager @Inject constructor() {
    private val viewToRelease = hashMapOf<LifecycleOwner, MutableSet<Releasable>>()
    private val monitor = Any()

    private val observer = LifecycleEventObserver { source, event ->
        synchronized(monitor) {
            when(event) {
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
     * [lifecycleOwner]'s [onDestroy()][Lifecycle.State.DESTROYED] event.
     */
    fun observeViewLifecycle(lifecycleOwner: LifecycleOwner?, view: Releasable) = synchronized(monitor) {
        if (lifecycleOwner == null) {
            Log.w(TAG, NOT_ATTACHED_TO_LIFECYCLE_WARNING)
            return@synchronized
        }

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
    fun stopObservingView(context: Div2Context, view: Releasable) = synchronized(monitor) {
        val lifecycleOwner = context.lifecycleOwner ?: return@synchronized

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
