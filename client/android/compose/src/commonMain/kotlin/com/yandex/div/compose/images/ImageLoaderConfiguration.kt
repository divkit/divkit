package com.yandex.div.compose.images

import coil3.ComponentRegistry
import coil3.EventListener

/**
 * Customizes DivKit's Coil [coil3.ImageLoader].
 *
 * @see com.yandex.div.compose.DivConfiguration
 */
public interface ImageLoaderConfiguration {

    /**
     * Registers additional Coil components on [builder].
     * Built-in DivKit components are already added, except the network fetcher: it is added
     * afterwards, so a fetcher registered here takes precedence over it.
     */
    public fun applyComponents(builder: ComponentRegistry.Builder): Unit = Unit

    /**
     * Registers additional Coil event listeners.
     */
    public val eventListener: EventListener
        get() = EventListener.NONE

    /**
     * Enables or disables image loading errors reporting via [com.yandex.div.compose.DivReporter].
     */
    public val reportErrors: Boolean
        get() = true
}
