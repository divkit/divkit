package com.yandex.divkit.demo.div

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.state.DefaultDivStateChangeListener
import com.yandex.div.core.util.SafeAlertDialogBuilder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.font.YandexSansDisplayDivTypefaceProvider
import com.yandex.div.font.YandexSansDivTypefaceProvider
import com.yandex.div.internal.Log
import com.yandex.div.internal.util.IOUtils
import com.yandex.div.lottie.DivLottieExtensionHandler
import com.yandex.div.lottie.DivLottieLogger
import com.yandex.div.shimmer.DivShimmerExtensionHandler
import com.yandex.div.zoom.DivPinchToZoomConfiguration
import com.yandex.div.zoom.DivPinchToZoomExtensionHandler
import com.yandex.divkit.demo.R
import com.yandex.divkit.demo.databinding.SamplesActivityBinding
import com.yandex.divkit.demo.utils.DivkitDemoPermissionHelper
import com.yandex.divkit.demo.utils.lifecycleOwner
import com.yandex.divkit.demo.utils.showToast
import org.json.JSONObject

open class DivActivity : AppCompatActivity() {

    private enum class DivFolder(val path: String) {
        DIV2_SAMPLES("samples")
    }

    private val adapter by lazy { createAdapter() }
    private val fileStorage by lazy { DivFileStorage() }

    private var lastRequestedPermissionAction: (() -> Unit)? = null
    private lateinit var binding: SamplesActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SamplesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView.children.forEach { view ->
                    (view as? Div2View)?.tryLogVisibility()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val scrollY = binding.scrollView.scrollY
                if (scrollY > 500) {
                    binding.recycler.setBackgroundColor(Color.WHITE)
                } else {
                    binding.recycler.setBackgroundResource(R.drawable.rounded_top_corners)
                }
            }
        })
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = getString(R.string.samples)
        val config: Configuration = resources.configuration
        val icon = if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            R.drawable.ic_back_rtl
        } else {
            R.drawable.ic_back
        }
        binding.toolbar.setNavigationIcon(icon)
        binding.toolbarLayout.setCollapsedTitleTextColor(Color.BLACK)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        addAll()

        // required for DivReceiver
        DivkitDemoPermissionHelper.requestReadExternalStoragePermission(this, R.string.permission_rationale_read_external_storage)
    }

    private fun createAdapter(): DivViewAdapter {
        val stateChangeListener = DefaultDivStateChangeListener(rootViewProvider = { binding.recycler })

        val logger = object : DivLottieLogger {
            override fun log(message: String) {
                android.util.Log.d("4444", message)
            }

            override fun fail(message: String, throwable: Throwable?) {
                android.util.Log.e("4444", message, throwable)
            }
        }
        val configuration = divConfiguration(this)
            .divStateChangeListener(stateChangeListener)
            .extension(DivPinchToZoomExtensionHandler(DivPinchToZoomConfiguration.Builder(this).build()))
            .extension(DivLottieExtensionHandler(DemoDivLottieRawResProvider, logger))
            .extension(DivShimmerExtensionHandler())
            .typefaceProvider(YandexSansDivTypefaceProvider(this))
            .additionalTypefaceProviders(mapOf("display" to YandexSansDisplayDivTypefaceProvider(this)))
            .build()
        val context = divContext(
            baseContext = this,
            configuration = configuration,
            lifecycleOwner = lifecycleOwner
        )

        return DivViewAdapter(context)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.map { it == PackageManager.PERMISSION_GRANTED}.reduce { acc, it -> acc && it}) {
            lastRequestedPermissionAction?.invoke()
            lastRequestedPermissionAction = null
        } else {
            noPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> data?.data?.path?.let { loadDivJsonPath(it) } ?: showToast("No json path data")
            else -> showToast("Unsupported requestCode: $requestCode")
        }
    }

    protected fun addItemFromAsset(assetName: String, path: String) {
        val assetFullName = if (!TextUtils.isEmpty(path)) "$path/$assetName" else assetName
        val stream = assets.open(assetFullName)
        addItemFromJson(IOUtils.toString(stream))
    }

    private fun addItemFromJson(jsonString: String) = JSONObject(jsonString).let { addItemFromJson(it) }

    private fun addItemFromJson(json: JSONObject) =
        try {
            adapter.addFromJson(json)
            binding.recycler.scrollToPosition(0)
        } catch (e: Exception) {
            Log.e("DivActivity", "Bad div json: ", e)
            Toast.makeText(this@DivActivity, R.string.bad_div_json, Toast.LENGTH_SHORT).show()
        }

    private fun addAll() {
        adapter.removeAll()
        listAssetsRecursively(DivFolder.DIV2_SAMPLES.path).reversed().forEach { addItemFromAsset(it, DivFolder.DIV2_SAMPLES.path) }
    }

    private fun loadDivJsonPath(path: String) =
            {
                try {
                    loadDivJsonResult(fileStorage.readJsonFromFile(path))
                } catch (e: Exception) {
                    showToast("Error when loading $path :'(")
                    Log.e("DivActivity", "Error when loading", e)
                }
            }.invokeWithPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)

    private fun loadDivJsonResult(result: JsonResult) =
            when (result) {
                is JsonResult.Array -> adapter.fromJson(result.data)
                is JsonResult.Object -> addItemFromJson(result.data)
                JsonResult.Malformed -> showToast("Malformed json :'(")
            }

    private fun (()->Unit).invokeWithPermissions(vararg permissions: String) =
            when {
                checkPermissions(permissions) ->
                    invoke()
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                    requestPermissions(permissions, REQUEST_CODE)
                else ->
                    noPermissions()
            }

    private fun checkPermissions(permissions: Array<out String>) =
            permissions
                    .map { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
                    .reduce { acc, it -> acc && it}

    private fun noPermissions() {
        SafeAlertDialogBuilder(this)
            .setTitle("Unable to add new template from storage: no permissions")
            .setPositiveButton("Ok") {
                    dialog, id ->  dialog.cancel()
            }
    }

    private fun listAssetsRecursively(baseFolder: String, relativePath: String = ""): List<String> =
        assets.list(baseFolder.subPath(relativePath))
            ?.flatMap { fileName ->
                val relativePathToFile = relativePath.subPath(fileName)
                listAssetsRecursively(baseFolder, relativePathToFile).ifEmpty {
                    listOf(relativePathToFile)
                }
            } ?: emptyList()

    private fun String.subPath(relativePath: String): String {
        return when {
            isEmpty() -> relativePath
            relativePath.isEmpty() -> this
            else -> "$this/$relativePath"
        }
    }

    companion object {
        const val REQUEST_CODE = 49
    }
}
