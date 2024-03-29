package com.yandex.div.core.image

private const val ASSET_SCHEME = "divkit-asset"

internal class DivImageAssetUrlModifier : DivImageUrlModifier {
    override fun modifyImageUrl(imageUrl: String): String =
        if (imageUrl.startsWith(ASSET_SCHEME)) {
            "file:///android_asset/divkit/${imageUrl.removePrefix("${ASSET_SCHEME}://")}"
        } else imageUrl
}
