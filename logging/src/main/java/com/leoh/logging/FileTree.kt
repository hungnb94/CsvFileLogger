package com.leoh.logging

import com.elvishew.xlog.printer.Printer
import timber.log.Timber

class FileTree(
	private val filePrinter: Printer,
) : Timber.DebugTree() {
	init {
		val uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
		Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
			Timber.e(throwable, "Uncaught exception!!!")
			uncaughtExceptionHandler?.uncaughtException(thread, throwable)
		}
	}

	override fun log(
		priority: Int,
		tag: String?,
		message: String,
		t: Throwable?,
	) {
		filePrinter.println(priority, tag, message)
	}
}
