package com.yandex.div.storage

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.database.DatabaseOpenHelper
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.spy
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivStorageImplHandleIllegalStateExceptionTest {

    private val ID_0 = "id0"

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val readableDb = mock<DatabaseOpenHelper.Database> {
        on { rawQuery(any(), any()) } doReturn mock()
        on { query(any(), any(), any(), any(), any(), any(), any(), any()) } doReturn mock()
        on { query(any(), eq(null), any(), eq(null), eq(null), eq(null), eq(null), eq(null)) } doReturn mock()
        on { query(any(), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), eq(null)) } doReturn mock()
    }
    private val dbHelper = mock<DatabaseOpenHelper> {
        on { readableDatabase } doReturn readableDb
        on { writableDatabase } doReturn readableDb
    }
    private val openDbHelperProvider = mock<DatabaseOpenHelperProvider> {
        on { provide(any(), any(), any(), any(), any()) } doReturn dbHelper
    }

    private val underTest = spy(DivStorageImpl(context, openDbHelperProvider))
    private val savedStatementExecutor = spy(underTest.statementExecutor)

    @Test
    fun `loadData without exception calls only one iteration`() {
        // action
        underTest.loadData(listOf(ID_0))

        // assertion
        verify(dbHelper, times(1)).readableDatabase
    }

    @Test
    fun `loadData doesnt return error when next try was correct`() {
        // action
        throwExceptionOnFirstIteration()
        val res = underTest.loadData(listOf(ID_0))

        // assertion
        verify(dbHelper, times(2)).readableDatabase
        assertEquals(0, res.errors.size)
    }

    @Test
    fun `loadData with exception calls two db iteration and not throw exception`() {
        // action
        alwaysThrowException()
        val res = underTest.loadData(listOf(ID_0))

        // assertion
        verify(dbHelper, times(2)).readableDatabase
        assertEquals(1, res.errors.size)
    }

    @Test
    fun `loadTemplates without exception calls only one iteration`() {
        // action
        underTest.readTemplates(setOf(ID_0))

        // assertion
        verify(dbHelper, times(1)).readableDatabase
    }

    @Test
    fun `loadTemplates with exception calls two db iteration and not throw exception`() {
        // action
        alwaysThrowException()
        val res = underTest.readTemplates(setOf(ID_0))

        // assertion
        verify(dbHelper, times(2)).readableDatabase
        assertEquals(1, res.errors.size)
    }

    @Test
    fun `loadTemplates doesnt return error when next try was correct`() {
        // action
        throwExceptionOnFirstIteration()
        val res = underTest.readTemplates(setOf(ID_0))

        // assertion
        verify(dbHelper, times(2)).readableDatabase
        assertEquals(0, res.errors.size)
    }

    @Test
    fun `loadTemplateReferences without exception calls only one iteration`() {
        // action
        underTest.readTemplateReferences()

        // assertion
        verify(dbHelper, times(1)).readableDatabase
    }

    @Test
    fun `loadTemplateReferences with exception calls two db iteration and not throw exception`() {
        // action
        alwaysThrowException()
        val res = underTest.readTemplateReferences()

        // assertion
        verify(dbHelper, times(2)).readableDatabase
        assertEquals(1, res.errors.size)
    }

    @Test
    fun `loadTemplateReferences doesnt return error when next try was correct`() {
        // action
        throwExceptionOnFirstIteration()
        val res = underTest.readTemplateReferences()

        // assertion
        verify(dbHelper, times(2)).readableDatabase
        assertEquals(0, res.errors.size)
    }

    @Test
    fun `isCardExist without exception calls only one iteration`() {
        // action
        underTest.isCardExists(ID_0, ID_0)

        // assertion
        verify(dbHelper, times(1)).writableDatabase
    }

    @Test
    fun `isCardExist doesnt return error when next try was correct`() {
        // action
        throwExceptionOnFirstIteration()
        underTest.isCardExists(ID_0, ID_0)

        // assertion
        verify(dbHelper, times(2)).writableDatabase
    }

    @Test
    fun `isCardExist with a lot of exceptions stops after 2 iterations`() {
        // action
        alwaysThrowException()
        underTest.isCardExists(ID_0, ID_0)

        // assertion
        verify(dbHelper, times(2)).writableDatabase
    }

    @Test
    fun `isTemplateExist without exception calls only one iteration`() {
        // action
        underTest.isTemplateExists(ID_0)

        // assertion
        verify(dbHelper, times(1)).writableDatabase
    }

    @Test
    fun `isTemplateExist doesnt return error when next try was correct`() {
        // action
        throwExceptionOnFirstIteration()
        underTest.isTemplateExists(ID_0)

        // assertion
        verify(dbHelper, times(2)).writableDatabase
    }

    @Test
    fun `isTemplateExist with a lot of exceptions stops after 2 iterations`() {
        // action
        alwaysThrowException()
        underTest.isTemplateExists(ID_0)

        // assertion
        verify(dbHelper, times(2)).writableDatabase
    }

    private fun alwaysThrowException() {
        whenever(dbHelper.readableDatabase).then { throw IllegalStateException() }
        whenever(savedStatementExecutor.execute()).then {
            return@then ExecutionResult(listOf(DivStorageErrorException(cause = IllegalStateException())))
        }
    }

    private fun throwExceptionOnFirstIteration() {
        var iteration = 0
        whenever(dbHelper.readableDatabase).then {
            if (iteration == 0) {
                iteration++
                throw IllegalStateException()
            }  else {
                return@then readableDb
            }
        }

        whenever(savedStatementExecutor.execute()).then {
            if (iteration == 0) {
                iteration++
                return@then ExecutionResult(listOf(DivStorageErrorException(cause = IllegalStateException())))
            }  else {
                return@then ExecutionResult()
            }
        }
    }
}
