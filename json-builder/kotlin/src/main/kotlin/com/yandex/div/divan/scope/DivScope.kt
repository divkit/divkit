package com.yandex.div.divan.scope

import com.yandex.div.divan.core.Div
import com.yandex.div.divan.core.Template
import kotlin.String
import kotlin.collections.MutableMap

open class DivScope internal constructor() {
    internal val templates: MutableMap<String, Template<out Div>> = mutableMapOf()
}
