package com.yandex.div.compose.actions

import android.net.Uri
import com.yandex.div.core.annotations.ExperimentalApi
import org.json.JSONObject

/**
 * Data associated with a DivKit action.
 */
@ExperimentalApi
data class DivActionData internal constructor(
    val id: String,
    val payload: JSONObject?,
    val source: DivActionSource,
    val url: Uri?
)
