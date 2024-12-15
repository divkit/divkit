package com.yandex.div.datetime

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.datetime.data.PickerMode
import com.yandex.div.datetime.domain.PickerType
import com.yandex.div.datetime.utils.parseMode
import com.yandex.div.datetime.utils.toPickerType
import com.yandex.div.internal.util.getStringOrEmpty
import com.yandex.div.internal.util.getStringOrNull
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivInput
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DivExtension that allows to show date/time picker from input.
 * Setting this extension will make input field non-focusable to prevent showing keyboard.
 *
 * Parameters:
 * * text_variable – name of string variable to set selected date and time
 * * mode – sets the type of picker. Can be "date", "time" or both separated by vertical bar (|) sign.
 *
 * Example of usage:
 * ```json
 * "extensions": [
 *    {
 *        "id": "date_time_picker",
 *        "params": {
 *            "text_variable": "value",
 *            "mode": "date|time"
 *        }
 *    }
 * ],
 * ```
 *
 */

public class DivDateTimeExtensionHandler : DivExtensionHandler {

    private var fragmentManagerRef: FragmentManager? = null
    private var divViewRef: Div2View? = null
    private var inputVariableName: String? = null
    private var dateFormatter: SimpleDateFormat? = null
    private var pickerType: PickerType? = null
    private var inputWasFocusable = false

    override fun matches(div: DivBase): Boolean {
        if (div !is DivInput) {
            return false
        }
        val hasExtensionSetUp =
            div.extensions?.any { extension -> extension.id == EXTENSION_ID } ?: false
        return hasExtensionSetUp
    }

    override fun bindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        initVariablesOnBind(divView, div, view)
        divView.doOnAttach {
            val lifecycleOwner = divView.findViewTreeLifecycleOwner() as? AppCompatActivity
            if (lifecycleOwner != null) {
                val fragmentManager = lifecycleOwner.supportFragmentManager
                fragmentManagerRef = fragmentManager
                observePickerResult(fragmentManager, lifecycleOwner)
            }
        }
        view.isFocusable = false
        view.setOnClickListener { _ -> handleClickEvent() }
    }

    override fun unbindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        view.setOnClickListener(null)
        view.isFocusable = inputWasFocusable
        fragmentManagerRef = null
        divViewRef = null
        inputVariableName = null
        dateFormatter = null
        pickerType = null
    }

    private fun initVariablesOnBind(
        divView: Div2View,
        divBase: DivBase,
        view: View
    ) {
        val params = divBase.extensions?.first { it.id == EXTENSION_ID }?.params ?: return
        val modes = readMode(params)
        validateMode(modes)
        val parsedPickerType = requireNotNull(modes.toPickerType())
        inputVariableName = readTextVariable(params)
        divViewRef = divView
        pickerType = parsedPickerType
        dateFormatter = SimpleDateFormat(parsedPickerType.toDateFormatter(), Locale.getDefault())
        inputWasFocusable = view.isFocusable
    }

    private fun handleClickEvent() {
        val currentPickerType = pickerType
        val fragmentManager = fragmentManagerRef
        if (currentPickerType != null && fragmentManager != null) {
            DivDateTimePickerDialogFragment.newInstance(currentPickerType).show(
                fragmentManager,
                null
            )
        }
    }

    private fun observePickerResult(
        fragmentManager: FragmentManager,
        lifecycleOwner: LifecycleOwner
    ) {
        fragmentManager.setFragmentResultListener(
            DivDateTimePickerDialogFragment.RESULT_KEY,
            lifecycleOwner,
        ) { _, bundle ->
            if (bundle.containsKey(DivDateTimePickerDialogFragment.RESULT_SELECTED_DATETIME)) {
                val result =
                    bundle.getSerializable(
                        DivDateTimePickerDialogFragment.RESULT_SELECTED_DATETIME
                    ) as Date
                onDateSet(result)
            }
        }
    }

    private fun onDateSet(result: Date) {
        val view = divViewRef
        val variable = inputVariableName
        val dateFormatter = dateFormatter
        if (view != null && variable != null && dateFormatter != null) {
            view.setVariable(variable, dateFormatter.format(result))
        }
    }

    private fun readTextVariable(json: JSONObject): String? {
        return json.getStringOrNull(PARAM_TEXT_VARIABLE)
    }

    private fun readMode(json: JSONObject): Set<PickerMode> {
        val modeString = json.getStringOrEmpty(PARAM_MODE)
        return parseMode(modeString)
    }

    private fun validateMode(modes: Set<PickerMode>) {
        if (modes.isEmpty()) {
            throw IllegalArgumentException(
                "\"mode\" should be set as \"date\", \"time\", or both split by | sign"
            )
        }
    }

    private fun PickerType.toDateFormatter() = when (this) {
        PickerType.DATE -> DATE_FORMATTER
        PickerType.TIME -> TIME_FORMATTER
        PickerType.DATE_TIME -> DATETIME_FORMATTER
    }


    internal companion object {
        const val EXTENSION_ID = "date_time_picker"
        private const val PARAM_TEXT_VARIABLE = "text_variable"
        private const val PARAM_MODE = "mode"
        private const val DATE_FORMATTER = "dd.MM.yyyy"
        private const val TIME_FORMATTER = "HH:mm"
        private const val DATETIME_FORMATTER = "dd.MM.yyyy HH:mm"
    }
}