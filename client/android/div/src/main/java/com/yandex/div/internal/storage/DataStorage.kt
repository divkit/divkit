package com.yandex.div.internal.storage

import android.content.Context
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

internal class DataStorage<T> private constructor(
    context: Context,
    fileName: String,
    private val coroutineScope: CoroutineScope,
    private val editor: DataEditor<T>,
) {

    private val coroutineContext = coroutineScope.coroutineContext
    private val dataFile = File(context.applicationContext.filesDir, fileName)
    private val mutex = Mutex()

    @Suppress("UNCHECKED_CAST")
    private val dataStateFlow = MutableStateFlow<DataState<T>>(
        value = DataState.Initial as DataState<T>
    )

    val data: StateFlow<T?> = dataStateFlow.onStart { restoreState() }
        .map { state -> evaluateState(state) }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), null)

    init {
        dataStateFlow.onSubscription {  }
        coroutineContext[Job]?.invokeOnCompletion {
            @Suppress("UNCHECKED_CAST")
            dataStateFlow.value = DataState.Finalized as DataState<T>
        }
    }

    private fun evaluateState(state: DataState<T>): T? {
        return when (state) {
            is DataState.Initial -> null
            is DataState.WithData<T> -> state.value
            is DataState.WithException<T> -> null
            is DataState.Finalized -> error("Cannot read from closed storage")
        }
    }

    private suspend fun restoreState() {
        return withContext(coroutineContext) {
            mutex.withLock {
                if (dataStateFlow.value is DataState.Initial) {
                    try {
                        val value = editor.read(dataFile.inputStream())
                        dataStateFlow.value = DataState.WithData(value)
                    } catch (e: Exception) {
                        dataStateFlow.value = DataState.WithException(e)
                    }
                }
            }
        }
    }

    suspend fun update(value: T) {
        withContext(coroutineContext) {
            mutex.withLock {
                try {
                    if (!dataFile.exists()) {
                        dataFile.parentFile?.mkdirs()
                        dataFile.createNewFile()
                    }

                    val outputStream = dataFile.outputStream()
                    outputStream.use { output ->
                        editor.write(value, output)
                    }

                    dataStateFlow.value = DataState.WithData(value)
                } catch (e: Exception) {
                    dataStateFlow.value = DataState.WithException(e)
                }
            }
        }
    }

    suspend fun clear(): Boolean {
        return withContext(coroutineContext) {
            mutex.withLock {
                try {
                    val result = dataFile.delete()
                    @Suppress("UNCHECKED_CAST")
                    dataStateFlow.value = DataState.Initial as DataState<T>
                    result
                } catch (e: Exception) {
                    dataStateFlow.value = DataState.WithException(e)
                    false
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun <T> create(
            context: Context,
            fileName: String,
            editor: DataEditor<T>
        ): DataStorage<T> {
            return DataStorage(
                context = context,
                fileName = fileName,
                coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                editor = editor
            )
        }

        @JvmStatic
        @VisibleForTesting
        internal fun <T> create(
            context: Context,
            fileName: String,
            editor: DataEditor<T>,
            coroutineScope: CoroutineScope
        ): DataStorage<T> {
            return DataStorage(
                context = context,
                fileName = fileName,
                coroutineScope = coroutineScope,
                editor = editor
            )
        }
    }
}
