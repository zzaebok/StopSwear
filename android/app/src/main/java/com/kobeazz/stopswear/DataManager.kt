package com.kobeazz.stopswear
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class DataManager {

    companion object {
        private const val TAG = "DATA_MANAGER"
        private const val DAYS = 4

        private var instance: DataManager? = null
        private lateinit var sp: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private lateinit var dateFormat: SimpleDateFormat

        fun getInstance(context: Context): DataManager {
            if (instance == null) {
                initialize(context)
            }
            return instance!!
        }

        fun initialize(context: Context) {
            // constructing
            sp = context.getSharedPreferences(R.string.sharedPrefName.toString(), Context.MODE_PRIVATE)
            editor = sp.edit()
            dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            instance = DataManager()
        }
    }

    init {
        // default 5 days
        initializeDates()
    }

    fun logSwearingTimes() {
        Log.d(TAG, sp.all.toString())
        val today = getToday()
        if (!contains(today)) {
            updateDates()
        }
        putValue(today, getValue(today, "Int") as Int + 1, "Int")
    }

    fun getToday(): String {
        return dateFormat.format(Calendar.getInstance().time)
    }

    fun getPreviousDays(dateString: String, days: Int): MutableList<String> {
        val dateList: MutableList<String> = mutableListOf<String>()
        dateList.add(dateString)
        for (i in 1 .. days) {
            val c: Calendar = Calendar.getInstance()
            c.setTime(dateFormat.parse(dateString))
            c.add(Calendar.DATE, -i)
            val previousDateString = dateFormat.format(c.time)
            dateList.add(previousDateString)
        }
        return dateList
    }

    fun initializeDates() {
        val today = getToday()
        val previousDays = getPreviousDays(today, DAYS)
        val previousDaysString = previousDays.joinToString(separator="/")
        putValue(key="days", value=previousDaysString, type="String")
        for (day in previousDays) {
            putValue(key=day, value=0, type="Int")
        }
    }

    fun updateDates() {
        // already in sp
        val days = getValue("days", "String").toString()
        val daysList = days.split("/")

        // new days to be added
        val today = getToday()
        val previousDays = getPreviousDays(today, DAYS)
        val previousDaysString = previousDays.joinToString(separator="/")
        putValue(key="days", value=previousDaysString, type="String")

        for (previousDay in previousDays) {
            if (previousDay !in daysList) {
                putValue(key=previousDay, value=0, type="Int")
            }
        }

        // delete old days
        for (day in daysList) {
            if (day !in previousDays) {
                removeValue(day)
            }
        }
    }

    fun getValue(key: String, type: String): Any? {
        val ret = when (type) {
            "String" -> sp.getString(key, "")
            // should be false to handle vibration settings
            "Boolean" -> sp.getBoolean(key, false)
            "Int" -> sp.getInt(key, 0)
            "Float" -> sp.getFloat(key, 0.0f)
            else -> null
        }
        return ret
    }

    fun putValue(key: String, value: Any, type: String) {
        when (type) {
            "String" -> editor.putString(key, value.toString())
            "Boolean" -> editor.putBoolean(key, value as Boolean)
            "Int" -> editor.putInt(key, value as Int)
            "Float" -> editor.putFloat(key, value as Float)
        }
        editor.commit()
    }

    fun removeValue(key: String) {
        Log.d(TAG, "removeValue" + key)
        editor.remove(key)
        editor.commit()
    }

    fun contains(key: String): Boolean {
        return sp.contains(key)
    }

}