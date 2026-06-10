package com.yandex.div.core.expression.storedvalues

import android.net.Uri
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.StoredValue
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.storedvalues.StoredValueParser
import com.yandex.div.internal.storedvalues.toStoredValueScope
import com.yandex.div2.DivActionSetStoredValue

private const val AUTHORITY_SET_STORED_VALUE = "set_stored_value"

private const val PARAM_NAME = "name"
private const val PARAM_VALUE = "value"
private const val PARAM_LIFETIME = "lifetime"
private const val PARAM_TYPE = "type"
private const val PARAM_SCOPE = "scope"

internal object StoredValuesActionHandler {

    @JvmStatic
    fun canHandle(authority: String?): Boolean = authority == AUTHORITY_SET_STORED_VALUE

    @JvmStatic
    fun handleAction(uri: Uri, view: DivViewFacade) {
        val div2View = view as? Div2View ?: run {
            KAssert.fail { "Handler view is not instance of Div2View" }
            return
        }

        val name = uri.getParam(forName = PARAM_NAME) ?: return
        val value = uri.getParam(forName = PARAM_VALUE) ?: return
        val lifetime = uri.getParam(forName = PARAM_LIFETIME)?.toLongOrNull() ?: return
        val type = uri.getParam(forName = PARAM_TYPE)
            ?.run { StoredValue.Type.fromString(this) }
            ?: return
        val scope = uri.getParam(forName = PARAM_SCOPE)?.let { scope ->
            DivActionSetStoredValue.Scope.fromString(scope) ?: run {
                div2View.logError(
                    RuntimeException(
                        "Value $name stored with the default scope. Invalid scope: $scope."
                    )
                )
                null
            }
        }

        val parser = StoredValueParser(
            reportError = { message -> div2View.logError(RuntimeException(message)) }
        )
        val storedValue = parser.parse(type, name, value) ?: return
        return executeAction(storedValue, scope, lifetime, div2View)
    }

    fun executeAction(
        value: StoredValue,
        scope: DivActionSetStoredValue.Scope?,
        lifetime: Long,
        view: Div2View
    ) {
        val errorCollector = view.viewComponent.errorCollectors.getOrCreate(
            view.divTag,
            view.divData
        )
        view.div2Component.storedValuesController.setStoredValue(
            value,
            lifetime,
            scope.toStoredValueScope(),
            view.divTag.id,
            errorCollector
        )
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
