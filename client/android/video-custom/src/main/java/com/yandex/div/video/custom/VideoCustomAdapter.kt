package com.yandex.div.video.custom

import android.view.View
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KAssert
import com.yandex.div2.DivCustom

private const val CUSTOM_VIDEO_TYPE = "custom_video"

class VideoCustomAdapter(
    private val videoCustomViewController: VideoCustomViewController
) : DivCustomContainerViewAdapter {

    override fun preload(
        div: DivCustom,
        callBack: DivPreloader.Callback
    ): DivPreloader.PreloadReference {
        val preloadJob = videoCustomViewController.preload(div.videoConfig) { hasErrors ->
            callBack.finish(hasErrors)
        }

        return DivPreloader.PreloadReference { preloadJob.cancel() }
    }

    override fun createView(div: DivCustom, divView: Div2View, path: DivStatePath): View {
        return VideoView(divView.context, div.videoConfig.zOrderMode, videoCustomViewController)
    }

    override fun bindView(view: View, div: DivCustom, divView: Div2View, path: DivStatePath) {
        if (view is VideoView) {
            videoCustomViewController.bind(view, div.videoConfig)
        } else {
            KAssert.fail { "Wrong view type" }
        }
    }

    override fun isCustomTypeSupported(type: String) = type == CUSTOM_VIDEO_TYPE

    override fun release(view: View, div: DivCustom) {
        if (view is VideoView) {
            videoCustomViewController.release(view)
        } else {
            KAssert.fail { "Wrong view type" }
        }
    }
}
