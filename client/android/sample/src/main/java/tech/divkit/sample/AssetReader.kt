package tech.divkit.sample

import android.content.Context
import com.yandex.div.core.utils.IOUtils
import org.json.JSONObject

class AssetReader(private val context: Context) {

    fun read(filename: String): JSONObject {
        val data = IOUtils.toString(context.assets.open(filename))
        return JSONObject(data)
    }
}
