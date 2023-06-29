package com.yandex.divkit.demo.div

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Path
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.PathInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.transition.TransitionManager
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivDataChangeListener
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.state.DivStateChangeListener
import com.yandex.div.core.state.DivStateTransition
import com.yandex.div.core.util.SafeAlertDialogBuilder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.Variable
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.internal.Log
import com.yandex.div.lottie.DivLottieExtensionHandler
import com.yandex.div.shimmer.DivShimmerExtensionHandler
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.databinding.ActivityDiv2Binding
import com.yandex.divkit.demo.div.editor.DEMO_ACTIVITY_COMPONENT_NAME
import com.yandex.divkit.demo.div.editor.list.DivEditorAdapter
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.divkit.demo.ui.SCHEME_DIV_ACTION
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.demo.utils.showToast
import org.json.JSONObject
import java.net.URL

private const val AUTHORITY_DEMO_ACTIVITY = "demo_activity"
private const val PARAM_ACTION = "action"
private const val OPEN_FILE = "open_file"
private const val QR_CODE = "open_qr"
private const val PASTE = "paste"
private const val SHOW_RESULT = "show_result"

const val URL = "url"
const val JSON = "json"
const val DIV2_KEY_URL = "key_url"

private const val DIV2_TEXT_INPUT_VARIABLE = "div2activity_text"
private const val OPEN_FILE_ACTIVITY_REQUEST_CODE = 102

class Div2Activity : AppCompatActivity() {
    private val preferences by lazy { getSharedPreferences("div2", Context.MODE_PRIVATE) }
    private val globalVariableController = DemoGlobalVariablesController()

    private lateinit var binding: ActivityDiv2Binding
    private lateinit var div: Div2View

    private lateinit var div2Adapter: DivEditorAdapter
    private lateinit var qrScan: IntentIntegrator
    private lateinit var variable: Variable.StringVariable
    private val fileStorage by lazy { DivFileStorage() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiv2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = getString(R.string.demo)
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        val transitionScheduler = DivParentTransitionScheduler(binding.container)
        val divConfiguration = divConfiguration(this)
            .extension(
                DivPinchToZoomExtensionHandler(
                    DivPinchToZoomConfiguration.Builder(this).build()
                )
            )
            .extension(DivLottieExtensionHandler())
            .extension(DivShimmerExtensionHandler())
            .divStateChangeListener(transitionScheduler)
            .divDataChangeListener(transitionScheduler)
            .actionHandler(Div2ActionHandler(Container.uriHandler))
            .typefaceProvider(YandexSansDivTypefaceProvider(this))
            .additionalTypefaceProviders(mapOf("display" to YandexSansDisplayDivTypefaceProvider(this)))
            .build()
        val context = divContext(baseContext = this, configuration = divConfiguration)

        val prevValue = preferences.getString(DIV2_KEY_URL, "")?.takeIf { it.isNotBlank() } ?: ""
        variable = Variable.StringVariable(DIV2_TEXT_INPUT_VARIABLE, prevValue)
        globalVariableController.bindWith(context)
        globalVariableController.putOrUpdate(variable)

        div2Adapter = DivEditorAdapter(context)
        val divJson = DivAssetReader(context).read("application/demo.json")
        val divData = divJson.asDiv2DataWithTemplates(componentName = DEMO_ACTIVITY_COMPONENT_NAME)
        val div = Div2View(context).apply {
            setData(divData, DivDataTag("div2"))
        }

        binding.container.addView(div)
        div.layoutParams.width = MATCH_PARENT
        div.layoutParams.height = MATCH_PARENT

        qrScan = IntentIntegrator(this)
        qrScan.setOrientationLocked(false)

        bindLink()
    }

