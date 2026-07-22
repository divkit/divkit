package com.yandex.div.core.view2.divs

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.hypot

/**
 * When a view is [View.isFocusableInTouchMode], Android takes focus on the first tap's
 * [MotionEvent.ACTION_UP] and skips the click. This listener synthesizes [View.performClick]
 * for a real tap (within touch slop and under long-press timeout when long-clickable) so
 * focus and click actions both run on one user tap.
 *
 * Touch-slop / CANCEL tracking avoids synthesizing a click for scroll or cancelled gestures.
 * For long-press: synthesis is skipped only when the view is [View.isLongClickable] and the
 * hold reached the long-press timeout (platform would fire long-click instead of click).
 *
 * Trade-off: if an `on_focus` action synchronously moves focus away before the posted
 * runnable runs (e.g. `focus_element`), [View.isFocused] is false and tap actions are
 * intentionally not synthesized — avoids double clicks at the cost of dropping that tap.
 */
internal class FocusClickSynthesizer(
    private val touchSlop: Int,
    private val longPressTimeout: Long,
) : (View, MotionEvent) -> Boolean {

    private var downX = 0f
    private var downY = 0f
    private var downTime = 0L
    private var movedBeyondSlop = false

    override fun invoke(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                downTime = event.eventTime
                movedBeyondSlop = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (!movedBeyondSlop) {
                    val dx = event.x - downX
                    val dy = event.y - downY
                    if (hypot(dx.toDouble(), dy.toDouble()) > touchSlop) {
                        movedBeyondSlop = true
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                movedBeyondSlop = true
            }
            MotionEvent.ACTION_UP -> {
                // Focus is taken inside View.onTouchEvent(ACTION_UP) after this listener runs.
                // If this tap will take focus, Android skips click — synthesize it after.
                val willSkipClickForFocus = view.isFocusable
                    && view.isFocusableInTouchMode
                    && !view.isFocused
                val heldPastLongPress = view.isLongClickable
                    && (event.eventTime - downTime) >= longPressTimeout
                val isTap = willSkipClickForFocus
                    && !movedBeyondSlop
                    && !heldPastLongPress
                if (isTap) {
                    // Post so View.onTouchEvent can take focus first. Calling performClick() here
                    // would focus the view early and let View fire a second click.
                    // isFocused: only synthesize when focus was actually taken (click was skipped);
                    // if requestFocus failed, View already handled the click itself.
                    // Also skips when on_focus synchronously moved focus away (see class KDoc).
                    view.post {
                        if (view.isFocused) {
                            view.performClick()
                        }
                    }
                }
            }
        }
        return false
    }

    companion object {
        fun create(target: View): FocusClickSynthesizer? {
            if (!target.isFocusableInTouchMode || !target.isClickable) return null
            return FocusClickSynthesizer(
                touchSlop = ViewConfiguration.get(target.context).scaledTouchSlop,
                longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong(),
            )
        }
    }
}
