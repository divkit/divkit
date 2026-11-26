package com.yandex.div.compose.views.transition

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.GraphicsContext
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.platform.LocalGraphicsContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.LocalComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

@Composable
internal fun BindStateChangeSnapshots(cache: StateChangeSnapshotCache) {
    val graphicsContext = LocalGraphicsContext.current
    val coroutineScope = rememberCoroutineScope()
    val reporter = LocalComponent.current.reporter
    cache.layerStore = remember(graphicsContext, coroutineScope, reporter) {
        StateChangeLayerStore(graphicsContext, coroutineScope, reporter)
    }
}

internal class StateChangeLayerStore(
    private val context: GraphicsContext,
    val coroutineScope: CoroutineScope,
    private val reporter: DivReporter,
) : RememberObserver {
    private val layers = mutableSetOf<GraphicsLayer>()
    private val freezeJobs = mutableMapOf<StateChangeSnapshotNode, Job>()
    private var released = false

    fun createLayer(): GraphicsLayer = context.createGraphicsLayer().also(layers::add)

    fun releaseLayer(layer: GraphicsLayer) {
        if (layers.remove(layer)) context.releaseGraphicsLayer(layer)
    }

    fun freeze(snapshot: StateChangeSnapshot, layout: StateChangeLayoutState?) {
        val version = ++snapshot.captureVersion
        val previousJob = freezeJobs[snapshot.node]
        val size = snapshot.layer.size
        if (size.width == 0 || size.height == 0) return

        val captureArea = snapshot.captureArea(size, layout)
        val frozenLayer = snapshot.frozen?.layer ?: createLayer()
        snapshot.frozen = StateChangeFrozenSnapshot(frozenLayer, captureArea.offset)
        freezeJobs[snapshot.node] = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            previousJob?.cancelAndJoin()
            val captureLayer = context.createGraphicsLayer()
            try {
                val image = snapshot.freezeImage(captureLayer, frozenLayer, captureArea)
                if (released || snapshot.captureVersion != version) return@launch
                frozenLayer.record(image)
            } catch (error: CancellationException) {
                throw error
            } catch (error: IllegalStateException) {
                reporter.reportError(error)
            } finally {
                context.releaseGraphicsLayer(captureLayer)
            }
        }
    }

    fun release(snapshot: StateChangeSnapshot) {
        ++snapshot.captureVersion
        freezeJobs.remove(snapshot.node)?.cancel()
        snapshot.frozen?.layer?.let(::releaseLayer)
        snapshot.frozen = null
    }

    override fun onRemembered() = Unit

    override fun onForgotten() = release()

    override fun onAbandoned() = release()

    private fun release() {
        released = true
        freezeJobs.values.forEach(Job::cancel)
        layers.forEach(context::releaseGraphicsLayer)
        layers.clear()
        freezeJobs.clear()
    }
}
