package com.yandex.div.core.image

import com.yandex.div.core.annotations.InternalApi

private const val ASSET_SCHEME = "divkit-asset"

@InternalApi
const val ASSET_PREFIX = "file:///android_asset/"

internal class DivImageAssetUrlModifier : DivImageUrlModifier {
    override fun modifyImageUrl(imageUrl: String): String =
        if (imageUrl.startsWith(ASSET_SCHEME)) {
            "${ASSET_PREFIX}divkit/${imageUrl.removePrefix("${ASSET_SCHEME}://")}"
        } else imageUrl
}
