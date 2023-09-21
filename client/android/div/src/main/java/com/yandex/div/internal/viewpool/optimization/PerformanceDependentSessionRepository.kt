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

internal typealias PerformanceDependentSessionList = MutableList<PerformanceDependentSession>

internal class PerformanceDependentSessionRepository(
    context: Context,
    constraints: ViewPreCreationProfile
) {
    private val store: DataStore<PerformanceDependentSessionList> =
        context.getStoreForId(constraints.id)

    fun get(): PerformanceDependentSessionList = runCatching {
        runBlocking { store.data.first() }
    }
        .onFailure { KLog.e(TAG, it) }
        .getOrElse { ListSerializer.defaultValue }


    fun save(session: PerformanceDependentSession): Boolean = runCatching {
        runBlocking {
            store.updateData {
                it.toMutableList()
                    .apply { add(session) }
                    .takeLast(CAPACITY)
                    .toMutableList()
            }
        }
    }
        .onFailure { KLog.e(TAG, it) }
        .isSuccess


    @OptIn(ExperimentalSerializationApi::class)
    private object ListSerializer : Serializer<PerformanceDependentSessionList> {
        override val defaultValue: PerformanceDependentSessionList
            get() = mutableListOf()

        override suspend fun readFrom(input: InputStream) = runCatching {
            Json.decodeFromStream<PerformanceDependentSessionList>(input)
        }.onFailure { KLog.e(TAG, it) }.getOrNull() ?: defaultValue

        override suspend fun writeTo(t: PerformanceDependentSessionList, output: OutputStream) {
            runCatching {
                Json.encodeToStream(t, output)
            }.onFailure { KLog.e(TAG, it) }
        }
    }

    private companion object {
        const val TAG = "PerformanceDependentSessionRepository"
        const val CAPACITY = 10
        const val STORE_PATH = "divkit_viewpool_sessions_%s.json"

        val stores = WeakHashMap<String?, DataStore<PerformanceDependentSessionList>>()

        fun Context.getStoreForId(id: String?): DataStore<PerformanceDependentSessionList> =
            stores.getOrPut(id) {
                DataStoreFactory.create(
                    serializer = ListSerializer,
                    produceFile = { File(cacheDir, STORE_PATH.format(id)) },
                )
            }
    }
}