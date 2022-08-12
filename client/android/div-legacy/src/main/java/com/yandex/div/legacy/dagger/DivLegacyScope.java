package com.yandex.div.legacy.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;

/**
 * A Dagger scope used for objects created for each instance of //todo.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface DivLegacyScope {}
