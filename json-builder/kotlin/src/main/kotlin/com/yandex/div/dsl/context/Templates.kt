// Copyright (c) 2022 Yandex LLC. All rights reserved.
// Author: Anton Gulevsky <gulevsky@yandex-team.ru>.

package com.yandex.div.dsl.context

import com.yandex.div.dsl.ReferenceProperty

sealed class TemplateBinding<T>

class PropertyOverriding<T> internal constructor(
    val name: String,
    val value: T
) : TemplateBinding<T>()

fun <T> TemplateContext<*>.override(name: String, value: T): PropertyOverriding<T> {
    return PropertyOverriding(name, value)
}

fun <T> CardContext.override(name: String, value: T): PropertyOverriding<T> {
    return PropertyOverriding(name, value)
}

class ReferenceResolving<T> internal constructor(
    val reference: ReferenceProperty<T>,
    val value: T
) : TemplateBinding<T>()

fun <T> CardContext.resolve(reference: ReferenceProperty<T>, value: T): ReferenceResolving<T> {
    return ReferenceResolving(reference, value)
}
