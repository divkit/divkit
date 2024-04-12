package com.yandex.div.core.annotations

@RequiresOptIn(
    message = "This API is for internal use only. It shouldn't be used as it may be changed or removed without notice and will cause a compilation error in a future release.",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
public annotation class InternalApi
