package com.leoh.csvfilelogger

import android.app.Application
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator

class LogApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		initLog()
	}

	private fun initLog() {
		val config =
			LogConfiguration
				.Builder()
				.logLevel(LogLevel.ALL)
				.enableThreadInfo() // Enable thread info, disabled by default
				.enableStackTrace(2) // Enable stack trace info with depth 2, disabled by default
				.enableBorder() // Enable border, disabled by default
				.build()

		val androidPrinter: Printer =
			AndroidPrinter(true) // Printer that print the log using android.util.Log
		val filePrinter: Printer =
			FilePrinter
				.Builder(obbDir.absolutePath) // Specify the directory path of log file(s)
				.fileNameGenerator(DateFileNameGenerator()) // Default: ChangelessFileNameGenerator("log")
				.build()

		XLog.init( // Initialize XLog
			config, // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
			androidPrinter, // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
			filePrinter,
		)
		Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
			XLog.e("Face uncaught exception", exception)
			throw exception
		}
	}
}
