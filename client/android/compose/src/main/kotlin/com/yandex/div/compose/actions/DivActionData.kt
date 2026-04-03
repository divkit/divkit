package com.yandex.div.compose.actions

import android.net.Uri
import com.yandex.div.core.annotations.PublicApi
import org.json.JSONObject

/**
 * Data associated with a DivKit action.
 */
@PublicApi
data class DivActionData(
    val id: String,
    val payload: JSONObject?,
    val url: Uri?
)
