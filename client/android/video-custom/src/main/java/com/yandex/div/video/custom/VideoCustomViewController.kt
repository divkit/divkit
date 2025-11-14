package com.yandex.div.video.custom

import android.content.Context
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

private const val TAG = "VideoCustomViewController"

@Deprecated("Use div.video.m3 package")
class VideoCustomViewController(
    private val cache: VideoCache,
    private val coroutineScope: CoroutineScope,
    private val context: Context,
) {
    private val viewModels = mutableListOf<MutableVideoViewModel>()
    private val actionNotifier = VideoCustomActionNotifier()

    internal fun bind(view: VideoView, config: VideoConfig) {
        if (!isValid(config)) {
            KAssert.fail { "Video config is not valid: $config" }
        }
        val viewModel = provideViewModel(config)
        view.bindToViewModel(viewModel)
    }

    internal fun unbind(viewModel: VideoViewModel) {
        viewModels.remove(viewModel)
        viewModel.release()
    }

    internal fun release(view: VideoView) {
        val viewModel = viewModels.find { it == view.viewModel }
        if (viewModel != null) {
            viewModel.release()
            viewModels.remove(viewModel)
        }
        view.release()
    }

    private suspend fun preload(config: VideoConfig): Boolean {
        // Warm up view model
        provideViewModel(config)

        val stubImagePreloadJob = coroutineScope.async {
            if (config.stubImageUrl == null) return@async
            cache.cacheStubImage(config.stubImageUrl)
        }

        val videoPreloadJob = coroutineScope.async {
            cache.cacheVideo(config.dataSpec)
        }

        return try {
            awaitAll(stubImagePreloadJob, videoPreloadJob)
            true
        } catch (e: VideoCachingException) {
            KLog.d(TAG, e) { "Preloading failed for video with config=$config" }
            false
        }
    }

    internal fun preload(
        config: VideoConfig,
        callback: (hasErrors: Boolean) -> Unit
    ): Job = coroutineScope.launch {
        val hasErrors = !preload(config)
        callback(hasErrors)
    }

    fun addActionListener(listener: VideoCustomActionListener) {
        actionNotifier.addListener(listener)
    }

    fun removeActionListener(listener: VideoCustomActionListener) {
        actionNotifier.removeListener(listener)
    }

    fun playVideo(id: String) {
        findViewModelForId(id)?.play()
    }

    fun pauseVideo(id: String) {
        findViewModelForId(id)?.pause()
    }

    fun resetVideo(id: String) {
        findViewModelForId(id)?.reset()
    }

    private fun provideViewModel(config: VideoConfig): MutableVideoViewModel {
        val viewModel = viewModels.find { viewModel -> viewModel.videoId == config.id }
        return viewModel ?: MutableVideoViewModel(
            config,
            cache,
            actionNotifier,
            this@VideoCustomViewController,
            context
        ).also { viewModels.add(it) }
    }

    private fun findViewModelForId(id: String): MutableVideoViewModel? {
        val viewModel = viewModels.find { viewModel -> viewModel.videoId == id }
        if (viewModel == null) {
            KAssert.fail { "VideoViewModel was not found for video with id=$id" }
        }
        return viewModel
    }
}
