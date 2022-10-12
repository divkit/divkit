package com.yandex.divkit.demo.div

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.core.util.Assert
import com.yandex.div.core.view2.Div2View
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.lottie.DivLottieExtensionHandler
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.databinding.ActivityDiv2ScenarioBinding
import com.yandex.divkit.demo.div.editor.*
import com.yandex.divkit.demo.div.editor.list.DivEditorAdapter
import com.yandex.divkit.demo.div.histogram.LoggingHistogramBridge
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.demo.utils.coroutineScope
import com.yandex.divkit.demo.utils.loadText
import com.yandex.divkit.demo.utils.longToast
import com.yandex.divkit.regression.MetadataBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class Div2ScenarioActivity : AppCompatActivity(), Div2MetadataBottomSheet.MetadataHost {

    private lateinit var binding: ActivityDiv2ScenarioBinding
    private val metadataBottomSheet = Div2MetadataBottomSheet()
    private lateinit var editorPresenter: DivEditorPresenter
    private lateinit var div2Adapter: DivEditorAdapter
    private lateinit var divContext: Div2Context
    private val globalVariableController = DemoGlobalVariablesController()
    private val preferences by lazy { getSharedPreferences("div2", Context.MODE_PRIVATE) }
    private var json: String? = null
    private var url: String? = null
    private lateinit var div2View: Div2View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiv2ScenarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = getString(R.string.demo)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }
        binding.metadataButton.setOnClickListener {
            metadataBottomSheet.showNow(supportFragmentManager, MetadataBottomSheet.TAG)
            (metadataBottomSheet.dialog as BottomSheetDialog).behavior.run {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        json = intent.getStringExtra(JSON)
        url = intent.getStringExtra(URL)

        val transitionScheduler = Div2Activity.DivParentTransitionScheduler(binding.singleContainer)
        val divConfiguration = divConfiguration(this, transitionScheduler)
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

        divContext = divContext(baseContext = this, configuration = divConfiguration)
        div2Adapter = DivEditorAdapter(divContext)
        globalVariableController.bindWith(divContext)

        setupDiv2View()

        with(binding.div2Recycler) {
            layoutManager = LinearLayoutManager(this@Div2ScenarioActivity, RecyclerView.VERTICAL, false)
            adapter = div2Adapter
        }
        val divEditorUi = DivEditorUi(
            this,
            binding.metadataButton,
            binding.error,
            binding.singleContainer,
            div2View,
            binding.div2Recycler,
            div2Adapter,
            this,
            Container.flagPreferenceProvider.getExperimentFlag(Experiment.SHOW_RENDERING_TIME)
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
            intent.getStringExtra("deviceKey"),
            this
        )
        initDiv()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.div2_scenario_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.div2_variables -> {
                showVariablesDialog()
                true
            }
            R.id.div2_download_patch -> {
                showDownloadDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initDiv() {
        if (url != null) {
            refresh()
            return
        }

        json?.let {
            preferences.edit().putString(DIV2_KEY_URL, json).apply()
            coroutineScope.launch {
                editorPresenter.parseDivDataList(JSONObject(it))
            }
            return
        }

        Assert.fail("Incorrect div2ScenarioActivity state")
    }

    private fun refresh() {
        val loadUrl = url ?: return
        setError("")

        if (editorPresenter.subscribe(loadUrl) || editorPresenter.load(loadUrl) || editorPresenter.readAsset(loadUrl)) {
            preferences.edit().putString(DIV2_KEY_URL, url).apply()
        } else {
            setError(getString(R.string.unhandled_uri_error))
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
        val editText = layoutInflater.inflate(R.layout.apply_patch_edit_text, null) as EditText
        preferences.getString(DIV2_PATCH_KEY_URL, "")?.takeIf { it.isNotBlank() }?.let { editText.setText(it) }
        val adb = AlertDialog.Builder(this)
            .setView(editText)
            .setPositiveButton("Apply") { _, _ ->
                try {
                    val divPatch = JSONObject(editText.text.toString())
                        .asDivPatchWithTemplates(errorLogger.apply { clear() })
                    div2Adapter.applyPath(
                        divPatch,
                        errorCallback = { longToast("Error while applied JSON patch!") }
                    )
                } catch (e: JSONException) {
                    val url = editText.text.toString()
                    preferences.edit().putString(DIV2_PATCH_KEY_URL, url).apply()
                    lifecycleScope.launch {
                        val divPatch = withContext(Dispatchers.IO) {
                            val loadedJson = loadJson(Uri.parse(url))
                            loadedJson.asDivPatchWithTemplates(errorLogger.apply { clear() })
                        }
                        div2Adapter.applyPath(
                            divPatch,
                            errorCallback = { longToast("Error while applied loaded patch!") }
                        )
                    }
                }
            }
        adb.create().show()
    }

    private fun setupDiv2View() {
        div2View = Div2View(divContext)
        div2View.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.singleContainer.addView(div2View)
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

    private inner class TransitionActionHandler(
        uriHandler: DivkitDemoUriHandler
    ) : DemoDivActionHandler(uriHandler) {

        override fun handleAction(action: DivAction, view: DivViewFacade): Boolean {
            val url = action.url?.evaluate(view.expressionResolver) ?: return false
            if (url.scheme == "div-demo-action" && url.host == "set_data") {
                val assetName = url.getQueryParameter("path")
                editorPresenter.readAsset("asset:///regression_test_data/$assetName")
                return true
            }
            return super.handleAction(action, view)
        }
    }

    private fun setError(error: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.error.text = error
        }
    }

    private companion object {
        private const val DIV2_PATCH_KEY_URL = "patch_key_url"
        private const val DIV2_VARIABLES_KEY_URL = "variables_url"
    }

    override val renderingTimeMessages: HashMap<String, LoggingHistogramBridge.TimeHistogram> = HashMap()
}
