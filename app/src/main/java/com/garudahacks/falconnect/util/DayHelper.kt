package com.garudahacks.falconnect.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Date.timeOfDay(): TimeOfDay {
	val calendar = Calendar.getInstance(TimeZone.getDefault())
	calendar.time = this
	val hour = calendar.get(Calendar.HOUR_OF_DAY)
	Log.d("TimeOfDay", "hour: $hour")
	return when (hour) {
		in 5..11 -> TimeOfDay.MORNING
		in 11..15 -> TimeOfDay.DAY
		in 15..18 -> TimeOfDay.AFTERNOON
		else -> TimeOfDay.NIGHT
	}
}

fun getCurrentDateTime(): String {
	val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
	val currentDate = Date()
	return dateFormat.format(currentDate)
}