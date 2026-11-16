package com.yandex.div.storage

import com.yandex.div.core.annotations.InternalApi

@InternalApi
public enum class ActionOnError {
    ABORT_TRANSACTION,
    SKIP_ELEMENT,
}
