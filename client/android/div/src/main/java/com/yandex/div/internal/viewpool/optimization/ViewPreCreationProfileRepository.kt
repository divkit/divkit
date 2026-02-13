package com.yandex.div.internal.viewpool.optimization

import android.content.Context
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names.APP_CONTEXT
import com.yandex.div.internal.KLog
import com.yandex.div.internal.storage.DataStorage
import com.yandex.div.internal.storage.TextDataEditor
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import com.yandex.div.internal.viewpool.ViewPreCreationProfileParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.WeakHashMap
import javax.inject.Inject
import javax.inject.Named

@DivScope
@Mockable
class ViewPreCreationProfileRepository @Inject constructor(
    @param:Named(APP_CONTEXT) private val context: Context,
    private val defaultProfile: ViewPreCreationProfile
) {

    suspend fun get(id: String): ViewPreCreationProfile = withContext(Dispatchers.IO) {
        runCatching { context.getStorageForId(context, id).data.first() }
            .onFailure { KLog.e(TAG, it) }
            .getOrNull() ?: defaultProfile.copy(id = id)
    }

    suspend fun save(profile: ViewPreCreationProfile): Boolean = withContext(Dispatchers.IO) {
        runCatching { context.getStorageForId(context, profile.id!!).update(profile) }
            .onFailure { KLog.e(TAG, it) }
            .isSuccess
    }

    private object ViewPreCreationProfileEditor : TextDataEditor<ViewPreCreationProfile>() {

        override fun read(input: String): ViewPreCreationProfile {
            return ViewPreCreationProfileParser.deserialize(JSONObject(input))
        }

        override fun write(value: ViewPreCreationProfile): String {
            return ViewPreCreationProfileParser.serialize(value).toString()
        }
    }

    private companion object {
        const val TAG = "OptimizedViewPreCreationProfileRepository"
        const val STORE_PATH = "divkit_optimized_viewpool_profile_%s.json"

        val stores = WeakHashMap<String, DataStorage<ViewPreCreationProfile>>()

        fun Context.getStorageForId(context: Context, id: String): DataStorage<ViewPreCreationProfile> =
            stores.getOrPut(id) {
                DataStorage.create(
                    context = context,
                    fileName = STORE_PATH,
                    editor = ViewPreCreationProfileEditor
                )
            }
    }
}
