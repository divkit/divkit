package com.yandex.div.internal.viewpool.optimization

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names.APP_CONTEXT
import com.yandex.div.internal.KLog
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.WeakHashMap
import javax.inject.Inject
import javax.inject.Named

@DivScope
@Mockable
class ViewPreCreationProfileRepository @Inject constructor(
    @Named(APP_CONTEXT) private val context: Context,
    private val defaultProfile: ViewPreCreationProfile
) {
    suspend fun get(id: String): ViewPreCreationProfile = withContext(Dispatchers.IO) {
        runCatching { context.getStoreForId(id).data.first() }
            .onFailure { KLog.e(TAG, it) }
            .getOrNull() ?: defaultProfile.copy(id = id)
    }

    suspend fun save(profile: ViewPreCreationProfile): Boolean = withContext(Dispatchers.IO) {
        runCatching { context.getStoreForId(profile.id!!).updateData { profile } }
            .onFailure { KLog.e(TAG, it) }
            .isSuccess
    }

    @OptIn(ExperimentalSerializationApi::class)
    private object ViewPreCreationProfileSerializer : Serializer<ViewPreCreationProfile?> {
        private val json = Json { encodeDefaults = false }

        override val defaultValue: ViewPreCreationProfile? = null

        override suspend fun readFrom(input: InputStream) = runCatching {
            json.decodeFromStream<ViewPreCreationProfile?>(input)
        }.onFailure { KLog.e(TAG, it) }.getOrNull()

        override suspend fun writeTo(t: ViewPreCreationProfile?, output: OutputStream) {
            runCatching {
                json.encodeToStream(t, output)
            }.onFailure { KLog.e(TAG, it) }
        }
    }

    private companion object {
        const val TAG = "OptimizedViewPreCreationProfileRepository"
        const val STORE_PATH = "divkit_optimized_viewpool_profile_%s.json"

        val stores = WeakHashMap<String, DataStore<ViewPreCreationProfile?>>()

        fun Context.getStoreForId(id: String): DataStore<ViewPreCreationProfile?> =
            stores.getOrPut(id) {
                DataStoreFactory.create(
                    serializer = ViewPreCreationProfileSerializer,
                    produceFile = { File(filesDir, STORE_PATH.format(id)) }
                )
            }
    }
}
