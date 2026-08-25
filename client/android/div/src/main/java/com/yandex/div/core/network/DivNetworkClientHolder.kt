package com.yandex.div.core.network

import com.yandex.div.core.annotations.InternalApi

/** Carries the optional root network client through dependency injection. */
@InternalApi
public class DivNetworkClientHolder internal constructor(
    public val client: DivNetworkClient?,
)
