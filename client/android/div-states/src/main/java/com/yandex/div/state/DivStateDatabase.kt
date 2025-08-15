package com.yandex.div.state

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import androidx.collection.ArrayMap
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.state.db.DivStateDao
import com.yandex.div.state.db.DivStateDaoImpl
import com.yandex.div.state.db.DivStateDbOpenHelper
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

/**
 * Persistent storage of div states.
 */
@PublicApi
@Mockable
class DivStateDatabase(
    context: Context,
    databaseName: String,
    private val executorService: ExecutorService,
) : DivStateStorage {

    private val cacheImpl = DivStateCacheImpl(this, executorService)
    internal val divStateDao: DivStateDao by lazy {
        val dbOpenHelper = DivStateDbOpenHelper(context, databaseName)
        DivStateDaoImpl(dbOpenHelper.writableDatabase).apply {
            deleteModifiedBefore(System.currentTimeMillis() - STATE_MAX_AGE)
        }
    }
    override val cache: DivStateCache
        get() = cacheImpl

    @AnyThread
    override fun preloadState(cardId: String) {
        val future = executorService.submit(Callable<MutableMap<String, String>> {
            val result = ArrayMap<String, String>()
            divStateDao.getStates(cardId).forEach {
                result[it.path] = it.stateId
            }
            result
        })
        val rootStateFuture = executorService.submit(Callable {
            divStateDao.getRootStateId(cardId)
        })
        cacheImpl.putRootState(cardId, rootStateFuture)
        cacheImpl.putState(cardId, future)
    }

    @WorkerThread
    override fun deleteAllStates() {
        divStateDao.deleteAll()
        cacheImpl.clear()
    }

    @WorkerThread
    override fun deleteStatesExceptGiven(cardIds: List<String>) {
        divStateDao.deleteAllExcept(cardIds)
    }

    companion object {
        private val STATE_MAX_AGE = TimeUnit.DAYS.toMillis(2)
    }
}
