package com.yandex.div.core.action

import android.net.Uri
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div2.DivActionDefault
import com.yandex.div2.DivDownloadCallbacks
import org.json.JSONObject

/**
 * Model for default Div2 UI-events
 */
@PublicApi
data class DivActionInfo(
    @JvmField val downloadCallbacks: DivDownloadCallbacks? = null,
    @JvmField val logId: String,
    @JvmField val logUrl: Uri? = null,
    @JvmField val menuItems: List<DivActionDefault.MenuItem>? = null,
    @JvmField val payload: JSONObject? = null,
    @JvmField val referer: Uri? = null,
    @JvmField val target: DivActionDefault.Target? = null,
    @JvmField val url: Uri? = null,
)
