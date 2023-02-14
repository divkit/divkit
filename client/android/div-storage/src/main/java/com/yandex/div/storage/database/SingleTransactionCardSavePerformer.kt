package com.yandex.div.storage.database

import com.yandex.div.storage.DivDataRepository.ActionOnError
import com.yandex.div.storage.RawDataAndMetadata
import com.yandex.div.storage.templates.Template
import java.io.IOException
import javax.inject.Inject

internal class SingleTransactionCardSavePerformer @Inject constructor(
        private val storageStatementsExecutor: StorageStatementExecutor,
) {
    @Throws(IOException::class)
    fun save(groupId: String,
             cards: List<RawDataAndMetadata>,
             templatesByHash: List<Template>,
             actionOnError: ActionOnError = ActionOnError.ABORT_TRANSACTION
    ): ExecutionResult {
        val statements = mutableListOf<StorageStatement>()

        // 1. Save templates names that were declared in this group
        statements.add(StorageStatements.writeTemplatesUsages(groupId, templatesByHash))

        // 2. Save contents of cards
        statements.add(createCardsSaveStatement(groupId, cards))

        // 3. Save templates
        statements.add(StorageStatements.writeTemplates(templatesByHash))

        // 4. Delete templates that no longer used by cards
        statements.add(StorageStatements.deleteTemplatesWithoutLinksToCards())

        return storageStatementsExecutor.execute(actionOnError, *statements.toTypedArray())
    }

    private fun createCardsSaveStatement(
            groupId: String,
            cards: List<RawDataAndMetadata>
    ): StorageStatement {
        return StorageStatements.replaceCards(groupId, cards)
    }
}
