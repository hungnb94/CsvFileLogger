package com.leoh.logging

import com.elvishew.xlog.printer.file.naming.FileNameGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvFileNameGenerator : FileNameGenerator {
	private val timeFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ssZ", Locale.US)

	override fun isFileNameChangeable(): Boolean = false

	override fun generateFileName(
		logLevel: Int,
		timestamp: Long,
	): String = "${timeFormat.format(Date(timestamp))}.csv"
}
