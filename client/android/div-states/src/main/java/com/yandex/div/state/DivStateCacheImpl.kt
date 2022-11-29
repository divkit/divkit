package com.yandex.div.state

import androidx.annotation.AnyThread
import com.yandex.div.internal.Assert
import com.yandex.div.internal.util.CompletedFuture
import com.yandex.div.internal.util.SingleThreadExecutor
import com.yandex.div.state.db.DivStateEntity
import java.util.Collections
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

internal class DivStateCacheImpl(
    private val divStateDatabase: DivStateDatabase,
    executorService: ExecutorService,
) : DivStateCache {

    private val singleThreadExecutor = WorkerThreadExecutor(executorService)

    private val cache = Collections.synchronizedMap(mutableMapOf<String, Future<MutableMap<String, String>>>())
    private val rootState = Collections.synchronizedMap(mutableMapOf<String, Future<String?>>())

    @AnyThread
    fun putState(cardId: String, future: Future<MutableMap<String, String>>) {
        synchronized(cache) {
            if (!cache.containsKey(cardId)) {
                cache[cardId] = future
            }
        }
    }

    @AnyThread
    fun putRootState(cardId: String, future: Future<String?>) {
        synchronized(rootState) {
            if (!rootState.containsKey(cardId)) {
                rootState[cardId] = future
            }
        }
    }

    @AnyThread
    override fun putState(cardId: String, path: String, stateId: String) {
        synchronized(cache) {
            cache.getOrPut(cardId) {
                CompletedFuture(mutableMapOf())
            }.get()[path] = stateId

            singleThreadExecutor.post {
                divStateDatabase.divStateDao.updateState(
                    DivStateEntity(
                        id = 0,
                        cardId = cardId,
                        path = path,
                        stateId = stateId,
                        modificationTime = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    @AnyThread
    override fun putRootState(cardId: String, stateId: String) {
        synchronized(rootState) {
            rootState[cardId] = CompletedFuture(stateId)
            singleThreadExecutor.post {
                divStateDatabase.divStateDao.deleteCardRootState(cardId)
                divStateDatabase.divStateDao.updateState(
                    DivStateEntity(
                        id = 0,
                        cardId = cardId,
                        path = "/",
                        stateId = stateId,
                        modificationTime = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    @AnyThread
    override fun getState(cardId: String, path: String): String? {
        try {
            val future = cache[cardId]
            if (future?.isDone == true) {
                return future.get()?.get(path)
            }
        } catch (e: ExecutionException) {
            Assert.fail("", e)
        }

        return null
    }

    @AnyThread
    override fun getRootState(cardId: String): String? {
        try {
            synchronized(rootState) {
                val future = rootState[cardId]
                if (future?.isDone == true) {
                    val result = future.get()
                    if (result == null) {
                        rootState[cardId] = null
                    }
                    return result
                }
            }
        } catch (e: ExecutionException) {
            Assert.fail("", e)
        }

        return null
    }

    @AnyThread
    fun clear() {
        cache.clear()
        rootState.clear()
    }

    private class WorkerThreadExecutor(
        executor: Executor
    ) : SingleThreadExecutor(executor, "DivStateCache") {

        override fun handleError(e: RuntimeException) {
            Assert.fail("", e)
        }
    }
}
