package com.yandex.div.view

/**
 * Interface definition for [android.view.ViewGroup] that holds and notifies
 * an instance of [OnInterceptTouchEventListener]
 */
interface OnInterceptTouchEventListenerHost {

    /**
     * Listener itself.
     */
    var onInterceptTouchEventListener: OnInterceptTouchEventListener?
}
