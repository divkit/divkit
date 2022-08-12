package com.yandex.div.core.view2

import com.yandex.div.core.annotations.PublicApi

/**
 * Implement this interface if you want to receive release view events.
 * Please consider using this interface.
 */
@PublicApi
interface Releasable {

    /**
     * Called once the view not gonna used by divkit. Now you can release resources or
     * reuse view. Be careful, released view can still has parent, so detach it before reusing.
     */
    fun release()
}
