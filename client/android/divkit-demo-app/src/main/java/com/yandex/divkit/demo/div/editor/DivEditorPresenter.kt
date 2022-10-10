package com.yandex.divkit.demo.div.editor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import com.neovisionaries.ws.client.WebSocketException
import com.yandex.div2.DivData
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.div.Div2MetadataBottomSheet
import com.yandex.divkit.demo.div.editor.DivEditorMessageUtils.addExceptions
import com.yandex.divkit.demo.div.editor.DivEditorMessageUtils.addMetadata
import com.yandex.divkit.demo.div.isDiv2Data
import com.yandex.divkit.demo.div.parseToDiv2List
import com.yandex.divkit.demo.utils.loadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.jetbrains.anko.displayMetrics
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.UnknownHostException
import kotlin.math.roundToInt

private const val TARGET_DENSITY = 1.5f
private const val JPG_COMPRESS_QUALITY = 80
private const val JSON_PARAM = "json"
private const val ERROR_TEXT_PARAM = "text"
private const val WEB_EDITOR_URL = "divview"
const val DEMO_ACTIVITY_COMPONENT_NAME = "com.yandex.divkit.demo.div.Div2ScenarioActivity"

const val DIV_RENDER_TOTAL = "Div.Render.Total"
const val DIV_PARSING_DATA = "Div.Parsing.Data"
const val DIV_PARSING_TEMPLATES = "Div.Parsing.Templates"

