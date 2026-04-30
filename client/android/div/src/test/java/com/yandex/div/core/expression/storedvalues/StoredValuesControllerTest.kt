package com.yandex.div.core.expression.storedvalues

import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.data.StoredValue
import com.yandex.div.storage.DivStorageComponent
import com.yandex.div.storage.RawJsonRepository
import com.yandex.div.storage.RawJsonRepositoryResult
import com.yandex.div2.DivActionSetStoredValue.Scope
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

private const val VALUE_NAME = "name"
private const val DATA_TAG = "unique_tag"
private const val GLOBAL_SCOPE_ID = "stored_value_name"
private const val CARD_SCOPE_ID = "card_unique_tag_stored_value_name"

class StoredValuesControllerTest {

    private val ids = argumentCaptor<List<String>>()
    private val payload = argumentCaptor<RawJsonRepository.Payload>()
    private val result = mock<RawJsonRepositoryResult>()
    private val rawJsonRepository = mock<RawJsonRepository> {
        on { get(ids.capture()) } doReturn result
        on { put(payload.capture()) } doReturn result
    }
    private val storageComponent = mock<DivStorageComponent>{
        on { rawJsonRepository } doReturn rawJsonRepository
    }
    private val errorCollector = mock<ErrorCollector>()
    private val storedValue = StoredValue.StringStoredValue(VALUE_NAME, "")
    private val underTest = StoredValuesController { storageComponent }

    @Test
    fun `get value without scope prefix when scope is global`() {
        underTest.getStoredValue(VALUE_NAME, errorCollector, DATA_TAG, Scope.toString(Scope.GLOBAL))
        checkValueId(GLOBAL_SCOPE_ID)
    }

    @Test
    fun `get value with card id when scope is card`() {
        underTest.getStoredValue(VALUE_NAME, errorCollector, DATA_TAG, Scope.toString(Scope.CARD))
        checkValueId(CARD_SCOPE_ID)
    }

    private fun checkValueId(expected: String) {
        Assert.assertEquals(expected, ids.firstValue.first())
    }

    @Test
    fun `store value without scope prefix when scope is global`() {
        underTest.setStoredValue(storedValue, 0L, Scope.GLOBAL, DATA_TAG, errorCollector)
        checkStoredValueId(GLOBAL_SCOPE_ID)
    }

    @Test
    fun `store value with card id when scope is card`() {
        underTest.setStoredValue(storedValue, 0L, Scope.CARD, DATA_TAG, errorCollector)
        checkStoredValueId(CARD_SCOPE_ID)
    }

    private fun checkStoredValueId(expected: String) {
        Assert.assertEquals(expected, payload.firstValue.jsons.first().id)
    }
}
