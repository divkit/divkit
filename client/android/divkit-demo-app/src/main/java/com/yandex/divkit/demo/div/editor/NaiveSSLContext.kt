package com.yandex.divkit.demo.div.editor

import java.lang.RuntimeException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Escaping ssl issues on oreo+ devices.
 */
object NaiveSSLContext {

    @Throws(NoSuchAlgorithmException::class)
    fun getInstance(protocol: String?): SSLContext {
        val context: SSLContext = SSLContext.getInstance(protocol)
        try {
            context.init(null, arrayOf<TrustManager>(NaiveTrustManager()), null)
        } catch (e: KeyManagementException) {
            throw RuntimeException("Failed to initialize an SSLContext.", e)
        }
        return context
    }

    internal class NaiveTrustManager : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
    }
}