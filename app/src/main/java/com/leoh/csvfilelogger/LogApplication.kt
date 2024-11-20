package com.leoh.csvfilelogger

import android.app.Application
import com.leoh.logging.CsvFileTree
import timber.log.Timber

class LogApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		initLog()
	}

	private fun initLog() {
		obbDir.mkdirs()
		Timber.plant(CsvFileTree(obbDir))
	}
}
