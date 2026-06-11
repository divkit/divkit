package com.yandex.div.compose.images

import android.net.Uri
import coil3.map.Mapper
import coil3.request.Options
import com.yandex.div.core.annotations.InternalApi

@InternalApi
class DivkitAssetUriMapper : Mapper<Uri, String> {

    override fun map(data: Uri, options: Options): String? {
        if (data.scheme != ASSET_SCHEME) return null
        val path = data.toString().removePrefix("$ASSET_SCHEME://")
        return "$ANDROID_ASSET_PREFIX$path"
    }

    private companion object {
        const val ASSET_SCHEME = "divkit-asset"
        const val ANDROID_ASSET_PREFIX = "file:///android_asset/divkit/"
    }
}
