package com.yandex.div.core.network

/** An HTTP response whose [body] and underlying resources are released by [close]. */
public interface DivNetworkResponse : AutoCloseable {
    public val url: String
    public val code: Int
    public val contentType: String?
    public val body: DivNetworkResponseBody?

    /** Returns all values for [name], matching the header name case-insensitively. */
    public fun headers(name: String): List<String>

    public val isSuccessful: Boolean
        get() = code in 200..299
}

/**
 * A platform-independent streaming response body.
 *
 * Implementations return `-1` at end of stream and never return `0` when `length` is positive.
 * [read] requires a non-negative `offset` and `length` whose range fits into `buffer`; [skip]
 * requires a non-negative `byteCount`.
 */
public interface DivNetworkResponseBody : AutoCloseable {
    public fun read(buffer: ByteArray, offset: Int, length: Int): Int
    public fun skip(byteCount: Long): Long

    public companion object {
        public fun fromBytes(bytes: ByteArray): DivNetworkResponseBody = ByteArrayResponseBody(bytes.copyOf())
    }
}

public fun DivNetworkResponseBody.readBytes(): ByteArray {
    val chunks = mutableListOf<ByteArray>()
    var size = 0
    while (true) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val count = read(buffer, 0, buffer.size)
        if (count < 0) break
        check(count != 0) { "Body returned 0 for a non-empty buffer" }
        chunks += if (count == buffer.size) buffer else buffer.copyOf(count)
        size += count
    }
    val result = ByteArray(size)
    var offset = 0
    chunks.forEach { chunk ->
        chunk.copyInto(result, offset)
        offset += chunk.size
    }
    return result
}

private class ByteArrayResponseBody(private val bytes: ByteArray) : DivNetworkResponseBody {
    private var position = 0

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        require(offset >= 0 && length >= 0 && offset <= buffer.size - length)
        if (length == 0) return 0
        if (position == bytes.size) return -1
        val count = minOf(length, bytes.size - position)
        bytes.copyInto(buffer, offset, position, position + count)
        position += count
        return count
    }

    override fun skip(byteCount: Long): Long {
        require(byteCount >= 0)
        val count = minOf(byteCount, (bytes.size - position).toLong()).coerceAtLeast(0).toInt()
        position += count
        return count.toLong()
    }

    override fun close() = Unit
}
