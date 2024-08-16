package com.yandex.div.json.templates

import com.yandex.div.json.JsonTemplate

@Deprecated("Use 'com.yandex.div.json.templates.CachingTemplateProvider' instead")
class MainTemplateProvider<T: JsonTemplate<*>>(
    inMemoryProvider: InMemoryTemplateProvider<T>,
    dbProvider: TemplateProvider<T>,
) : CachingTemplateProvider<T>(inMemoryProvider, dbProvider)
