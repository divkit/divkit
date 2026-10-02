package com.yandex.div.compose.actions

import android.net.Uri
import org.json.JSONObject

/**
 * Data associated with a DivKit action.
 */
@ConsistentCopyVisibility
data class DivActionData internal constructor(
    val id: String?,
    val payload: JSONObject?,
    val source: DivActionSource,
    val url: Uri?,
    @Deprecated("Use custom actions or payload to pass application-specific parameters.")
    val logUrl: Uri? = null,
    @Deprecated("Use custom action or payload to pass application-specific parameters.")
    val referer: Uri? = null,
)
