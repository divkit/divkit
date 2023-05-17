package com.yandex.div.rive

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import app.rive.runtime.kotlin.core.Rive
import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivCustom
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val CUSTOM_RIVE_ANIMATION_TYPE = "rive_animation"

class RiveCustomViewAdapter private constructor(
    context: Context,
    maxCacheSize: Int,
    networkDelegate: DivRiveNetworkDelegate,
    private val loadExecutor: ExecutorService,
    private val logger: DivRiveLogger,
    localResProvider: DivRiveLocalResProvider
) : DivCustomViewAdapter {
    private val repo = DivRiveRepository(maxCacheSize, context, networkDelegate, localResProvider)
    private val handler = Handler(Looper.getMainLooper())
    private val holderMap = mutableMapOf<DivCustom, RiveAnimationLoadHolder>()

    init {
        Rive.init(context = context)
    }

    override fun preload(div: DivCustom, callBack: DivPreloader.Callback): DivPreloader.PreloadReference {
        val loader = getLoader(div)
        loader.preload(callBack)
        return DivPreloader.PreloadReference { loader.cancel() }
    }

    override fun createView(div: DivCustom, divView: Div2View): View {
        return DivRiveContainer(divView.context)
    }

    override fun bindView(view: View, div: DivCustom, divView: Div2View) {
        if (view is DivRiveContainer) {
            getLoader(div).setView(view)
        }
    }

    private fun getLoader(div: DivCustom): RiveAnimationLoadHolder {
        return holderMap[div]
            ?: RiveAnimationLoadHolder(repo, loadExecutor, handler, div.riveAnimationProps, logger)
                .also {
                    holderMap[div] = it
                }
    }

    override fun isCustomTypeSupported(type: String) = type == CUSTOM_RIVE_ANIMATION_TYPE

    override fun release(view: View, div: DivCustom) {
        if (view is DivRiveContainer) {
            holderMap.remove(div)?.cancel()
            view.release()
        }
    }

    class Builder(private val context: Context, private val networkDelegate: DivRiveNetworkDelegate) {
        private var maxCacheSize: Int? = null
        private var loadExecutor: ExecutorService? = null
        private var logger: DivRiveLogger = DivRiveLogger.STUB
        private var localResProvider: DivRiveLocalResProvider = DivRiveLocalResProvider.STUB

        fun maxCacheSize(maxCacheSize: Int): Builder {
            this.maxCacheSize = maxCacheSize
            return this
        }

        fun loadExecutor(loadExecutor: ExecutorService): Builder {
            this.loadExecutor = loadExecutor
            return this
        }

        fun logger(logger: DivRiveLogger): Builder {
            this.logger = logger
            return this
        }

        fun localResProvider(localResProvider: DivRiveLocalResProvider): Builder {
            this.localResProvider = localResProvider
            return this
        }

        fun build(): RiveCustomViewAdapter {
            return RiveCustomViewAdapter(context, maxCacheSize ?: DEFAULT_CACHE_SIZE, networkDelegate,
                loadExecutor ?: createDefaultExecutor(), logger, localResProvider)
        }

        private fun createDefaultExecutor() =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors().coerceIn(2, 4))

        companion object {
            private const val DEFAULT_CACHE_SIZE = 8 * 1024 * 1024
        }
    }
}
