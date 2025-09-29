package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionView
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Releasable
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div2.Div

internal class DivVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divImageStyle
) : FrameContainerLayout(context, attrs, defStyleAttr),
    DivHolderView<Div.Video> by DivHolderViewMixin(),
    DivExtensionView,
    Releasable,
    MediaReleasable {

    var path: DivStatePath? = null
    private val videoSubscriptions = mutableListOf<Disposable>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        drawBorderClipped(canvas) { super.draw(it) }
    }

    override fun release() {
        super.release()
        releaseMedia()
    }

    override fun releaseMedia() {
        getPlayerView()?.let { playerView ->
            val lastPlayer = playerView.getAttachedPlayer()
            playerView.detach()
            lastPlayer?.release()
        }
        videoSubscriptions.forEach { it.close() }
        videoSubscriptions.clear()
    }

    fun getPlayerView(): DivPlayerView? {
        if (this.childCount > 2) {
            KAssert.fail { "Too many children in DivVideo" }
        }
        this.getChildAt(0)?.let {
            if (it !is DivPlayerView) {
                KAssert.fail { "Wrong view type inside DivVideo" }
                return null
            }
            return it
        }
        return null
    }

    override fun getBaseline() = measuredHeight - paddingBottom

    fun addVideoSubscription(subscription: Disposable?) {
        if (subscription != null && subscription !== Disposable.NULL) {
            videoSubscriptions += subscription
        }
    }
}
