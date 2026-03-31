package com.yandex.div.compose.actions

import android.net.Uri
import com.yandex.div.core.annotations.PublicApi
import org.json.JSONObject

/**
 * Data associated with a custom DivKit action.
 */
@PublicApi
class DivActionData(
    val payload: JSONObject?,
    val url: Uri?
)
