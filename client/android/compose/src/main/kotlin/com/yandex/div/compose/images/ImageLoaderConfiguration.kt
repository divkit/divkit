package com.yandex.div.compose.images

import coil3.ComponentRegistry
import coil3.EventListener
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Customizes DivKit's Coil [coil3.ImageLoader].
 *
 * @see com.yandex.div.compose.DivComposeConfiguration
 */
@ExperimentalApi
interface ImageLoaderConfiguration {

    /**
     * Registers additional Coil components on [builder].
     * Built-in DivKit components are already added.
     */
    fun applyComponents(builder: ComponentRegistry.Builder) = Unit

    /**
     * Registers additional Coil event listeners.
     */
    val eventListener: EventListener
        get() = EventListener.NONE
}
