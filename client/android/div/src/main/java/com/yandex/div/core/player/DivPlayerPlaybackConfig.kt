package com.yandex.div.core.player

import org.json.JSONObject

/**
 * Initial player parameters.
 */
data class DivPlayerPlaybackConfig(
    val autoplay: Boolean = false,
    val isMuted: Boolean = false,
    val repeatable: Boolean = false,
    val payload: JSONObject? = null,
)
