package com.yandex.divkit.demo.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.action.DivActionInfo
import com.yandex.div2.DivAction
import com.yandex.divkit.demo.div.DemoDivActionHandler
import com.yandex.divkit.demo.div.Div2Activity
import com.yandex.divkit.demo.div.DivActivity
import com.yandex.divkit.demo.settings.SettingsActionHandler
import com.yandex.divkit.demo.settings.SettingsActivity
import com.yandex.divkit.demo.utils.DivkitDemoUriHandler
import com.yandex.divkit.regression.RegressionActivity

private const val AUTHORITY_OPEN_SCREEN = "open_screen"

const val SCHEME_DIV_ACTION = "div-action"

private const val ACTIVITY_DEMO = "demo"
private const val ACTIVITY_SAMPLES = "samples"
private const val ACTIVITY_REGRESSION = "regression"
private const val ACTIVITY_SETTINGS = "settings"

private const val PARAM_ACTIVITY = "activity"

class UIDiv2ActionHandler(
    uriHandler: DivkitDemoUriHandler,
    private val context: Context
) : DemoDivActionHandler(uriHandler) {

    override fun handleAction(info: DivActionInfo, view: DivViewFacade): Boolean {
        val uri = info.url ?: return false
        return (handleActivityActionUrl(uri) || SettingsActionHandler.handleActionUrl(uri)
                || super.handleAction(info, view))
    }

    private fun handleActivityActionUrl(uri: Uri): Boolean {

        if (uri.authority != AUTHORITY_OPEN_SCREEN || uri.scheme != SCHEME_DIV_ACTION) return false

        when (uri.getQueryParameter(PARAM_ACTIVITY)) {
            ACTIVITY_DEMO -> startActivityAction(Div2Activity::class.java)
            ACTIVITY_REGRESSION -> startActivityAction(RegressionActivity::class.java)
            ACTIVITY_SAMPLES -> startActivityAction(DivActivity::class.java)
            ACTIVITY_SETTINGS -> startActivityAction(SettingsActivity::class.java)
            else -> return false
        }
        return true
    }

    private fun startActivityAction(klass: Class<out Activity>) {
        startActivity(context, Intent(context, klass), null)
    }
}
