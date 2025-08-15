package com.yandex.div.core.dagger

import javax.inject.Scope

/**
 * A Dagger scope used for objects created for each instance of [com.yandex.div.core.DivConfiguration].
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DivScope
