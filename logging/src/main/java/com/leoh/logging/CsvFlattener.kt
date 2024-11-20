package com.leoh.logging

import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.flattener.Flattener2
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvFlattener : Flattener2 {
	private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

	override fun flatten(
		timeMillis: Long,
		logLevel: Int,
		tag: String?,
		message: String?,
	): CharSequence = "${timeFormat.format(Date(timeMillis))}, ${LogLevel.getLevelName(logLevel)}, $tag, $message"
}
