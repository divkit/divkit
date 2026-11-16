package com.yandex.div.storage.db

import com.yandex.div.storage.entity.Template

internal interface TemplateDao {
    fun getAllTemplates(): List<Template>
    fun getTemplates(cardId: String): List<Template>
    fun getTemplatesByIds(templateIds: List<String>): List<Template>
    fun insertTemplate(template: Template)
    fun deleteAllTemplates()
    fun deleteUnusedTemplates()
}
