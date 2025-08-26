package divkit.dsl.annotation

import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

/**
 * Annotation to to mark source code that should no longer be used.
 */
@Target(CLASS, CONSTRUCTOR, FUNCTION, PROPERTY, VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class DeprecatedApi
