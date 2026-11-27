package com.yandex.div.test.network

import android.content.res.AssetManager
import androidx.core.net.toUri
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Answers network requests of screenshot tests locally:
 * - a path ending with `/status/<code>` answers `<code>`;
 * - GET answers 200 with `test-utils/src/main/assets/network/<path>`;
 * - other methods answer 200 for any address; to test a failed submit, use a path ending with `/status/<code>`.
 */
class LocalNetworkResponses(private val assetManager: AssetManager) {

    private val unanswered = ConcurrentLinkedQueue<String>()

    class Response(val code: Int, val body: ByteArray)

    fun respond(method: String, url: String): Response {
        val path = url.toUri().path.orEmpty()
        val status = path.substringAfterLast("/status/", "").toIntOrNull()
        if (status != null || method != "GET") {
            return Response(status ?: 200, "{}".encodeToByteArray())
        }
        val asset = "network$path"
        return try {
            Response(200, assetManager.open(asset).use { it.readBytes() })
        } catch (e: FileNotFoundException) {
            val message = "No local response for $url: missing asset $asset"
            unanswered += message
            throw IOException(message, e)
        }
    }

    fun checkAllAnswered() = check(unanswered.isEmpty()) { unanswered.joinToString("\n") }
}
