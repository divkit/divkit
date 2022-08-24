// Copyright (c) 2022 Yandex LLC. All rights reserved.
// Author: Anton Gulevsky <gulevsky@yandex-team.ru>.

package com.yandex.div.dsl.util

import com.yandex.div.dsl.ExpressionProperty
import com.yandex.div.dsl.LiteralProperty
import com.yandex.div.dsl.Property
import com.yandex.div.dsl.ReferenceProperty
import com.yandex.div.dsl.context.PropertyOverriding
import com.yandex.div.dsl.context.ReferenceResolving
import com.yandex.div.dsl.context.TemplateBinding

fun propertyMapOf(vararg properties: Pair<String, Property<*>?>): Map<String, Any> {
    return properties.mapNotNull { (key, property) -> associateProperty(key, property) }
        .toMap()
}

fun propertyMapOf(vararg bindings: TemplateBinding<*>): Map<String, Any> {
    return bindings.mapNotNull { binding ->
        when (binding) {
            is PropertyOverriding -> associateProperty(binding.name, binding.property)
            is ReferenceResolving -> associateProperty(binding.reference.name, binding.property)
        }
    }.toMap()
}

@Suppress("ConvertToStringTemplate")
private fun associateProperty(key: String, property: Property<*>?): Pair<String, Any>? {
    return when (property) {
        null -> null
        is ReferenceProperty -> ("$" + key) to property.name
        is LiteralProperty -> if (property.value == null) null else key to property.value
        is ExpressionProperty -> key to property.expression
    }
}