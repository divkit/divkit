package com.yandex.div.internal.viewpool.optimization

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.yandex.div.internal.KLog
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.WeakHashMap

internal class OptimizedViewPreCreationProfileRepository(
    context: Context,
    private val constraints: ViewPreCreationProfile
) {
    private val store = context.getStoreForId(constraints.id)

    fun get(): ViewPreCreationProfile = runCatching {
        runBlocking { store.data.first() }
    }
        .onFailure { KLog.e(TAG, it) }
        .getOrNull() ?: constraints


    fun save(optimizedProfile: ViewPreCreationProfile): Boolean = runCatching {
        runBlocking { store.updateData { optimizedProfile } }
    }
        .onFailure { KLog.e(TAG, it) }
        .isSuccess


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

        val stores = WeakHashMap<String?, DataStore<ViewPreCreationProfile?>>()

        fun Context.getStoreForId(id: String?): DataStore<ViewPreCreationProfile?> =
            stores.getOrPut(id) {
                DataStoreFactory.create(
                    serializer = ViewPreCreationProfileSerializer,
                    produceFile = { File(cacheDir, STORE_PATH.format(id)) },
                )
            }
    }
}
