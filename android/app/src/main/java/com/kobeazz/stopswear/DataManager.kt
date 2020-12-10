package com.kobeazz.stopswear
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class DataManager(private val context: Context) {
    val sp: SharedPreferences
    val editor: SharedPreferences.Editor
    val dateFormat: SimpleDateFormat

    init {
        sp = context.getSharedPreferences(R.string.sharedPrefName.toString(), Context.MODE_PRIVATE)
        editor = sp.edit()
        dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    companion object {
        private const val TAG = "DATA_MANAGER"
    }

    fun logSwearingTimes() {
        // Get todays date string

        val dateString = dateFormat.format(Calendar.getInstance().time)
        if (sp.contains(dateString)) {
            editor.putInt(dateString, sp.getInt(dateString, 0) + 1)
        } else {
            editor.putInt(dateString, 1)
        }

        // Delete old histories
        val validDateList = getValidDates(dateString)
        for (x in sp.all) {
            if (x.key !in validDateList) {
                editor.remove(x.key)
            }
        }
        for (validDate in validDateList) {
            if (validDate !in sp.all) {
                editor.putInt(validDate, 0)
            }
        }
        editor.commit()

        for (x in sp.all) {
            Log.d(TAG, x.key + " " + x.value.toString())
        }

    }

    private fun getValidDates(dateString: String): MutableList<String> {
        val dateList: MutableList<String> = mutableListOf<String>()
        dateList.add(dateString)
        for (i in 1 .. 4) {
            val c: Calendar = Calendar.getInstance()
            c.setTime(dateFormat.parse(dateString))
            c.add(Calendar.DATE, -i)
            val previousDateString = dateFormat.format(c.time)
            dateList.add(previousDateString)
        }
        return dateList
    }

}