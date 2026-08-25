package com.yandex.div.core.network

/** An immutable HTTP request passed to [DivNetworkClient]. */
public class DivNetworkRequest private constructor(
    public val url: String,
    public val method: String,
    headers: List<DivNetworkRequestHeader>,
    body: ByteArray?,
    public val contentType: String?,
) {
    public val headers: List<DivNetworkRequestHeader> = buildList { addAll(headers) }
    private val bodyValue: ByteArray? = body?.copyOf()
    public val body: ByteArray?
        get() = bodyValue?.copyOf()

    /** Builds a request for [url]. */
    public class Builder(private val url: String) {
        private var method: String = "GET"
        private val headers = mutableListOf<DivNetworkRequestHeader>()
        private var body: ByteArray? = null
        private var contentType: String? = null

        public fun method(method: String): Builder = apply {
            this.method = method
        }

        public fun addHeader(name: String, value: String): Builder = apply {
            headers += DivNetworkRequestHeader(name, value)
        }

        public fun addHeader(header: DivNetworkRequestHeader): Builder = apply {
            headers += header
        }

        public fun body(body: ByteArray?): Builder = apply {
            this.body = body?.copyOf()
        }

        public fun contentType(contentType: String?): Builder = apply {
            this.contentType = contentType
        }

        public fun build(): DivNetworkRequest = DivNetworkRequest(url, method, headers, body, contentType)
    }

}
