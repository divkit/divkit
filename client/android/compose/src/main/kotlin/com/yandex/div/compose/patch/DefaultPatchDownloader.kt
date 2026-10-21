package com.yandex.div.compose.patch

import android.net.Uri
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.dagger.Names
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.readBytes
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div2.DivPatch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@DivContextScope
internal class DefaultPatchDownloader @Inject constructor(
    @Named(Names.IO_DISPATCHER)
    private val ioDispatcher: CoroutineDispatcher,
    private val networkClient: DivNetworkClient,
    private val reporter: DivReporter,
) : DivPatchDownloader {

    override suspend fun downloadPatch(url: Uri): DivPatch = withContext(ioDispatcher) {
        val request = DivNetworkRequest.Builder(url.toString()).build()
        networkClient.execute(request).use { response ->
            if (!response.isSuccessful) {
                throw IOException("Patch request failed: HTTP ${response.code}")
            }

            val body = response.body ?: throw IOException("Empty patch response")
            val json = JSONObject(body.readBytes().decodeToString())
            val environment = DivParsingEnvironment({ reporter.reportError(it) })
            json.optJSONObject("templates")?.let { environment.parseTemplates(it) }
            DivPatch(environment, json.getJSONObject("patch"))
        }
    }
}
