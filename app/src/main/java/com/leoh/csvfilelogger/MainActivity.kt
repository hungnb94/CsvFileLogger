package com.leoh.csvfilelogger

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.elvishew.xlog.XLog

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
			XLog.v("Log verbose")
		}
		btnLogDebug.setOnClickListener {
			XLog.d("Log debug")
		}
		btnLogInfo.setOnClickListener {
			XLog.i("Log info")
		}
		btnLogWarn.setOnClickListener {
			XLog.w("Log warn", RuntimeException("warn!"))
		}
		btnLogError.setOnClickListener {
			XLog.e("Log error", RuntimeException("error!!!"))
		}
		btnFireException.setOnClickListener {
			throw Exception("Uncaught exception")
		}
	}
}
