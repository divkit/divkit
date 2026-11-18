package com.yandex.div.compose.images

import android.net.Uri
import coil3.map.Mapper
import coil3.request.Options

internal class DivkitAssetUriMapper : Mapper<Uri, String> {

    override fun map(data: Uri, options: Options): String? {
        if (data.scheme != ASSET_SCHEME) return null
        val path = data.toString().removePrefix("$ASSET_SCHEME://")
        return "file:///android_asset/divkit/$path"
    }
}

private const val ASSET_SCHEME = "divkit-asset"
