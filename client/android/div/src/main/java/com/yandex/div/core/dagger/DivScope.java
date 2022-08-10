package com.yandex.div.core.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * A Dagger scope used for objects created for each instance of {@link com.yandex.div.core.DivConfiguration}.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface DivScope { }
