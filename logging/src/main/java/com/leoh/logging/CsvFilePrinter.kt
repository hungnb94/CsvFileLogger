package com.leoh.logging

import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import java.io.File

class CsvFilePrinter(
	folderPath: File,
) : Printer {
	private val filePrinter =
		FilePrinter
			.Builder(folderPath.absolutePath)
			.flattener(CsvFlattener())
			.fileNameGenerator(CsvFileNameGenerator())
			.build()

	override fun println(
		logLevel: Int,
		tag: String?,
		msg: String?,
	) {
		filePrinter.println(logLevel, tag, msg)
	}
}
