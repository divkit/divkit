package com.yandex.div.core.network

/** An HTTP request header. Multiple headers with the same [name] are preserved. */
public class DivNetworkRequestHeader(
    public val name: String,
    public val value: String,
)
