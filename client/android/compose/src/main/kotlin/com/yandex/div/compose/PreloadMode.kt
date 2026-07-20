package com.yandex.div.compose

import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Controls how [DivViewHost.setContent] and [DivContext.preload] load resources.
 *
 * - [DISABLED] — do not preload.
 * - [REQUIRED_ONLY] — walk all state variants; load only resources with `preloadRequired = true`.
 * - [ACTIVE_STATE_ONLY] — walk the current/active state tree; load all resources in it.
 * - [ALL] — walk all state variants; load all resources.
 */
@ExperimentalApi
enum class PreloadMode {
    DISABLED,
    REQUIRED_ONLY,
    ACTIVE_STATE_ONLY,
    ALL,
}
