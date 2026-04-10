package com.yandex.div.compose.actions

import com.yandex.div.core.annotations.ExperimentalApi
import org.json.JSONObject

/**
 * Data associated with a custom  DivKit action (action with `"type": "custom"`).
 */
@ExperimentalApi
data class DivCustomActionData internal constructor(
    val id: String,
    val payload: JSONObject?,
    val source: DivActionSource
)
