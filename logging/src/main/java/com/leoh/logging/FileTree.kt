package com.leoh.logging

import android.util.Log
import timber.log.Timber
import java.io.File

class FileTree(
	private val directory: File,
	private val fileNameGenerator: FileNameGenerator,
) : Timber.DebugTree() {
	init {
		val uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
		Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
			Timber.e(throwable, "Uncaught exception!!!")
			uncaughtExceptionHandler?.uncaughtException(thread, throwable)
		}
	}

	private val worker =
		Worker { logRecord ->
			writeLog(logRecord)
		}

	override fun log(
		priority: Int,
		tag: String?,
		message: String,
		t: Throwable?,
	) {
		if (!worker.isStarted()) {
			worker.start()
		}
		worker.enqueue(
			LogRecord(
				time = System.currentTimeMillis(),
				priority = priority,
				tag = tag,
				message = message,
				throwable = t,
			),
		)
	}

	private fun writeLog(log: LogRecord) {
		if (!directory.exists()) directory.mkdirs()
	}

	private val priorityMap =
		mapOf(
			Log.VERBOSE to "VERBOSE",
			Log.DEBUG to "DEBUG",
			Log.INFO to "INFO",
			Log.WARN to "WARN",
			Log.ERROR to "ERROR",
			Log.ASSERT to "ASSERT",
		)

	private fun priorityToString(priority: Int) = priorityMap.getOrDefault(priority, "Unknown")
}
