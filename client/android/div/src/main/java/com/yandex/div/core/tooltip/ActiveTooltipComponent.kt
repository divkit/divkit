package com.yandex.div.core.tooltip

import android.os.Handler
import android.view.View
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor

internal class ActiveTooltipComponent(
    val data: TooltipData,
    private val tooltipRestrictor: DivTooltipRestrictor,
    private val divPreloader: DivPreloader,
    viewFactory: DivTooltipViewFactory,
    private val mainThreadHandler: Handler,
    private val onFinished: (ActiveTooltipComponent) -> Unit,
) {

    private var state = State.Pending
    private var isStarted = false
    private var preloadTicket: DivPreloader.Ticket? = null
    private var durationCallback: Runnable? = null

    private val view = viewFactory.create(
        data = data,
        onTouchOutside = ::dismiss,
        onDismissed = ::onPlatformViewDismissed,
    )
    private val updateCoordinator = TooltipUpdateCoordinator(
        events = TooltipUpdateEvents(
            rootView = data.divView.rootView,
            anchor = data.anchor,
            handler = mainThreadHandler,
        ),
        recomputePosition = view::recomputePosition,
    )

    fun start(multiple: Boolean) {
        if (isStarted || state != State.Pending) {
            return
        }

        isStarted = true
        var isPreloadCompleted = false
        val ticket = divPreloader.preload(
            data.divTooltip.div,
            data.tooltipBlock.expressionResolver,
        ) { hasFailures ->
            if (isPreloadCompleted || state != State.Pending) {
                return@preload
            }

            isPreloadCompleted = true
            if (!canShowTooltip(hasFailures, multiple)) {
                dismiss()
                return@preload
            }

            preloadTicket = null
            view.show(::onTooltipShown)
        }

        if (!isPreloadCompleted && state == State.Pending) {
            preloadTicket = ticket
        } else {
            ticket.cancel()
        }
    }

    fun dismiss() {
        when (state) {
            State.Pending -> {
                finishLifetime()
                view.dismiss()
            }

            State.Shown -> {
                state = State.Dismissing
                updateCoordinator.stop()
                cancelLogicalLifetime()
                view.dismiss()
            }

            State.Dismissing, State.Finished -> Unit
        }
    }

    fun dispose() {
        val wasShown = state == State.Shown || state == State.Dismissing
        if (!finishLifetime()) {
            return
        }

        view.dismissImmediately()
        if (wasShown) {
            notifyTooltipDismissed()
        }
    }

    fun onConfigurationChanged() {
        updateCoordinator.onConfigurationChanged()
    }

    fun findViewWithTag(id: String): View? {
        return view.findViewWithTag(id)
    }

    private fun canShowTooltip(hasFailures: Boolean, multiple: Boolean): Boolean {
        if (hasFailures || !data.anchor.isAttachedToWindow) {
            return false
        }

        return tooltipRestrictor.canShowTooltip(
            data.divView,
            data.anchor,
            data.divTooltip,
            multiple,
            data.scopeId,
        )
    }

    private fun onTooltipShown() {
        if (state != State.Pending) {
            return
        }

        state = State.Shown
        updateCoordinator.start()
        tooltipRestrictor.tooltipShownCallback
            ?.onDivTooltipShown(data.divView, data.anchor, data.divTooltip)
        if (state != State.Shown) {
            return
        }

        val duration = data.divTooltip.duration.evaluate(data.anchorResolver)
        if (duration > 0L) {
            val callback = Runnable {
                durationCallback = null
                dismiss()
            }
            durationCallback = callback
            mainThreadHandler.postDelayed(callback, duration)
        }
    }

    private fun onPlatformViewDismissed(dismissedView: DivTooltipView) {
        if (dismissedView !== view) {
            return
        }

        val wasShown = state == State.Shown || state == State.Dismissing
        if (finishLifetime() && wasShown) {
            notifyTooltipDismissed()
        }
    }

    private fun finishLifetime(): Boolean {
        if (state == State.Finished) {
            return false
        }

        state = State.Finished
        updateCoordinator.stop()
        cancelLogicalLifetime()
        onFinished(this)
        return true
    }

    private fun cancelLogicalLifetime() {
        preloadTicket?.cancel()
        preloadTicket = null
        durationCallback?.let(mainThreadHandler::removeCallbacks)
        durationCallback = null
    }

    private fun notifyTooltipDismissed() {
        tooltipRestrictor.tooltipShownCallback
            ?.onDivTooltipDismissed(data.divView, data.anchor, data.divTooltip)
    }

    private enum class State {
        Pending,
        Shown,
        Dismissing,
        Finished,
    }
}
