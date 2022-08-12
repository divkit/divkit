package com.yandex.div.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.WorkerThread
import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.storage.db.DatabaseOpenHelper
import com.yandex.div.storage.db.TemplateDao
import com.yandex.div.storage.db.TemplateDaoImpl
import com.yandex.div.storage.db.TemplateUsageDao
import com.yandex.div.storage.db.TemplateUsageDaoImpl
import com.yandex.div.storage.entity.Template
import com.yandex.div.storage.entity.TemplateUsage
import java.io.Closeable

/**
 * Persistent div storage backed by the database.
 */
@PublicApi
class DivDatabaseStorage @JvmOverloads constructor(
    context: Context,
    databaseName: String = "div.db"
) : DivTemplateStorage, Closeable {

    private val database: SQLiteDatabase by lazy {
        DatabaseOpenHelper(context, databaseName).writableDatabase
    }
    private val templateDao: TemplateDao by lazy {
        TemplateDaoImpl(database)
    }
    private val templateUsageDao: TemplateUsageDao by lazy {
        TemplateUsageDaoImpl(database)
    }

    @WorkerThread
    fun readAllTemplates(): Map<String, ByteArray> {
        return templateDao.getAllTemplates()
            .associate { template -> template.id to template.data }
    }

    @WorkerThread
    override fun readTemplates(cardId: String): Map<String, ByteArray> {
        return templateDao.getTemplates(cardId)
            .associate { template -> template.id to template.data }
    }

    @WorkerThread
    override fun readTemplatesByIds(vararg templateId: String): Map<String, ByteArray> {
        return templateDao.getTemplatesByIds(templateId.toList())
            .associate { template -> template.id to template.data }
    }

    @WorkerThread
    override fun writeTemplates(cardId: String, templates: Map<String, ByteArray>) {
        database.inTransaction {
            templateUsageDao.deleteTemplateUsages(cardId)
            templates.forEach { (templateId, templateData) ->
                templateDao.insertTemplate(Template(templateId, templateData))
                templateUsageDao.insertTemplateUsage(TemplateUsage(cardId, templateId))
            }
            templateDao.deleteUnusedTemplates()
        }
    }

    @WorkerThread
    override fun deleteTemplates(cardId: String) {
        database.inTransaction {
            templateUsageDao.deleteTemplateUsages(cardId)
            templateDao.deleteUnusedTemplates()
        }
    }

    @WorkerThread
    override fun clear() {
        database.inTransaction {
            templateDao.deleteAllTemplates()
            templateUsageDao.deleteAllTemplateUsages()
        }
    }

    override fun close() {
        database.close()
    }

    private inline fun SQLiteDatabase.inTransaction(block: () -> Unit) {
        beginTransaction()
        try {
            block()
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }
}
