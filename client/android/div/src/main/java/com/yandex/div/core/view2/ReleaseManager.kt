package com.yandex.div.core.view2

import androidx.core.view.doOnAttach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.yandex.div.core.Div2Context
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.RuntimeStoreProvider
import com.yandex.div.internal.Log
import javax.inject.Inject

/**
 * The class responsible for releasing divs when the corresponding LifecycleOwner is destroyed.
 */
@DivScope
@Mockable
internal class ReleaseManager @Inject constructor(
    private val runtimeStoreProvider: RuntimeStoreProvider,
) {
    private val divToRelease = hashMapOf<LifecycleOwner, MutableSet<Div2View>>()
    private val monitor = Any()

    private val observer = LifecycleEventObserver { source, event ->
        synchronized(monitor) {
            when (event) {
                Lifecycle.Event.ON_DESTROY -> {
                    divToRelease[source]?.forEach {
                        it.cleanup()
                        runtimeStoreProvider.cleanupRuntime(it)
                    }
                    divToRelease.remove(source)
                }
                else -> Unit
            }
        }
    }

    /**
     * Attaches [cleanup()][Div2View.cleanup] method call of the [Div2View] to the
     * [LifecycleOwner]'s [onDestroy()][Lifecycle.State.DESTROYED] event found in [Div2Context]
     * or manually when it attached to the window.
     */
    fun observeDivLifecycle(divView: Div2View) {
        val lifecycleOwner = divView.context.lifecycleOwner

        if (lifecycleOwner != null) {
            addLifecycleListener(lifecycleOwner, divView)
            return
        }

        divView.doOnAttach {
            val owner = divView.findViewTreeLifecycleOwner()

            if (owner != null) {
                addLifecycleListener(owner, divView)
            } else {
                Log.w(TAG, NOT_ATTACHED_TO_LIFECYCLE_WARNING)
            }
        }
    }

    private fun addLifecycleListener(lifecycleOwner: LifecycleOwner, divView: Div2View) = synchronized(monitor) {
        if (divToRelease.containsKey(lifecycleOwner)) {
            divToRelease[lifecycleOwner]?.add(divView)
        } else {
            divToRelease[lifecycleOwner] = mutableSetOf(divView)
            lifecycleOwner.lifecycle.addObserver(observer)
        }
    }

    companion object {
        const val TAG = "ReleaseManager"
        const val NOT_ATTACHED_TO_LIFECYCLE_WARNING =
            "Attempt to bind a Div2View, which has no LifecycleOwner. " +
                "Release event will not be caught! If you're using some long-lived resources, " +
                "like a video player, call cleanup explicitly when you don't need Div2View anymore"
    }
}
