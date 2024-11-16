package com.leoh.logging

interface FileNameGenerator {
	fun generateFileName(
		logLevel: Int,
		timestamp: Long,
	): String
}
