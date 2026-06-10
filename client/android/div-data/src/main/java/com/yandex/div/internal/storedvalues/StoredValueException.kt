package com.yandex.div.internal.storedvalues

import com.yandex.div.core.annotations.InternalApi

@InternalApi
class StoredValueException(
    override val message: String
) : RuntimeException(message)
