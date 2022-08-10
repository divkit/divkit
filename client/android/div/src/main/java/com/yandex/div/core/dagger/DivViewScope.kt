package com.yandex.div.core.dagger

import javax.inject.Scope

/**
 * A Dagger scope used for objects created for each instance of [com.yandex.div.core.view2.Div2View].
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class DivViewScope()