class DivEditorPresenter(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val stateKeeper: DivEditorActivityStateKeeper,
    private val divEditorUi: DivEditorUi,
    private val webController: DivEditorWebController,
    private val httpClient: OkHttpClient,
    private val errorLogger: DivEditorParsingErrorLogger,
    private val logger: DivEditorLogger,
    private val deviceKey: String?,
    private val metadataHost: Div2MetadataBottomSheet.MetadataHost,
) {

    init {
        divEditorUi.onDiv2ViewDrawnListener = this::sendUiState
        updateState(stateKeeper.state)
        webController.addMessageListener(MessageType.JSON_MESSAGE, this::receiveDivMessage)
        webController.addMessageListener(MessageType.ERROR, this::receiveErrorMessage)
    }

    /**
     * @return false if not gonna handle [userInput].
     */
    fun subscribe(userInput: String?): Boolean {
        if (userInput.isNullOrBlank() || !userInput.contains(WEB_EDITOR_URL)) {
            return false
        }

        updateState(DivEditorState.LoadingState)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    webController.connect(userInput)
                    val uuid: String = Uri.parse(userInput).getQueryParameter("uuid")
                        ?: throw IllegalArgumentException("failed to retrieve uuid from $userInput")
                    val listenPayload = JSONObject()
                        .put("uuid", uuid)
                        .addMetadata(context)
                    webController.sendMessage(MessageType.LISTEN_MESSAGE, listenPayload)
                } catch (e: IllegalArgumentException) {
                    updateFailedState("Uri $userInput violates RFC 2396.", e)
                } catch (e: IOException) {
                    updateFailedState("Failed to create a socket for uri: $userInput", e)
                } catch (e: WebSocketException) {
                    updateFailedState("Failed to create a socket for uri: $userInput", e)
                } catch (e: UnknownHostException) {
                    val message = "Failed to load data for uri: $userInput\n" +
                        context.getString(R.string.unknown_host_error)
                    updateFailedState(message, e)
                } catch (e: Exception) {
                    updateFailedState("Failed to connect to $userInput", e)
                }
            }
        }
        return true
    }

    fun load(userInput: String): Boolean {
        if (userInput.isBlank()) {
            return false
        }

        val uri = Uri.parse(userInput)
        if (uri.scheme != "http" && uri.scheme != "https") {
            return false
        }

        updateState(DivEditorState.LoadingState)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val data = httpClient.loadText(uri.toString())!!
                    parseDivDataList(JSONObject(data))
                } catch (e: UnknownHostException) {
                    val message = "Failed to load data for uri: $userInput\n" +
                        context.getString(R.string.unknown_host_error)
                    updateFailedState(message, e)
                } catch (e: Exception) {
                    updateFailedState("Failed to load data for uri: $userInput", e)
                }
            }
        }
        return true
    }

    fun readAsset(userInput: String): Boolean {
        if (userInput.isBlank()) {
            return false
        }

        val uri = Uri.parse(userInput)
        if (uri.scheme != "asset") {
            return false
        }
        val assetPath = uri.path ?: return false

        updateState(DivEditorState.LoadingState)
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val stream = context.assets.open(assetPath.trimStart('/'))
                    val data = stream.use {
                        stream.bufferedReader().readText()
                    }
                    parseDivDataList(JSONObject(data))
                } catch (e: Exception) {
                    updateFailedState("Failed to read asset for uri: $userInput", e)
                }
            }
        }
        return true
    }

    @WorkerThread
    private fun receiveDivMessage(message: JSONObject) {
        val json = message.opt(JSON_PARAM) as? JSONObject
        if (json == null) {
            updateFailedState("there's no \"$JSON_PARAM\" field in received message")
            return
        }
        errorLogger.reset()
        parseDivDataList(json)
    }

    @WorkerThread
    fun parseDivDataList(json: JSONObject) {
        val divDataList: List<DivData>? = try {
            divEditorUi.hasTemplates = (json.has("templates"))
            json.parseToDiv2List(errorLogger, DEMO_ACTIVITY_COMPONENT_NAME)
        } catch (e: Exception) {
            errorLogger.logError(e)
            null
        }
        if (divDataList != null) {
            val isSingleCard = json.isDiv2Data()
            updateState(DivEditorState.DivReceivedState(divDataList, json, isSingleCard))
            logger.log("Successfully parsed")
        } else {
            updateFailedState("Parsing failed, reason: ${errorLogger.parsingExceptions().lastOrNull()?.message}")
        }
    }

    private fun receiveErrorMessage(message: JSONObject) {
        val errorText = message.optString(
            ERROR_TEXT_PARAM,
            "there's no \"$ERROR_TEXT_PARAM\" field in received error message"
        )
        updateFailedState(errorText)
    }

    private fun sendUiState(bitmap: Bitmap?) {
        if (!webController.isConnected()) {
            return
        }
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val encodedImage: String = try {
                    bitmap.toBase64(context)
                } catch (e: Exception) {
                    logger.logError("failed to encode screenshot to base 64", e)
                    ""
                }
                try {
                    val message = JSONObject()
                        .addMetadata(context)
                        .addExceptions(errorLogger.parsingExceptions())
                        .put("device_key", deviceKey)
                        .put("screenshot", encodedImage)
                        .put("rendering_time", JSONObject().apply {
                            put("div_render_total", metadataHost.renderingTimeMessages
                                .getValue(DIV_RENDER_TOTAL).toJSONObject())
                            put("div_parsing_data", metadataHost.renderingTimeMessages
                                .getValue(DIV_PARSING_DATA).toJSONObject())
                            if (metadataHost.renderingTimeMessages.containsKey(DIV_PARSING_TEMPLATES)) {
                                put("div_parsing_templates", metadataHost.renderingTimeMessages
                                    .getValue(DIV_PARSING_TEMPLATES).toJSONObject())
                            }
                        })
                    webController.sendMessage(MessageType.UI_STATE, message)
                } catch (e: Exception) {
                    logger.logError("failed send uiState", e)
                }
            }
        }
    }

    @AnyThread
    private fun updateState(newState: DivEditorState) {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                stateKeeper.state = newState
                divEditorUi.updateState(newState)
            }
        }
    }

    @AnyThread
    private fun updateFailedState(message: String, throwable: Throwable? = null) {
        throwable?.printStackTrace()
        val errorMessage = message + (throwable?.let { "\nStacktrace is in logcat" } ?: "")
        val failedState = DivEditorState.FailedState(errorMessage)
        logger.logError(message, throwable)
        updateState(failedState)
    }
}

private fun Bitmap?.toBase64(context: Context): String {
    if (this == null) {
        return ""
    }
    val byteArrayOutputStream = ByteArrayOutputStream()
    val compressed = scaleDown(context)
    compressed.compress(Bitmap.CompressFormat.JPEG, JPG_COMPRESS_QUALITY, byteArrayOutputStream)
    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

fun Bitmap.scaleDown(context: Context): Bitmap {
    val density = context.displayMetrics.density

    val scale = density / TARGET_DENSITY

    return if (scale <= 1f) {
        this
    } else {
        val width = (width / scale).roundToInt()
        val height = (height / scale).roundToInt()
        Bitmap.createScaledBitmap(this, width, height, true)
    }
}
