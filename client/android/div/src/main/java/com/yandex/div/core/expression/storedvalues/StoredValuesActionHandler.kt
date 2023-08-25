package com.yandex.div.core.expression.storedvalues

import android.net.Uri
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KAssert

private const val AUTHORITY_SET_STORED_VALUE = "set_stored_value"

private const val PARAM_NAME = "name"
private const val PARAM_VALUE = "value"
private const val PARAM_LIFETIME = "lifetime"
private const val PARAM_TYPE = "type"

internal object StoredValuesActionHandler {

    @JvmStatic
    fun canHandle(authority: String): Boolean = authority == AUTHORITY_SET_STORED_VALUE

    @JvmStatic
    fun handleAction(uri: Uri, view: DivViewFacade): Boolean {
        val div2View = if (view is Div2View) view else null
        if (div2View == null) {
            KAssert.fail { "Handler view is not instance of Div2View" }
            return false
        }

        val name = uri.getParam(forName = PARAM_NAME) ?: return false
        val value = uri.getParam(forName = PARAM_VALUE) ?: return false
        val lifetime = uri.getParam(forName = PARAM_LIFETIME) ?: return false
        val type = uri.getParam(forName = PARAM_TYPE) ?: return false

        val storedValuesController = div2View.div2Component.storedValuesController
        return storedValuesController.setStoredValue(name, value, lifetime, type)
    }

    private fun Uri.getParam(forName: String): String? {
        val param = getQueryParameter(forName)
        if (param == null) {
            KAssert.fail { "The required parameter $forName is missing" }
            return null
        }
        return param
    }

}
