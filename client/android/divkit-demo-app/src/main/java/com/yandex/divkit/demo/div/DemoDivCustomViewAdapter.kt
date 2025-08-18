package com.yandex.divkit.demo.div

import android.content.Context
import android.util.Log
import android.view.View
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.rive.OkHttpDivRiveNetworkDelegate
import com.yandex.div.rive.RiveCustomViewAdapter
import com.yandex.div.video.custom.VideoCustomAdapter
import com.yandex.div.video.custom.VideoCustomViewController
import com.yandex.div2.DivCustom
import okhttp3.OkHttpClient

private const val TAG = "DivCustomViewAdapter"

class DemoDivCustomViewAdapter(
    baseContext: Context,
    videoCustomViewController: VideoCustomViewController
): DivCustomContainerViewAdapter {

    private val demoCustomContainerAdapter: DemoCustomContainerAdapter = DemoCustomContainerAdapter()
    private val videoCustomAdapter = VideoCustomAdapter(videoCustomViewController)
    private val divRiveAdapter =
        RiveCustomViewAdapter.Builder(baseContext, OkHttpDivRiveNetworkDelegate(OkHttpClient.Builder().build())).build()
    private val adapters = listOf(videoCustomAdapter, divRiveAdapter, demoCustomContainerAdapter)
    override fun preload(
        div: DivCustom,
        callBack: DivPreloader.Callback
    ): DivPreloader.PreloadReference {
        for (adapter in adapters) {
            if (adapter.isCustomTypeSupported(div.customType)) {
                return adapter.preload(div, callBack)
            }
        }
        return DivPreloader.PreloadReference.EMPTY
    }

    override fun createView(
        div: DivCustom,
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        path: DivStatePath
    ): View {
        for (adapter in adapters) {
            if (adapter.isCustomTypeSupported(div.customType)) {
                return adapter.createView(div, divView, expressionResolver, path)
            }
        }
        return demoCustomContainerAdapter.createView(div, divView, expressionResolver, path)
    }

    override fun bindView(
        view: View,
        div: DivCustom,
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        path: DivStatePath
    ) {
        Log.d(TAG, "binding view ${div.id} of type ${div.customType}")
        dispatch(div) { bindView(view, div, divView, expressionResolver, path) }
    }

    override fun isCustomTypeSupported(type: String): Boolean = adapters.any { it.isCustomTypeSupported(type) }

    override fun release(view: View, div: DivCustom) {
        dispatch(div) { release(view, div) }
        Log.d(TAG, "custom view released!")
    }

    private inline fun dispatch(div: DivCustom, block: DivCustomContainerViewAdapter.() -> Unit) {
        for (adapter in adapters) {
            if (adapter.isCustomTypeSupported(div.customType)) {
                block(adapter)
                break
            }
        }
    }
}
