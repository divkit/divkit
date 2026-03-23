package com.yandex.divkit.demo.screenshot

import android.content.Context
import android.os.Build
import com.yandex.div.internal.util.IOUtils
import org.json.JSONObject

class DivAssetReader(private val context: Context) {

    fun read(filename: String): JSONObject {
        val data = IOUtils.toString(context.assets.open(filename))
            .replaceUnsupportedUnicodeSymbols()
        return JSONObject(data)
    }

    private fun String.replaceUnsupportedUnicodeSymbols(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return this
        var result = this
        UNSUPPORTED_UNICODE_PRE_M.forEachIndexed { index, unsupportedString ->
            result = result.replace(unsupportedString, UNSUPPORTED_UNICODE_REPLACEMENTS[index])
        }
        return result
    }

    companion object {
        private val UNSUPPORTED_UNICODE_PRE_M = arrayOf("⌜", "⌝", "⌞", "⌄", "⌟")
        private val UNSUPPORTED_UNICODE_REPLACEMENTS = arrayOf("tl", "tr", "bl", "b", "br")
    }
}
