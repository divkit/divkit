package com.yandex.divkit.demo.div

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.core.util.SafeAlertDialogBuilder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.internal.Assert
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.lottie.DivLottieExtensionHandler
import com.yandex.div.shimmer.DivShimmerExtensionHandler
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.div2.DivAction
import com.yandex.div2.DivPatch
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.databinding.ActivityDiv2ScenarioBinding
import com.yandex.divkit.demo.div.editor.DivEditorActivityStateKeeper
import com.yandex.divkit.demo.div.editor.DivEditorLogger
import com.yandex.divkit.demo.div.editor.DivEditorParsingErrorLogger
import com.yandex.divkit.demo.div.editor.DivEditorPresenter
import com.yandex.divkit.demo.div.editor.DivEditorState
import com.yandex.divkit.demo.div.editor.DivEditorUi
import com.yandex.divkit.demo.div.editor.DivEditorWebController
import com.yandex.divkit.demo.div.editor.list.DivEditorAdapter
import com.yandex.divkit.demo.div.histogram.LoggingHistogramBridge
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.demo.utils.coroutineScope
import com.yandex.divkit.demo.utils.lifecycleOwner
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
    private lateinit var editorStateKeeper: DivEditorActivityStateKeeper
    private lateinit var div2Adapter: DivEditorAdapter
    private lateinit var divContext: Div2Context
    private val actionHandler = DemoDivActionHandler(Container.uriHandler.apply { handlingActivity = this@Div2ScenarioActivity })
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
        val config: Configuration = resources.configuration
        val icon = if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            R.drawable.ic_back_rtl
        } else {
            R.drawable.ic_back
        }
        binding.toolbar.setNavigationIcon(icon)
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
        val divConfiguration = divConfiguration(this)
            .extension(
                DivPinchToZoomExtensionHandler(
                    DivPinchToZoomConfiguration.Builder(this).build()
                )
            )
            .actionHandler(actionHandler)
            .extension(DivLottieExtensionHandler())
            .extension(DivShimmerExtensionHandler())
            .extension(CheckBoundsExtensionHandler())
            .divStateChangeListener(transitionScheduler)
            .divDataChangeListener(transitionScheduler)
            .actionHandler(TransitionActionHandler(Container.uriHandler))
            .typefaceProvider(YandexSansDivTypefaceProvider(this))
            .additionalTypefaceProviders(mapOf("display" to YandexSansDisplayDivTypefaceProvider(this)))
            .build()

        divContext = divContext(
            baseContext = this,
            configuration = divConfiguration,
            lifecycleOwner = lifecycleOwner
        )
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
        editorStateKeeper = ViewModelProvider(this)[DivEditorActivityStateKeeper::class.java]
        editorPresenter = DivEditorPresenter(
            this,
            coroutineScope,
            editorStateKeeper,
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
            R.id.div2_perform_action -> {
                showActionDialog()
                true
            }
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

    private fun showActionDialog() {
        val editText = layoutInflater.inflate(R.layout.apply_patch_edit_text, null) as EditText
        editText.setText("div-action://set_variable?name=var&value=1")
        preferences.getString(KEY_DIV2_ACTION_URL, "")?.takeIf { it.isNotBlank() }?.let { editText.setText(it) }
        val adb = SafeAlertDialogBuilder(this)
            .setView(editText)
            .setPositiveButton("Perform action") { _, _ ->
                val action = editText.text.toString()
                preferences.edit().putString(KEY_DIV2_ACTION_URL, action).apply()
                actionHandler.handleActionUrl(Uri.parse(action), div2View)
            }
        adb.create().show()
    }

    private fun showVariablesDialog() {
        val editText = layoutInflater.inflate(R.layout.apply_patch_edit_text, null) as EditText
        editText.setText("asset:///div2/expressions/theme_palette_light.json")
        preferences.getString(KEY_DIV2_VARIABLES_URL, "")?.takeIf { it.isNotBlank() }?.let { editText.setText(it) }
        val adb = SafeAlertDialogBuilder(this)
            .setView(editText)
            .setPositiveButton("Load variables") { _, _ ->
                val data = editText.text.toString()
                preferences.edit().putString(KEY_DIV2_VARIABLES_URL, data).apply()
                lifecycleScope.launch {
                    val json = if (data.trimStart().startsWith('{')) {
                        JSONObject(data)
                    } else {
                        val url = Uri.parse(data)
                        withContext(Dispatchers.IO) { loadJson(url) }
                    }
                    globalVariableController.updateVariables(json, errorLogger)
                }
            }
        adb.create().show()
    }

    private fun showDownloadDialog() {
        val editText = layoutInflater.inflate(R.layout.apply_patch_edit_text, null) as EditText
        preferences.getString(KEY_DIV2_PATCH_URL, "")?.takeIf { it.isNotBlank() }?.let { editText.setText(it) }
        val adb = SafeAlertDialogBuilder(this)
            .setView(editText)
            .setPositiveButton("Apply") { _, _ ->
                val fieldValue = editText.text.toString()
                preferences.edit().putString(KEY_DIV2_PATCH_URL, fieldValue).apply()

                lifecycleScope.launch {
                    val divPatch = try {
                        if (!fieldValue.trimStart().startsWith('{')) {
                            withContext(Dispatchers.IO) {
                                loadJson(Uri.parse(fieldValue))
                            }
                        } else {
                            JSONObject(fieldValue)
                        }.asDivPatchWithTemplates(errorLogger.apply { clear() })
                    } catch (e: JSONException) {
                        longToast("Error while parsing patch!")
                        return@launch
                    } catch (e: Exception) {
                        longToast("Error while downloading patch!")
                        return@launch
                    }

                    applyPatch(divPatch) {
                        longToast("Error while applying patch!")
                    }
                }
            }
        adb.create().show()
    }

    private fun applyPatch(divPatch: DivPatch, errorCallback: () -> Unit) {
        if (isSingleCardState()) {
            if (!div2View.applyPatch(divPatch)) {
                errorCallback()
            }
            return
        }
        div2Adapter.applyPath(divPatch, errorCallback)
    }

    private fun isSingleCardState(): Boolean {
        if (!this::editorStateKeeper.isInitialized) {
            return false
        }
        val state = editorStateKeeper.state
        return state is DivEditorState.DivReceivedState && state.isSingleCard
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
            val url = action.url?.evaluate(view.expressionResolver)
                ?: return super.handleAction(action, view)
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
        private const val KEY_DIV2_PATCH_URL = "patch_url"
        private const val KEY_DIV2_VARIABLES_URL = "variables_url"
        private const val KEY_DIV2_ACTION_URL = "action_url"
    }

    override val renderingTimeMessages: HashMap<String, LoggingHistogramBridge.TimeHistogram> = HashMap()
}
