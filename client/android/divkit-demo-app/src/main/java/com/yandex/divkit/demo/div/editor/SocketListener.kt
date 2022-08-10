package com.yandex.divkit.demo.div.editor

import com.neovisionaries.ws.client.ThreadType
import com.neovisionaries.ws.client.WebSocket
import com.neovisionaries.ws.client.WebSocketException
import com.neovisionaries.ws.client.WebSocketFrame
import com.neovisionaries.ws.client.WebSocketListener
import com.neovisionaries.ws.client.WebSocketState
import java.net.URI

abstract class SocketListener: WebSocketListener {

    override fun onStateChanged(websocket: WebSocket?, newState: WebSocketState?) {}

    override fun onConnected(websocket: WebSocket?, headers: MutableMap<String, MutableList<String>>?) {}

    override fun onConnectError(websocket: WebSocket?, cause: WebSocketException?) {}

    override fun onDisconnected(
        websocket: WebSocket?,
        serverCloseFrame: WebSocketFrame?,
        clientCloseFrame: WebSocketFrame?,
        closedByServer: Boolean
    ) {}

    override fun onFrame(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onContinuationFrame(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onTextFrame(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onBinaryFrame(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onCloseFrame(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onPingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onPongFrame(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onTextMessage(websocket: WebSocket?, text: String?) {}

    override fun onTextMessage(websocket: WebSocket?, data: ByteArray?) {}

    override fun onBinaryMessage(websocket: WebSocket?, binary: ByteArray?) {}

    override fun onSendingFrame(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onFrameSent(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onFrameUnsent(websocket: WebSocket?, frame: WebSocketFrame?) {}

    override fun onThreadCreated(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {}

    override fun onThreadStarted(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {}

    override fun onThreadStopping(websocket: WebSocket?, threadType: ThreadType?, thread: Thread?) {}

    override fun onError(websocket: WebSocket?, cause: WebSocketException?) {}

    override fun onFrameError(websocket: WebSocket?, cause: WebSocketException?, frame: WebSocketFrame?) {}

    override fun onMessageError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        frames: MutableList<WebSocketFrame>?
    ) {}

    override fun onMessageDecompressionError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        compressed: ByteArray?
    ) {}

    override fun onTextMessageError(websocket: WebSocket?, cause: WebSocketException?, data: ByteArray?) {}

    override fun onSendError(websocket: WebSocket?, cause: WebSocketException?, frame: WebSocketFrame?) {}

    override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {}

    override fun handleCallbackError(websocket: WebSocket?, cause: Throwable?) {}

    override fun onSendingHandshake(
        websocket: WebSocket?, requestLine: String?, headers: MutableList<Array<String>>?
    ) {}
}

class SocketEventListener : SocketListener() {

    var disconnectListener: ((uri: URI) -> Unit)? = null
    var onTextMessageReceive: ((text: String) -> Unit)? = null

    override fun onTextMessage(websocket: WebSocket?, text: String?) {
        if (text == null) {
            return
        }
        onTextMessageReceive?.invoke(text)
    }

    override fun onTextMessage(websocket: WebSocket?, data: ByteArray?) {
        if (data == null) {
            return
        }
        onTextMessageReceive?.invoke(String(data))
    }

    override fun onConnectError(websocket: WebSocket?, cause: WebSocketException?) {
        disconnectListener?.invoke(websocket!!.uri)
    }

    override fun onDisconnected(
        websocket: WebSocket?,
        serverCloseFrame: WebSocketFrame?,
        clientCloseFrame: WebSocketFrame?,
        closedByServer: Boolean
    ) {
        disconnectListener?.invoke(websocket!!.uri)
    }

    override fun onSendError(websocket: WebSocket?, cause: WebSocketException?, frame: WebSocketFrame?) {}

    override fun onUnexpectedError(websocket: WebSocket?, cause: WebSocketException?) {}

    override fun handleCallbackError(websocket: WebSocket?, cause: Throwable?) {}

    override fun onError(websocket: WebSocket?, cause: WebSocketException?) {}

    override fun onFrameError(websocket: WebSocket?, cause: WebSocketException?, frame: WebSocketFrame?) {}

    override fun onMessageError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        frames: MutableList<WebSocketFrame>?
    ) {}

    override fun onMessageDecompressionError(
        websocket: WebSocket?,
        cause: WebSocketException?,
        compressed: ByteArray?
    ) {}

    override fun onTextMessageError(websocket: WebSocket?, cause: WebSocketException?, data: ByteArray?) {}
}
