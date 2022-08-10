package com.yandex.div.view

import android.view.MotionEvent
import android.view.ViewGroup

/**
 * Interface definition for a callback that allows the application to intercept
 * touch events in progress at the view hierarchy level of target [ViewGroup]
 * before those touch events are considered for target's own behavior.
 */
interface OnInterceptTouchEventListener {

    /**
     * Silently observe and/or take over touch events sent to the target
     * before they are handled by either the target itself or its child views.
     *
     *
     * Implement this method to intercept all touch screen motion events.  This
     * allows you to watch events as they are dispatched to your children, and
     * take ownership of the current gesture at any point.
     *
     * @param target The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *        the event.
     *
     * @return True if this OnInterceptTouchEventListener wishes to begin intercepting touch events,
     *         false to continue with the current behavior and continue observing future events in
     *         the gesture.
     */
    fun onInterceptTouchEvent(target: ViewGroup, event: MotionEvent): Boolean
}
