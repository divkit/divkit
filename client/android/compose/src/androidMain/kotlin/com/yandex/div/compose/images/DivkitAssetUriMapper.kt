package com.yandex.div.compose.images

import android.net.Uri
import coil3.map.Mapper
import coil3.request.Options
import coil3.Uri as CoilUri

internal class DivkitAssetUriMapper : Mapper<Any, String> {

    override fun map(data: Any, options: Options): String? {
        val scheme = when (data) {
            is Uri -> data.scheme
            is CoilUri -> data.scheme
            else -> return null
        }
        if (scheme != ASSET_SCHEME) return null
        val path = data.toString().removePrefix("$ASSET_SCHEME://")
        return "file:///android_asset/divkit/$path"
    }
}

private const val ASSET_SCHEME = "divkit-asset"
