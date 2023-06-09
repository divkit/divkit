package com.yandex.div.core.view2.divs.widgets

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.AttributeSet
import android.widget.FrameLayout
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionView
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.view2.divs.updateBorderDrawer
import com.yandex.div.core.widget.invalidateAfter
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder
import com.yandex.div2.DivVideo

internal class DivVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.divImageStyle
) : FrameLayout(context, attrs, defStyleAttr),
    DivBorderSupports,
    TransientView,
    DivExtensionView,
    ExpressionSubscriber {
    internal var div: DivVideo? = null

    private var borderDrawer: DivBorderDrawer? = null
    override val border: DivBorder?
        get() = borderDrawer?.border

    override fun getDivBorderDrawer() = borderDrawer

    override var isTransient = false
        set(value) = invalidateAfter {
            field = value
        }

    override val subscriptions = mutableListOf<Disposable>()
    private var lastPlayer: DivPlayer? = null

    private var isDrawing = false

    private val activityCallback = object : ActivityLifecycleCallbacks {
        override fun onActivityStarted(activity: Activity) {
            if (getPlayerView()?.getAttachedPlayer() == null) {
                lastPlayer?.let {
                    getPlayerView()?.attach(it)
                }
            }
            lastPlayer = null
        }

        override fun onActivityStopped(activity: Activity) {
            getPlayerView()?.let {
                lastPlayer = it.getAttachedPlayer()
                it.detach()
            }
        }

        override fun onActivityPreDestroyed(activity: Activity) {
            release()
            (context.applicationContext as Application).unregisterActivityLifecycleCallbacks(this)
            super.onActivityPreDestroyed(activity)
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        override fun onActivityResumed(activity: Activity) = Unit
        override fun onActivityPaused(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit
    }

    init {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(activityCallback)
    }

    override fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        borderDrawer = updateBorderDrawer(border, resolver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderDrawer?.onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        isDrawing = true
        borderDrawer.drawClipped(canvas) { super.draw(canvas) }
        isDrawing = false
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (isDrawing) {
            super.dispatchDraw(canvas)
        } else {
            borderDrawer.drawClipped(canvas) { super.dispatchDraw(canvas) }
        }
    }

    override fun release() {
        super.release()
        getPlayerView()?.let {
            it.getAttachedPlayer()?.release()
            it.detach()
        }
        lastPlayer = null
        borderDrawer?.release()
    }

    fun getPlayerView(): DivPlayerView? {
        if (this.childCount > 1) {
            KAssert.fail { "More than one player view inside DivVideo" }
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
}