    private fun bindLink() {
        val data = intent?.data ?: return

        variable.setValue(Variable.StringVariable(DIV2_TEXT_INPUT_VARIABLE, data.toString()))
        checkInputAndShowIfCorrect()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(
            requestCode,
            resultCode,
            data
        ) ?: return super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPEN_FILE_ACTIVITY_REQUEST_CODE -> {
                data?.data?.path?.let { loadDivJsonPath(it) } ?: showToast("No json path data")
            }
            else -> {
                result.contents?.let {
                    variable.setValue(Variable.StringVariable(DIV2_TEXT_INPUT_VARIABLE, result.contents))
                }
                if (result.contents.isNullOrEmpty()) {
                    variable.setValue(Variable.StringVariable(DIV2_TEXT_INPUT_VARIABLE, "sorry, fail =("))
                }
            }
        }
    }

    private inner class Div2ActionHandler(
        uriHandler: DivkitDemoUriHandler
    ) : DemoDivActionHandler(uriHandler) {

        override fun handleAction(action: DivAction, view: DivViewFacade): Boolean {
            val url = action.url?.evaluate(view.expressionResolver) ?: return false
            return handleDemoActionUrl(url) || super.handleAction(action, view)
        }

        private fun handleDemoActionUrl(uri: Uri): Boolean {
            if (uri.authority != AUTHORITY_DEMO_ACTIVITY || uri.scheme != SCHEME_DIV_ACTION) return false

            when (uri.getQueryParameter(PARAM_ACTION)) {
                OPEN_FILE -> loadDivJson()
                QR_CODE -> qrScan.initiateScan()
                PASTE -> pasteFromClipboard()
                SHOW_RESULT -> checkInputAndShowIfCorrect()
                else -> return false
            }
            return true
        }
    }

    internal class DivParentTransitionScheduler(
        private val rootView: ViewGroup
    ) : DivDataChangeListener, DivStateChangeListener {

        private val interpolator = PathInterpolator(
            Path().apply { cubicTo(0.25f, 0.1f, 0.25f, 1.0f, 1.0f, 1.0f) }
        )

        override fun beforeAnimatedDataChange(divView: Div2View, data: DivData) = scheduleTransition()

        override fun afterAnimatedDataChange(divView: Div2View, data: DivData) = Unit

        override fun onDivAnimatedStateChanged(divView: Div2View) = scheduleTransition()

        private fun scheduleTransition() {
            val transition = DivStateTransition(rootView).setInterpolator(interpolator)
            TransitionManager.endTransitions(rootView)
            TransitionManager.beginDelayedTransition(rootView, transition)
        }
    }

    private fun pasteFromClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val paste = clipboard?.primaryClip?.getItemAt(0)?.text
        if (paste == null) {
            Toast.makeText(this@Div2Activity, "Clipboard is empty", Toast.LENGTH_LONG).show()
        } else {
            variable.setValue(Variable.StringVariable(DIV2_TEXT_INPUT_VARIABLE, paste.toString()))
        }
    }

    private fun checkInputAndShowIfCorrect() {
        val input = variable.getValue().toString()

        try {
            URL(input).toURI()
            startActivity(Intent(this@Div2Activity,
                Div2ScenarioActivity::class.java).apply {
                putExtra(URL, input)
            })
            return
        } catch (ignored: Exception){}

        try {
            JSONObject(input)
            startActivity(Intent(this@Div2Activity,
                Div2ScenarioActivity::class.java).apply {
                putExtra(JSON, input)
            })
            return
        } catch (ignored: Exception){}

        Toast.makeText(this, "Incorrect URL or JSON", Toast.LENGTH_LONG)
            .show()
    }

    private fun loadDivJson() =
        Intent(Intent.ACTION_GET_CONTENT)
            .apply { type = "file/*.json" }
            .let { intent ->
                try {
                    startActivityForResult(intent, OPEN_FILE_ACTIVITY_REQUEST_CODE)
                } catch (e: ActivityNotFoundException) {
                    showToast("No one can handle open json intent, use Google Play to install File Manager")
                }
            }

    private fun loadDivJsonPath(path: String) =
        {
            try {
                variable.setValue(
                    Variable.StringVariable(DIV2_TEXT_INPUT_VARIABLE,
                        fileStorage.readJsonFromFile(path).toString())
                )
            } catch (e: Exception) {
                showToast("Error when loading $path :'(")
                Log.e("Div2Activity", "Error when loading path", e)
            }
        }.invokeWithPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun (() -> Unit).invokeWithPermissions(vararg permissions: String) =
        when {
            checkPermissions(permissions) -> invoke()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                requestPermissions(permissions, OPEN_FILE_ACTIVITY_REQUEST_CODE)
            else -> SafeAlertDialogBuilder(this@Div2Activity)
                .setTitle("Unable to add new template from storage: no permissions")
                .setPositiveButton("Ok") { dialog, id ->
                    dialog.cancel()
                }
        }

    private fun checkPermissions(permissions: Array<out String>) =
        permissions
            .map { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
            .reduce { acc, it -> acc && it }
}
