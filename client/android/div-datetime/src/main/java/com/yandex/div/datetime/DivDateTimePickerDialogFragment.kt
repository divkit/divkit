package com.yandex.div.datetime

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.yandex.div.datetime.utils.setSelectedDate
import com.yandex.div.datetime.utils.setSelectedTime
import java.util.Calendar
import java.util.Date

internal class DivDateTimePickerDialogFragment : DialogFragment(),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    companion object {

        private const val ARG_PICKER_TYPE = "picker_type"
        private const val ARG_PREVIOUS_STEP_DATE = "previous_step_date"
        const val RESULT_KEY = "divkit_datepicker_result"
        const val RESULT_SELECTED_DATETIME = "divkit_datepicker_result_datetime"

        internal fun newInstance(
            type: PickerType,
            dateFromPreviousStep: Date? = null
        ) = DivDateTimePickerDialogFragment().apply {
            arguments = bundleOf(
                ARG_PICKER_TYPE to type,
                ARG_PREVIOUS_STEP_DATE to dateFromPreviousStep
            )
        }
    }

    private val pickerType: PickerType
        get() = requireArguments().getSerializable(ARG_PICKER_TYPE) as PickerType

    private val previousStepDate: Date?
        get() = requireArguments().getSerializable(ARG_PREVIOUS_STEP_DATE) as? Date

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        return pickerType.createDialog(calendar)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val resultCalendar = Calendar.getInstance()
        previousStepDate?.let { date ->
            resultCalendar.setSelectedDate(date)
        }
        resultCalendar.setSelectedTime(hourOfDay, minute)
        submitInput(resultCalendar.time)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val resultCalendar = Calendar.getInstance()
        resultCalendar.setSelectedDate(year, month, day)
        val resultDate = resultCalendar.time
        val displayTimeDialog = pickerType == PickerType.DATE_TIME
        if (displayTimeDialog) {
            navigateToTimeDialog(resultDate)
        } else {
            submitInput(resultDate)
        }
    }

    private fun navigateToTimeDialog(selectedDate: Date) {
        dismiss()
        newInstance(
            PickerType.TIME,
            selectedDate
        ).show(parentFragmentManager, null)
    }

    private fun submitInput(result: Date) {
        parentFragmentManager.setFragmentResult(
            RESULT_KEY,
            bundleOf(RESULT_SELECTED_DATETIME to result)
        )
        dismiss()
    }

    private fun PickerType.createDialog(calendar: Calendar): Dialog = when (this) {
        PickerType.DATE, PickerType.DATE_TIME -> DatePickerDialog(
            requireContext(),
            this@DivDateTimePickerDialogFragment,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        PickerType.TIME -> TimePickerDialog(
            requireActivity(),
            this@DivDateTimePickerDialogFragment,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(activity)
        )
    }
}