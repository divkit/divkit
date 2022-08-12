package com.yandex.divkit.demo.div

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivDataChangeListener
import com.yandex.div.core.DivStateChangeListener
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.animation.SpringInterpolator
import com.yandex.div.core.state.DivStateTransition
import com.yandex.div.core.view2.Div2View
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.lottie.DivLottieExtensionHandler
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.databinding.ActivityDiv2Binding
import com.yandex.divkit.demo.div.editor.DivEditorActivityStateKeeper
import com.yandex.divkit.demo.div.editor.DivEditorLogger
import com.yandex.divkit.demo.div.editor.DivEditorParsingErrorLogger
import com.yandex.divkit.demo.div.editor.DivEditorPresenter
import com.yandex.divkit.demo.div.editor.DivEditorUi
import com.yandex.divkit.demo.div.editor.DivEditorWebController
import com.yandex.divkit.demo.div.editor.list.DivEditorAdapter
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.demo.utils.coroutineScope
import com.yandex.divkit.demo.utils.loadText
import com.yandex.divkit.demo.utils.longToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val DIV2_VARIABLES = 41
private const val DIV2_BENDER_ITEM = 42
private const val DIV2_DOWNLOAD_PATCH = 43

class Div2Activity : AppCompatActivity() {
    private val preferences by lazy { getSharedPreferences("div2", Context.MODE_PRIVATE) }
    private val globalVariableController = DemoGlobalVariablesController()

    private lateinit var viewBinding: ActivityDiv2Binding

    private lateinit var div2Adapter: DivEditorAdapter
    private lateinit var editorPresenter: DivEditorPresenter
    private lateinit var qrScan: IntentIntegrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityDiv2Binding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.div2RefreshButton.setOnClickListener {
            refresh()
        }
        preferences.getString(DIV2_KEY_URL, "")?.takeIf { it.isNotBlank() }
            ?.let { viewBinding.div2UrlEdit.setText(it) }

        val transitionScheduler = DivParentTransitionScheduler(viewBinding.div2Placeholder)
        val divConfiguration = DivUtils.createDivConfiguration(this, transitionScheduler)
            .extension(
                DivPinchToZoomExtensionHandler(
                    DivPinchToZoomConfiguration.Builder(this).build()
                )
            )
            .extension(DivLottieExtensionHandler())
            .divDataChangeListener(transitionScheduler)
            .actionHandler(TransitionActionHandler(Container.uriHandler))
            .typefaceProvider(YandexSansDivTypefaceProvider(this))
            .displayTypefaceProvider(YandexSansDisplayDivTypefaceProvider(this))
            .build()
        val context = Div2Context(baseContext = this, configuration = divConfiguration)
        globalVariableController.bindWith(context)

        div2Adapter = DivEditorAdapter(context)
        with(viewBinding.div2Recycler) {
            layoutManager = LinearLayoutManager(this@Div2Activity, RecyclerView.VERTICAL, false)
            adapter = div2Adapter
        }

        val divEditorUi = DivEditorUi(
            this,
            viewBinding.div2UrlEdit,
            viewBinding.div2RefreshButton,
            viewBinding.div2Error,
            viewBinding.div2Placeholder,
            viewBinding.div2Recycler,
            div2Adapter,
        )
        val editorLogger = DivEditorLogger(this::setError)
        editorPresenter = DivEditorPresenter(
            this,
            coroutineScope,
            ViewModelProvider(this).get(DivEditorActivityStateKeeper::class.java),
            divEditorUi,
            DivEditorWebController(Container.webSocketFactory.apply {
                proxySettings.apply {
                    val proxyHost = intent.getStringExtra("proxyHost")
                    val proxyPort = intent.getIntExtra("proxyPort", 8080)

                    if (proxyHost != null) {
                        host = proxyHost
                        port = proxyPort
                    }
                }
            }, editorLogger),
            Container.httpClient,
            DivEditorParsingErrorLogger(),
            editorLogger,
            intent.getStringExtra("deviceKey")
        )
        qrScan = IntentIntegrator(this)
        qrScan.setOrientationLocked(false)
        viewBinding.scanQrCodeButton.setOnClickListener {
            qrScan.initiateScan()
        }

