package com.leoh.logging

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.Volatile

class Worker(
	private val doPrintln: (LogRecord) -> Unit,
) : Runnable {
	private val logs: BlockingQueue<LogRecord> = LinkedBlockingQueue()

	@Volatile
	private var started = false

	/**
	 * Enqueue the log.
	 *
	 * @param log the log to be written to file
	 */
	fun enqueue(log: LogRecord) {
		try {
			logs.put(log)
		} catch (e: InterruptedException) {
			e.printStackTrace()
		}
	}

	/**
	 * Whether the worker is started.
	 *
	 * @return true if started, false otherwise
	 */
	fun isStarted(): Boolean {
		synchronized(this) {
			return started
		}
	}

	/**
	 * Start the worker.
	 */
	fun start() {
		synchronized(this) {
			if (started) {
				return
			}
			Thread(this).start()
			started = true
		}
	}

	override fun run() {
		var log: LogRecord
		try {
			while ((logs.take().also { log = it }) != null) {
				doPrintln(log)
			}
		} catch (e: InterruptedException) {
			e.printStackTrace()
			synchronized(this) {
				started = false
			}
		}
	}
}
