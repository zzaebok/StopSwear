package com.kobeazz.stopswear
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class DataManager {

    companion object {
        private const val TAG = "DATA_MANAGER"

        private var instance: DataManager? = null
        private lateinit var sp: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private lateinit var dateFormat: SimpleDateFormat

        fun getInstance(context: Context): DataManager {
            if (instance == null) {
                sp = context.getSharedPreferences(R.string.sharedPrefName.toString(), Context.MODE_PRIVATE)
                editor = sp.edit()
                dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                instance = DataManager()
            }
            return instance!!
        }
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

    fun checkVibration(isChecked: Boolean) {
        Log.d(TAG, "isChecked" + isChecked.toString())
        if (!sp.contains("vibration")) {
            editor.putBoolean("vibration", true)
        }
        if (isChecked) {
            editor.putBoolean("vibration", true)
        } else {
            editor.putBoolean("vibration", false)
        }
        editor.commit()
        Log.d(TAG, "vibration" + sp.getBoolean("vibration", false).toString())
    }

    fun getVibration(): Boolean {
        Log.d(TAG, "getVibration" + sp.getBoolean("vibration", false).toString())
        return sp.getBoolean("vibration", false)
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
        // TODO
        dateList.add("vibration")
        return dateList
    }

}