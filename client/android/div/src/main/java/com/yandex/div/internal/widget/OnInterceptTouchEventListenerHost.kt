package com.yandex.div.internal.widget

/**
 * Interface definition for [android.view.ViewGroup] that holds and notifies
 * an instance of [OnInterceptTouchEventListener]
 */
internal interface OnInterceptTouchEventListenerHost {

    /**
     * Listener itself.
     */
    var onInterceptTouchEventListener: OnInterceptTouchEventListener?
}
