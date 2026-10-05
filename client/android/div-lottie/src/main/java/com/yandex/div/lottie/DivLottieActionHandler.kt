package com.yandex.div.lottie

import android.view.View
import com.yandex.div.DivDataTag
import com.yandex.div.core.expression.evaluation.DictEvaluator
import com.yandex.div.core.extension.DivExtensionActionHandler
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import java.util.WeakHashMap

internal class DivLottieActionHandler : DivExtensionActionHandler {

    private val playbackByTag = WeakHashMap<DivDataTag, LottiePlaybackStateController>()

    override fun onViewBind(divView: Div2View, path: DivStatePath, view: View) {
        val playbackController = view.getTag(R.id.lottie_playback_controller) as? LottiePlaybackController
        playbackController?.onBind(stateFor(divView), path)
    }

    override fun onViewUnbind(divView: Div2View, path: DivStatePath, view: View) {
        val playbackController = view.getTag(R.id.lottie_playback_controller) as? LottiePlaybackController
        playbackController?.onUnbind()
    }

    override fun handleAction(
        divView: Div2View,
        path: DivStatePath,
        payload: DictEvaluator?,
        resolver: ExpressionResolver,
    ): Boolean {
        when (payload?.get()?.getOrNull()?.get("type")) {
            "start" -> stateFor(divView).start(path)
            "stop" -> stateFor(divView).stop(path)
            else -> return false
        }
        return true
    }

    @Synchronized
    private fun stateFor(divView: Div2View): LottiePlaybackStateController =
        playbackByTag.getOrPut(divView.dataTag) { LottiePlaybackStateController() }
}
