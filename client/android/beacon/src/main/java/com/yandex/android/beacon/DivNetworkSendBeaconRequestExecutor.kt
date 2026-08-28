package com.yandex.android.beacon

import com.yandex.android.net.CookieStorage
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal class DivNetworkSendBeaconRequestExecutor(
    private val networkClient: DivNetworkClient,
) : SendBeaconRequestExecutor {
    override fun execute(request: SendBeaconRequest): SendBeaconResponse {
        try {
            val body = request.payload?.toString()?.toByteArray()
            val networkRequest = DivNetworkRequest.Builder(request.url.toString())
                .method(if (body == null) "GET" else "POST")
                .body(body)
                .contentType(body?.let { "application/json" })
                .apply {
                    request.headers.forEach { (name, value) -> addHeader(name, value) }
                    request.cookieStorage?.getCookies(request.url)?.let {
                        addHeader(COOKIE_HEADER, it)
                    }
                }
                .build()
            val response = runBlocking(Dispatchers.IO) { networkClient.execute(networkRequest) }
            val code = response.use {
                request.cookieStorage.processResponseCookies(
                    it.headers(SET_COOKIE_HEADER),
                    it.url,
                )
                it.code
            }
            return object : SendBeaconResponse {
                override val responseCode: Int = code
                override fun isValid(): Boolean = code in 200..299
            }
        } catch (error: Exception) {
            throw IOException("Failed to execute beacon request [${request.url}]", error)
        }
    }

    private fun CookieStorage?.processResponseCookies(cookies: List<String>, url: String) {
        this?.processCookies(cookies.ifEmpty { null }, url)
    }

    private companion object {
        const val COOKIE_HEADER = "Cookie"
        const val SET_COOKIE_HEADER = "Set-Cookie"
    }
}
