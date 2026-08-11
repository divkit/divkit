package com.yandex.div.compose.actions

import org.json.JSONObject

/**
 * Data associated with a custom  DivKit action (action with `"type": "custom"`).
 */
@ConsistentCopyVisibility
data class DivCustomActionData internal constructor(
    val id: String?,
    val payload: JSONObject?,
    val source: DivActionSource
)
