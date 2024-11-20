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
	): CharSequence {
		val timestamp = timeFormat.format(Date(timeMillis))
		val logLevelName = LogLevel.getLevelName(logLevel)
		val logMessage = message?.replace("\"", "\'")
		return """"$timestamp","$logLevelName","$tag","$logMessage""""
	}
}
