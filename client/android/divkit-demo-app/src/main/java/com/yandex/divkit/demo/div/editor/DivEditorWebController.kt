package com.yandex.divkit.demo.div.editor

import android.net.Uri
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketFactory
import com.yandex.divkit.demo.Container
import org.json.JSONObject
import java.net.URI

private const val WEB_SOCKET_SCHEME = "wss"
private const val MESSAGE_PARAM = "message"
private const val TYPE_PARAM = "type"

class DivEditorWebController(
    private val webSocketFactory: WebSocketFactory = Container.webSocketFactory,
    private val logger: DivEditorLogger
) {

    private var connection: Pair<URI, WebSocket>? = null
    private val socketEventListener = SocketEventListener()
    private val messageListeners = mutableMapOf<String, (message: JSONObject) -> Unit>()

    init {
        socketEventListener.disconnectListener = { socketUri: URI ->
            if (socketUri == connection?.first) {
                connection = null
            }
        }
        socketEventListener.onTextMessageReceive = { text: String ->
            try {
                val response = JSONObject(text)
                logger.log("received message $text")
                val type = response.get(TYPE_PARAM)
                    ?: throw IllegalStateException("there's no \"$TYPE_PARAM\" field in received message")
                val message = response.get(MESSAGE_PARAM) as? JSONObject
                    ?: throw IllegalStateException("there's no \"$MESSAGE_PARAM\" field in received message")
                messageListeners[type]?.invoke(message)
            } catch (ignored: Exception) {
                logger.logError("Failed to parse message $text", ignored)
            }
        }
    }

    @WorkerThread
    fun connect(url: String) {
        // User input like:
        // https://divview-test.in.yandex.net/api/json?uuid=3fa06f2e-2cd9-44b1-ada8-baaaaff0166b
        val uri = Uri.parse(url)
        if (!uri.isValid()) {
            throw IllegalArgumentException("$url is not valid address")
        }
        val socketUri = Uri.Builder()
            .scheme(WEB_SOCKET_SCHEME)
            .authority(uri.authority)
            .build()
            .toJavaURI()

        connection?.apply {
            if (socketUri == this.first) {
                return
            }
        }
        val socket: WebSocket = webSocketFactory.createSocket(socketUri)
            .addListener(socketEventListener)
            .connect()
        connection = socketUri to socket
    }

    @WorkerThread
    fun sendMessage(@MessageType type: String, payload: JSONObject) {
        val webSocket: WebSocket = connection?.second
            ?: throw IllegalStateException("failed to send message, establish connection to socket first!")
        val message = JSONObject()
            .put(TYPE_PARAM, type)
            .put(MESSAGE_PARAM, payload)
        logger.log("sending message to ${webSocket.uri}: $message")
        webSocket.sendText(message.toString())
    }

    @AnyThread
    fun isConnected(): Boolean = connection != null

    fun addMessageListener(@MessageType type: String, callback: (JSONObject) -> Unit) {
        messageListeners[type] = callback
    }

    private fun Uri.isValid(): Boolean {
        if (isRelative || !isHierarchical) {
            logger.logError("Url: $this is not valid editor url")
            return false
        }
        val uuid: String? = getQueryParameter("uuid")
        if (uuid.isNullOrBlank()) {
            logger.logError("Provided $this should contain uuid query param")
            return false
        }
        return true
    }

    private fun Uri.toJavaURI() = URI.create(toString())
}
