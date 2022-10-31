package com.yandex.divkit.demo.div

import android.util.Log
import android.view.View
import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivPreloader
import com.yandex.div.video.custom.VideoCustomAdapter
import com.yandex.div.video.custom.VideoCustomViewController
import com.yandex.div2.DivCustom

private const val TAG = "DivCustomViewAdapter"

class DemoDivCustomViewAdapter(
    videoCustomViewController: VideoCustomViewController
): DivCustomViewAdapter {

    private val demoCustomContainerAdapter: DemoCustomContainerAdapter = DemoCustomContainerAdapter()
    private val videoCustomAdapter = VideoCustomAdapter(videoCustomViewController)

    override fun preload(
        div: DivCustom,
        callBack: DivPreloader.Callback
    ): DivPreloader.PreloadReference {
        return if (videoCustomAdapter.isCustomTypeSupported(div.customType))
            videoCustomAdapter.preload(div, callBack)
        else
            DivPreloader.PreloadReference.EMPTY
    }

    override fun createView(div: DivCustom, divView: Div2View): View {
        return if (videoCustomAdapter.isCustomTypeSupported(div.customType))
            videoCustomAdapter.createView(div, divView)
        else
            demoCustomContainerAdapter.createView(div, divView)
    }

    override fun bindView(view: View, div: DivCustom, divView: Div2View) {
        Log.d(TAG, "binding view ${div.id} of type ${div.customType}")
        if (demoCustomContainerAdapter.isCustomTypeSupported(div.customType)) {
            demoCustomContainerAdapter.bindView(view, div, divView)
        } else {
            videoCustomAdapter.bindView(view, div, divView)
        }
    }

    override fun isCustomTypeSupported(type: String): Boolean =
        demoCustomContainerAdapter.isCustomTypeSupported(type) ||
        videoCustomAdapter.isCustomTypeSupported(type)

    override fun release(view: View, div: DivCustom) {
        if (videoCustomAdapter.isCustomTypeSupported(div.customType)) {
            videoCustomAdapter.release(view, div)
        }
        Log.d(TAG, "custom view released!")
    }
}
