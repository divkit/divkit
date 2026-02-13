package com.yandex.div.internal.storage

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class DataStorageTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val editor = mock<DataEditor<String>> {
        on { write(any(), any()) } doAnswer { Unit }
    }
    private val testFile = File(context.filesDir, "test_file")

    @Test
    fun `data should be null initially`() = runTest {
        val storage = createDataStorage(editor)
        collectFlow(storage.data)

        assertNull(storage.data.value)
    }

    @Test
    fun `update should write data to file and update state`() = runTest {
        val storage = createDataStorage(editor)
        val testData = "test data"

        storage.update(testData)
        collectFlow(storage.data)
        advanceUntilIdle()

        verify(editor).write(eq(testData), any())
        assertEquals(testData, storage.data.value)
    }

    @Test
    fun `clear should delete file and reset state`() = runTest {
        val storage = createDataStorage(editor)
        val testData = "test data"

        storage.update(testData)
        collectFlow(storage.data)
        advanceUntilIdle()

        assertEquals(testData, storage.data.value)

        val result = storage.clear()
        advanceUntilIdle()

        assertTrue(result)
        assertNull(storage.data.value)
    }

    @Test
    fun `data should be read from file on initialization`() = runTest {
        testFile.parentFile?.mkdirs()
        testFile.createNewFile()

        val testData = "test data"
        whenever(editor.read(any())).thenReturn(testData)
        val storage = createDataStorage(editor)

        collectFlow(storage.data)
        advanceUntilIdle()

        assertEquals(testData, storage.data.value)
    }

    @Test
    fun `data should be null if file reading fails`() = runTest {
        whenever(editor.read(any())).thenThrow(RuntimeException("Read error"))
        val storage = createDataStorage(editor)

        collectFlow(storage.data)
        advanceUntilIdle()

        assertNull(storage.data.value)
    }

    @Test
    fun `update should handle exceptions`() = runTest {
        val testData = "test data"
        whenever(editor.write(any(), any())).thenThrow(RuntimeException("Write error"))
        val storage = createDataStorage(editor)

        storage.update(testData)
        collectFlow(storage.data)
        advanceUntilIdle()

        // Data should remain null due to exception
        assertNull(storage.data.value)
    }

    @Test
    fun `clear should handle exceptions`() = runTest {
        // Make file undeletable by making it a non-empty directory
        testFile.mkdirs()
        File(testFile, "sub_file").createNewFile()
        val storage = createDataStorage(editor)

        val result = storage.clear()
        advanceUntilIdle()

        assertFalse(result)
    }

    private fun <T> TestScope.createDataStorage(editor: DataEditor<T>): DataStorage<T> {
        val coroutineDispatcher = StandardTestDispatcher(testScheduler)
        val coroutineScope = TestScope(coroutineDispatcher)
        return DataStorage.create(context, testFile.name, editor, coroutineScope)
    }

    private fun <T> TestScope.collectFlow(flow: Flow<T>): Job {
        return backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            flow.collect()
        }
    }
}
