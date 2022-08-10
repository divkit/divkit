package com.yandex.divkit.demo

import android.content.res.Configuration
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.yandex.divkit.demo.databinding.Div2ViewBinding
import com.yandex.divkit.demo.permissions.ActivityPermissionManager
import com.yandex.divkit.demo.permissions.Permission
import com.yandex.divkit.demo.permissions.PermissionRequestBuilder
import com.yandex.divkit.demo.permissions.PermissionRequestCallback
import com.yandex.divkit.demo.permissions.PermissionRequestResult
import com.yandex.divkit.demo.ui.UIDiv2ViewCreator
import com.yandex.divkit.regression.ScenarioLogDelegate

private const val PERMISSION_REQUEST_CODE = 9001

class LauncherActivity : AppCompatActivity() {

    private val permissionManager = ActivityPermissionManager(this)
    private lateinit var binding: Div2ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = Div2ViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val path = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> "application/menu.json"
            Configuration.ORIENTATION_LANDSCAPE -> "application/menu-land.json"
            else -> "application/menu.json"
        }
        val div = UIDiv2ViewCreator(this).createDiv2View(
            this,
            path,
            binding.root,
            ScenarioLogDelegate.Stub
        )

        div.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ).apply {
            weight = 1F
        }

        binding.root.addView(div)
    }
}
