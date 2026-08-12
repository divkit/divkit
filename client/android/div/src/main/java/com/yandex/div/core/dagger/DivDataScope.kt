package com.yandex.div.core.dagger

import com.yandex.div.DivDataTag
import javax.inject.Scope

/**
 * A Dagger scope used for objects created for every unique [DivDataTag].
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DivDataScope
