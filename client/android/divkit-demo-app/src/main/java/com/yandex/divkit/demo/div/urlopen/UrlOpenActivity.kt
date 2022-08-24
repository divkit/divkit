package com.yandex.divkit.demo.div.urlopen

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.divkit.demo.utils.longToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivityForResult
import java.io.IOException

/**
 * Activity to open jsons from urls
 */
class UrlOpenActivity : AppCompatActivity(), CoroutineScope {

    private val opener = UrlOpener()

    private val activityScopeJob = Job()
    private var job: Job? = null

    override val coroutineContext = Dispatchers.Main + activityScopeJob

    private val ui = UrlOpenActivityUi(scope = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        ui.onUrlSelect = ::onUrlSelect
        ui.onCancel = ::cancel
    }

    override fun onDestroy() {
        super.onDestroy()

        job?.cancel()
    }

    private suspend fun onUrlSelect(url: String) {
        try {
            val intent = this.intent
            intent.putExtra(RESULT_EXTRA, opener.open(url))
            setResult(RESULT_OK, intent)
            finish()
        } catch (e: IOException) {
            longToast("Error: ${e.message}")
            cancel()
        }
    }

    private fun cancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    companion object {

        const val RESULT_EXTRA = "OPENED_DATA"

    }
}
