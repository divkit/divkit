package com.yandex.div.compose.actions

import android.net.Uri
import com.yandex.div.core.annotations.PublicApi
import org.json.JSONObject

@PublicApi
class DivActionData(
    val payload: JSONObject?,
    val url: Uri?
)
