package com.yandex.div.storage.database

import com.yandex.div.storage.DivDataRepository.ActionOnError
import com.yandex.div.storage.RawDataAndMetadata
import com.yandex.div.storage.rawjson.RawJson
import com.yandex.div.storage.templates.Template
import java.io.IOException
import javax.inject.Inject

internal class SingleTransactionDataSavePerformer @Inject constructor(
        private val storageStatementsExecutor: StorageStatementExecutor,
) {
    @Throws(IOException::class)
    fun saveDivData(groupId: String,
                    cards: List<RawDataAndMetadata>,
                    templatesByHash: List<Template>,
                    actionOnError: ActionOnError,
    ) = executeStatements(actionOnError) {
        // 1. Save templates names that were declared in this group
        add(StorageStatements.writeTemplatesUsages(groupId, templatesByHash))

        // 2. Save contents of cards
        add(createCardsSaveStatement(groupId, cards))

        // 3. Save templates
        add(StorageStatements.writeTemplates(templatesByHash))

        // 4. Delete templates that no longer used by cards
        add(StorageStatements.deleteTemplatesWithoutLinksToCards())
    }

    @Throws(IOException::class)
    fun saveRawJsons(
        rawJsons: List<RawJson>,
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

    private fun createCardsSaveStatement(groupId: String, cards: List<RawDataAndMetadata>) =
        StorageStatements.replaceCards(groupId, cards)

    private fun createRawJsonsSaveStatement(rawJsons: List<RawJson>) =
        StorageStatements.replaceRawJsons(rawJsons)
}
