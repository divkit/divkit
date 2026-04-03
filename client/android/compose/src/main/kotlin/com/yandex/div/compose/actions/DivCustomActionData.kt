package com.yandex.div.compose.actions

import com.yandex.div.core.annotations.PublicApi
import org.json.JSONObject

/**
 * Data associated with a custom  DivKit action (action with `"type": "custom"`).
 */
@PublicApi
data class DivCustomActionData(
    val id: String,
    val payload: JSONObject?
)
