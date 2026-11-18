package com.yandex.div.storage

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.storage.database.DatabaseOpenHelper.Database
import com.yandex.div.storage.database.DatabaseOpenHelperProvider
import com.yandex.div.storage.db.DatabaseOpenHelper
import com.yandex.div.storage.db.TemplateDaoImpl
import com.yandex.div.storage.db.TemplateUsageDaoImpl
import com.yandex.div.storage.entity.Template
import com.yandex.div.storage.entity.TemplateUsage
import com.yandex.div.storage.internal.WorkerThread
import com.yandex.div.storage.util.closeAll
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * Persistent div storage backed by the database.
 */
@InternalApi
public open class BaseDivDatabaseStorage(
    openHelperProvider: DatabaseOpenHelperProvider,
    databaseName: String = "div.db"
) : DivTemplateStorage, AutoCloseable {

    private val lock = reentrantLock()
    private val createOpenHelper = { DatabaseOpenHelper(openHelperProvider, databaseName) }
    private var openHelper: DatabaseOpenHelper? = null
    private var database: Database? = null
    private var closed = false

    @WorkerThread
    public fun readAllTemplates(): Map<String, ByteArray> = withDatabase { db ->
        TemplateDaoImpl(db).getAllTemplates().associate { it.id to it.data }
    }

    @WorkerThread
    public override fun readTemplates(cardId: String): Map<String, ByteArray> = withDatabase { db ->
        TemplateDaoImpl(db).getTemplates(cardId).associate { it.id to it.data }
    }

    @WorkerThread
    public override fun readTemplatesByIds(vararg templateId: String): Map<String, ByteArray> =
        withDatabase { db ->
            TemplateDaoImpl(db).getTemplatesByIds(templateId.toList()).associate { it.id to it.data }
        }

    @WorkerThread
    public override fun writeTemplates(cardId: String, templates: Map<String, ByteArray>): Unit =
        withDatabase { db ->
            db.inTransaction {
                val templateDao = TemplateDaoImpl(db)
                val usageDao = TemplateUsageDaoImpl(db)
                usageDao.deleteTemplateUsages(cardId)
                templates.forEach { (templateId, templateData) ->
                    templateDao.insertTemplate(Template(templateId, templateData))
                    usageDao.insertTemplateUsage(TemplateUsage(cardId, templateId))
                }
                templateDao.deleteUnusedTemplates()
            }
        }

    @WorkerThread
    public override fun deleteTemplates(cardId: String): Unit = withDatabase { db ->
        db.inTransaction {
            TemplateUsageDaoImpl(db).deleteTemplateUsages(cardId)
            TemplateDaoImpl(db).deleteUnusedTemplates()
        }
    }

    @WorkerThread
    public override fun clear(): Unit = withDatabase { db ->
        db.inTransaction {
            TemplateDaoImpl(db).deleteAllTemplates()
            TemplateUsageDaoImpl(db).deleteAllTemplateUsages()
        }
    }

    override fun close(): Unit = lock.withLock {
        if (closed) return@withLock
        val db = if (openHelper != null) database else acquireDatabase()
        closed = true
        database = null
        db?.close()
    }

    private fun acquireDatabase(): Database {
        val helper = openHelper ?: createOpenHelper()
        val db = helper.writableDatabase
        openHelper = helper
        database = db
        return db
    }

    @Suppress("TooGenericExceptionCaught")
    private inline fun <T> withDatabase(block: (Database) -> T): T = lock.withLock {
        check(!closed) { "Storage is closed" }
        val db = database ?: acquireDatabase()
        try {
            block(db)
        } catch (error: Throwable) {
            database = null
            closeAll(db, failure = error)
            throw error
        }
    }

    private inline fun Database.inTransaction(block: () -> Unit) {
        beginTransaction()
        AutoCloseable { endTransaction() }.use {
            block()
            setTransactionSuccessful()
        }
    }
}
