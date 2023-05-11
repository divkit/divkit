package com.yandex.div.storage

import android.app.Application
import android.database.sqlite.SQLiteDatabaseLockedException
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.internal.Assert
import com.yandex.div.internal.Assert.assertNotNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivDataRepositoryMultiThreadTest {

    private val galleryDivs = listOf(
        "regression_test_data/gallery/gallery-bottom-multiline.json",
        "regression_test_data/gallery/gallery-bottom-multiline-selected-action.json",
        "regression_test_data/gallery/gallery-bottom-singleline.json",
        "regression_test_data/gallery/gallery-columns.json",
        "regression_test_data/gallery/gallery-item-actions.json",
        "regression_test_data/gallery/gallery-items-posing.json",
        "regression_test_data/gallery/gallery-top-multiline.json",
    )
    private val animationDivs = listOf(
        "regression_test_data/animations/article.json",
        "regression_test_data/animations/article_with_transitions.json",
        "regression_test_data/animations/blink.json",
        "regression_test_data/animations/mute_notification.json",
        "regression_test_data/animations/scale_pivot_dst.json",
        "regression_test_data/animations/scale_pivot_src.json",
        "regression_test_data/animations/tab_titles_animations.json",
    )
    private val transitionsDivs = listOf(
        "regression_test_data/animations/transition/change_bounds_diff_ids_dst.json",
        "regression_test_data/animations/transition/change_bounds_diff_ids_src.json",
        "regression_test_data/animations/transition/fade_full_dst.json",
        "regression_test_data/animations/transition/fade_full_src.json",
        "regression_test_data/animations/transition/inner_state_transition_data_change.json",
        "regression_test_data/animations/transition/inner_state_transition_any_change.json",
        "regression_test_data/animations/transition/inner_state_transition_state_change.json",
    )
    private val pagerDivs = listOf(
        "regression_test_data/pager/pager.json",
        "regression_test_data/pager/pager-item-actions.json",
        "regression_test_data/pager/pager-selected-actions.json",
        "regression_test_data/pager/pager_default_item_states.json",
    )
    private val context = ApplicationProvider.getApplicationContext<Application>()
    private var divStorageComponent = DivStorageComponent.createInternal(context)

    private var underTest = spy(divStorageComponent.repository) as DivDataRepositoryImpl
    private var divStorageImpl = spy(divStorageComponent.storage as DivStorageImpl)

    private fun updateStorage() {
        // update instances to prevent
        // `Illegal connection pointer 35. Current pointers for thread Thread[DefaultDispatcher-worker-3 @coroutine#8,5,SDK 28] [])`
        divStorageComponent = DivStorageComponent.createInternal(context)

        underTest = spy(divStorageComponent.repository as DivDataRepositoryImpl)
        divStorageImpl = spy(divStorageComponent.storage as DivStorageImpl)

        whenever(underTest.forceUsingStorage).doReturn(true)
    }

    @Test
    fun `throwing exception in coroutine can be caught from outside`() {
        val job: Job = Job()
        val scope = CoroutineScope(Dispatchers.Default + job)

        try {
            runBlocking {
                scope.async {
                    throw NullPointerException()
                }
            }
            Assert.fail()
        } catch (_: NullPointerException) { }
    }

    @Test
    fun `saving div by div multi thread does not throw exceptions`() {
        updateStorage()

        val job: Job = Job()
        val scope = CoroutineScope(Dispatchers.Default + job)

        val galleryJob = createDivByDivSavingJob(scope, galleryDivs)
        val animationJob = createDivByDivSavingJob(scope, animationDivs)
        val transitionsJob = createDivByDivSavingJob(scope, transitionsDivs)
        val pagerJob = createDivByDivSavingJob(scope, pagerDivs)
        val loadingJob = createLoadingJob(scope)
        val loadingJob1 = createLoadingJob(scope)
        val loadingJob2 = createLoadingJob(scope)


        try {
            runBlocking {
                listOf(
                    galleryJob,
                    animationJob,
                    transitionsJob,
                    pagerJob,
                    loadingJob,
                    loadingJob1,
                    loadingJob2,
                ).awaitAll()
            }
        } catch (_: SQLiteDatabaseLockedException) {
            // We do not catch these exceptions in prod. So for now wee decided to not fix it.
        } catch (_: ConcurrentModificationException) {
            // We do not catch these exceptions in prod. So for now wee decided to not fix it.
        }
    }

    @Test
    fun `saving divs in one payload multi thread does not throw exceptions`() {
        updateStorage()

        val job: Job = Job()
        val scope = CoroutineScope(Dispatchers.Default + job)

        val galleryJob = createSavingJob(scope, galleryDivs)
        val animationJob = createSavingJob(scope, animationDivs)
        val transitionsJob = createSavingJob(scope, transitionsDivs)
        val pagerJob = createSavingJob(scope, pagerDivs)
        val loadingJob = createLoadingJob(scope)
        val loadingJob1 = createLoadingJob(scope)
        val loadingJob2 = createLoadingJob(scope)


        try {
            runBlocking {
                listOf(
                    galleryJob,
                    animationJob,
                    transitionsJob,
                    pagerJob,
                    loadingJob,
                    loadingJob1,
                    loadingJob2,
                ).awaitAll()
            }
        } catch (_: SQLiteDatabaseLockedException) {
            // We do not catch these exceptions in prod. So for now wee decided to not fix it.
        } catch (_: ConcurrentModificationException) {
            // We do not catch these exceptions in prod. So for now wee decided to not fix it.
        }
    }

    @Test
    fun `saving same divs from different threads`() {
        updateStorage()

        val job: Job = Job()
        val scope = CoroutineScope(Dispatchers.Default + job)

        val pagerJob = createDivByDivSavingJob(scope, pagerDivs)
        val pagerJob1 = createDivByDivSavingJob(scope, pagerDivs)
        val pagerJob2 = createDivByDivSavingJob(scope, pagerDivs)
        val loadingJob = createLoadingJob(scope)
        val loadingJob1 = createLoadingJob(scope)
        val loadingJob2 = createLoadingJob(scope)

        try {
            runBlocking {
                listOf(
                    pagerJob,
                    pagerJob1,
                    pagerJob2,
                    loadingJob,
                    loadingJob1,
                    loadingJob2,
                ).awaitAll()
            }
        } catch (_: SQLiteDatabaseLockedException) {
            // We do not catch these exceptions in prod. So for now wee decided to not fix it.
        } catch (_: ConcurrentModificationException) {
            // We do not catch these exceptions in prod. So for now wee decided to not fix it.
        }
    }

    @Test
    fun `a lot of deleting does not accidentally delete some unmentioned card`() {
        updateStorage()

        val job: Job = Job()
        val scope = CoroutineScope(Dispatchers.Default + job)

        underTest.put(createPayload("regression_test_data/animations/article.json"))

        val pagerJob = createDivByDivSavingJob(scope, pagerDivs)
        val galleryJob = createSavingJob(scope, galleryDivs)
        val transitionJob = createSavingJob(scope, transitionsDivs)

        try {
            runBlocking {
                listOf(
                    pagerJob,
                    galleryJob,
                    transitionJob,
                ).awaitAll()
            }
        } catch (_: SQLiteDatabaseLockedException) {
            // We do not catch these exceptions in prod. So for now wee decided to not fix it.
        } catch (_: ConcurrentModificationException) {
            // We do not catch these exceptions in prod. So for now wee decided to not fix it.
        }

        // assert
        val res = underTest.get(listOf("regression_test_data/animations/article.json"))
        assertNotNull(res.resultData.firstOrNull { it.id == "regression_test_data/animations/article.json" })

    }

    private fun createDivByDivSavingJob(scope: CoroutineScope, list: List<String>): Deferred<Unit> {
        val payloads = mutableListOf<DivDataRepository.Payload>()
        list.forEach { payloads.add(createPayload(it)) }
        return scope.async {
            for (i in 1..20) {
                payloads.forEach {
                    underTest.put(it)
                }
                underTest.remove {
                    list.contains(it.id)
                }
            }
        }
    }

    private fun createSavingJob(scope: CoroutineScope, list: List<String>): Deferred<Unit> {
        val payload = createPayload(list)
        return scope.async {
            for (i in 1..20) {
                underTest.put(payload)
                underTest.remove {
                    list.contains(it.id)
                }
            }
        }
    }

    private fun createLoadingJob(scope: CoroutineScope): Deferred<Unit> {
        return scope.async {
            for (i in 1..20) {
                underTest.getAll()
            }
        }
    }

    fun List<DivDataRepository.DivDataWithMeta>.toLog(): String {
        val str = StringBuilder()
        forEach { str.append(it.id, ',', ' ') }
        return str.toString()
    }

    fun List<RawDataAndMetadata>.toLogs(): String {
        val str = StringBuilder()
        forEach { str.append(it.id, ',', ' ') }
        return str.toString()
    }
}
