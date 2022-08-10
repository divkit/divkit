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

@Suppress("ConvertToStringTemplate")
fun propertyMapOf(vararg properties: Pair<String, Property<*>?>): Map<String, Any> {
    return properties.mapNotNull { (key, property) ->
        when (property) {
            null -> null
            is ReferenceProperty -> ("$" + key) to property.name
            is LiteralProperty -> if (property.value == null) null else key to property.value
            is ExpressionProperty -> key to property.expression
        }
    }.toMap()
}

fun propertyMapOf(vararg bindings: TemplateBinding<*>): Map<String, Any> {
    return bindings.mapNotNull { binding ->
        when (binding) {
            is PropertyOverriding -> if (binding.value == null) null else binding.name to binding.value
            is ReferenceResolving -> if (binding.value == null) null else binding.reference.name to binding.value
        }
    }.toMap()
}
