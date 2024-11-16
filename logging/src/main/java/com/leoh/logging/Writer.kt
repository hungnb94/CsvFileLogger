package com.leoh.logging

import java.io.File

interface Writer {
	/**
	 * Open a specific log file for future writing, if it doesn't exist yet, just create it.
	 *
	 * @param file the specific log file, may not exist
	 * @return true if the log file is successfully opened, false otherwise
	 */
	fun open(file: File): Boolean

	/**
	 * Whether a log file is successfully opened in previous [.open].
	 *
	 * @return true if log file is opened, false otherwise
	 */
	val isOpened: Boolean

	/**
	 * Get the opened log file.
	 *
	 * @return the opened log file, or null if log file not opened
	 */
	val openedFile: File?

	/**
	 * Get the name of opened log file.
	 *
	 * @return the name of opened log file, or null if log file not opened
	 */
	val openedFileName: String?

	/**
	 * Append the log to the end of the opened log file, normally an extra line separator is needed.
	 *
	 * @param log the log to append
	 */
	fun appendLog(log: LogRecord)

	/**
	 * Make sure the opened log file is closed, normally called before switching the log file.
	 *
	 * @return true if the log file is successfully closed, false otherwise
	 */
	fun close(): Boolean
}
