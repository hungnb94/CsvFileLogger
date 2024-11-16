package com.leoh.csvfilelogger

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber

class MainActivity : AppCompatActivity() {
	private lateinit var btnLogVerbose: Button
	private lateinit var btnLogDebug: Button
	private lateinit var btnLogInfo: Button
	private lateinit var btnLogWarn: Button
	private lateinit var btnLogError: Button
	private lateinit var btnFireException: Button

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(R.layout.activity_main)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		btnLogVerbose = findViewById(R.id.btnLogVerbose)
		btnLogDebug = findViewById(R.id.btnLogDebug)
		btnLogInfo = findViewById(R.id.btnLogInfo)
		btnLogWarn = findViewById(R.id.btnLogWarn)
		btnLogError = findViewById(R.id.btnLogError)
		btnFireException = findViewById(R.id.btnFireException)
		btnLogVerbose.setOnClickListener {
			Timber.v("Log verbose")
		}
		btnLogDebug.setOnClickListener {
			Timber.d("Log debug")
		}
		btnLogInfo.setOnClickListener {
			Timber.i("Log info")
		}
		btnLogWarn.setOnClickListener {
			Timber.w(RuntimeException("warn!"), "Log warn")
		}
		btnLogError.setOnClickListener {
			Timber.e(RuntimeException("error!!!"), "Log error")
		}
		btnFireException.setOnClickListener {
			throw Exception("Uncaught exception")
		}
	}
}
