package com.yandex.div.compose.observer.events

/**
 * Navigation direction through gallery items or pager pages, independent of layout direction.
 * A cyclic pager reports the direction of the transition, including when it wraps.
 */
public enum class DivScrollDirection {
    /**
     * Movement forward through the item order.
     */
    FORWARD,

    /**
     * Movement backward through the item order.
     */
    BACKWARD,
}
