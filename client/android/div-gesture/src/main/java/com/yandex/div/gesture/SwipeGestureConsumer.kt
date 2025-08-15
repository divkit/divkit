package com.yandex.div.gesture

/**
 * Represents swipe gesture lifecycle callbacks.
 */
internal interface SwipeGestureConsumer {
    /**
     * Called on touch down event.
     *
     * @return `true` if event is consumed, `false` otherwise.
     */
    fun onTouchStart(info: SwipeGestureInfo): Boolean

    /**
     * Called on move events.
     *
     * @return `true` if event is consumed, `false` otherwise.
     */
    fun onSwipe(info: SwipeGestureInfo): Boolean

    /**
     * Called on cancel or up touch events.
     *
     * @return `true` if event is consumed, `false` otherwise.
     */
    fun onTouchEnd(info: SwipeGestureInfo): Boolean
}
