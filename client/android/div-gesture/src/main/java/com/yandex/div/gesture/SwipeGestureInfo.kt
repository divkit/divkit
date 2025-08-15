package com.yandex.div.gesture

/**
 * Info about ongoing gesture.
 */
internal interface SwipeGestureInfo {
    /**
     * @return `true` if current gesture is interpreted as left swipe.
     */
    val isSwipingLeft: Boolean

    /**
     * @return `true` if current gesture is interpreted as right swipe.
     */
    val isSwipingRight: Boolean

    /**
     * @return `true` if current gesture is interpreted as up swipe.
     */
    val isSwipingUp: Boolean

    /**
     * @return `true` if current gesture is interpreted as down swipe.
     */
    val isSwipingDown: Boolean

    /**
     * @return `true` if touch slop was breached.
     */
    val isTouchSlopBreached: Boolean
}
