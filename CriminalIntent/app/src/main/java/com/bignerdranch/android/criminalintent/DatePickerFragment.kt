package com.bignerdranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

private const val TAG = "DatePickerFragment"
private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateLisenter =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                Log.i(
                    TAG, "Date picked: ${year}, ${month}, $day"
                )
                val resultDate: Date = GregorianCalendar(year, month, day).time
                targetFragment?.let {
                    (it as Callbacks).onDateSelected(resultDate)
                }
            }

        val date: Date = arguments?.getLong(ARG_DATE)?.let {
            Date(it)
        } ?: throw IllegalArgumentException("no crime date found in arguments")

        val calendar = Calendar.getInstance()
        calendar.time = date

        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            // 获取视图相关必要资源的context对象
            requireContext(),
            dateLisenter,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putLong(ARG_DATE, date.time)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}