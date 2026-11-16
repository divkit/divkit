package com.yandex.div.storage.database

import com.yandex.div.storage.ActionOnError
import com.yandex.div.storage.StorageCodec
import com.yandex.div.storage.database.ExecutionResult
import com.yandex.div.storage.internal.RawDataAndMetadata
import com.yandex.div.storage.internal.rawjson.RawJson
import com.yandex.div.storage.templates.Template

internal class SingleTransactionDataSavePerformer<T : Any>(
        codec: StorageCodec<T>,
        private val storageStatementsExecutor: StorageStatementExecutor,
) {
    private val storageStatements = StorageStatements(codec)
    fun saveDivData(groupId: String,
                    cards: List<RawDataAndMetadata<T>>,
                    templatesByHash: List<Template<T>>,
                    actionOnError: ActionOnError,
    ) = executeStatements(actionOnError) {
        // 1. Save templates names that were declared in this group
        add(storageStatements.writeTemplatesUsages(groupId, templatesByHash))

        // 2. Save contents of cards
        add(createCardsSaveStatement(groupId, cards))

        // 3. Save templates
        add(storageStatements.writeTemplates(templatesByHash))

        // 4. Delete templates that no longer used by cards
        add(storageStatements.deleteTemplatesWithoutLinksToCards())
    }

    fun saveRawJsons(
        rawJsons: List<RawJson<T>>,
        actionOnError: ActionOnError,
    ) = executeStatements(actionOnError) {
        // 1. Save raw jsons
        add(createRawJsonsSaveStatement(rawJsons))
    }

    private fun executeStatements(
        actionOnError: ActionOnError = ActionOnError.ABORT_TRANSACTION,
        statementsBuilder: MutableList<StorageStatement>.() -> Unit
    ): ExecutionResult {
        val statementList = mutableListOf<StorageStatement>().apply {
            statementsBuilder.invoke(this)
        }
        return storageStatementsExecutor.execute(actionOnError, *statementList.toTypedArray())
    }

    private fun createCardsSaveStatement(groupId: String, cards: List<RawDataAndMetadata<T>>) =
        storageStatements.replaceCards(groupId, cards)

    private fun createRawJsonsSaveStatement(rawJsons: List<RawJson<T>>) =
        storageStatements.replaceRawJsons(rawJsons)
}