        bindLinkAndConnect()
    }

    private fun bindLinkAndConnect() {
        val data = intent?.data ?: return

        viewBinding.div2UrlEdit.setText(data.toString())
        viewBinding.div2RefreshButton.performClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(
            requestCode,
            resultCode,
            data
        ) ?: return super.onActivityResult(requestCode, resultCode, data)
        viewBinding.div2UrlEdit.setText(result.contents ?: "failed, sorry =(")
        if (result.contents.isNullOrEmpty()) {
            viewBinding.div2RefreshButton.performClick()
        }
    }

    private fun refresh() {
        setError("")

        if (editorPresenter.subscribe(url) || editorPresenter.load(url) || editorPresenter.readAsset(url)) {
            preferences.edit().putString(DIV2_KEY_URL, url).apply()
        } else {
            setError(getString(R.string.unhandled_uri_error))
        }

        viewBinding.div2RefreshButton.requestFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, DIV2_BENDER_ITEM, Menu.NONE, "Div2 Bender")
        menu?.add(Menu.NONE, DIV2_VARIABLES, Menu.NONE, "Div2 Variables")
        menu?.add(Menu.FIRST, DIV2_DOWNLOAD_PATCH, Menu.FIRST, "Load patch")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            DIV2_BENDER_ITEM -> {
                Div2BenderActivity.launch(this)
                true
            }
            DIV2_VARIABLES -> {
                showVariablesDialog()
                true
            }
            DIV2_DOWNLOAD_PATCH -> {
                showDownloadDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showVariablesDialog() {
        val editText = EditText(this)
        editText.setText("asset:///div2/expressions/theme_palette_light.json")
        preferences.getString(DIV2_VARIABLES_KEY_URL, "")?.takeIf { it.isNotBlank() }?.let { editText.setText(it) }
        val adb = AlertDialog.Builder(this)
            .setView(editText)
            .setPositiveButton("Load variables") { _, _ ->
                val url = editText.text.toString()
                preferences.edit().putString(DIV2_VARIABLES_KEY_URL, url).apply()
                lifecycleScope.launch {
                    val json = withContext(Dispatchers.IO) { loadJson(Uri.parse(url)) }
                    globalVariableController.updateVariables(json, errorLogger)
                }
            }
        adb.create().show()
    }

    private fun showDownloadDialog() {
        val editText = EditText(this)
        preferences.getString(DIV2_PATCH_KEY_URL, "")?.takeIf { it.isNotBlank() }?.let { editText.setText(it) }
        val adb = AlertDialog.Builder(this)
            .setView(editText)
            .setPositiveButton("Load") { _, _ ->
                val url = editText.text.toString()
                preferences.edit().putString(DIV2_PATCH_KEY_URL, url).apply()
                lifecycleScope.launch {
                    val divPatch = withContext(Dispatchers.IO) {
                        val loadedJson = loadJson(Uri.parse(url))
                        loadedJson.asDivPatchWithTemplates(errorLogger.apply { clear() })
                    }
                    div2Adapter.applyPath(
                        divPatch,
                        errorCallback = { longToast("Error while applied patch!") }
                    )
                }
            }
        adb.create().show()
    }

    @WorkerThread
    private suspend fun loadJson(uri: Uri): JSONObject {
        val data = when (uri.scheme) {
            "http", "https" -> Container.httpClient.loadText(uri.toString())
            "asset" -> loadAsset(uri.path)
            else -> null
        }
        return JSONObject(data)
    }

    /**
     * Path like asset:///div2/transition/change_bounds_curve_src.json
     */
    @WorkerThread
    private fun loadAsset(assetPath: String?): String? {
        if (assetPath == null) {
            return null
        }

        val stream = assets.open(assetPath.trimStart('/'))
        return stream.use {
            stream.bufferedReader().readText()
        }
    }

    private val errorLogger = object : ParsingErrorLogger {
        val parsingErrors = ArrayList<Exception>()
        val hasErrors get() = parsingErrors.isNotEmpty()

        fun prepareMessage() = parsingErrors.flatMap { causes(it) }.joinToString("\n")

        fun clear() = parsingErrors.clear()

        override fun logError(e: Exception) {
            parsingErrors.add(e)
        }

        private fun causes(e: Throwable?, list: MutableList<String> = ArrayList()): List<String> {
            return if (e == null) {
                list.apply { add("-----") }
            } else {
                val prefix = if (list.isNotEmpty()) "Caused by: " else "Error: "
                list.add(prefix + e.message)
                causes(e.cause, list)
            }
        }
    }

    private val url get(): String = viewBinding.div2UrlEdit.text?.toString() ?: ""

    private fun setError(error: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            viewBinding.div2Error.text = error
        }
    }

    private inner class TransitionActionHandler(
        uriHandler: DivkitDemoUriHandler
    ) : DemoDivActionHandler(uriHandler) {

        override fun handleAction(action: DivAction, view: DivViewFacade): Boolean {
            val url = action.url?.evaluate(view.expressionResolver) ?: return false
            if (url.scheme == "div-demo-action" && url.host == "set_data") {
                val assetName = url.getQueryParameter("path")
                editorPresenter.readAsset("asset:///$assetName")
                return true
            }
            return super.handleAction(action, view)
        }
    }

    internal class DivParentTransitionScheduler(
        private val rootView: ViewGroup
    ) : DivDataChangeListener, DivStateChangeListener {

        override fun beforeAnimatedDataChange(divView: Div2View, data: DivData) = scheduleTransition()

        override fun afterAnimatedDataChange(divView: Div2View, data: DivData) = Unit

        override fun onDivAnimatedStateChanged(divView: Div2View) = scheduleTransition()

        private fun scheduleTransition() {
            val transition = DivStateTransition(rootView)
                .setInterpolator(SpringInterpolator())
            TransitionManager.endTransitions(rootView)
            TransitionManager.beginDelayedTransition(rootView, transition)
        }
    }

    private companion object {
        private const val DIV2_KEY_URL = "key_url"
        private const val DIV2_PATCH_KEY_URL = "patch_key_url"
        private const val DIV2_VARIABLES_KEY_URL = "variables_url"
    }
}
