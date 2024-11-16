package com.leoh.logging

data class LogRecord(
	val time: Long,
	val priority: Int,
	val tag: String?,
	val message: String,
	val throwable: Throwable? = null,
)
