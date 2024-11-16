package com.leoh.csvfilelogger;

import android.util.Log;

import com.elvishew.xlog.flattener.Flattener;
import com.elvishew.xlog.flattener.Flattener2;
import com.elvishew.xlog.internal.DefaultsFactory;
import com.elvishew.xlog.internal.Platform;
import com.elvishew.xlog.internal.printer.file.backup.BackupStrategyWrapper;
import com.elvishew.xlog.internal.printer.file.backup.BackupUtil;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.backup.BackupStrategy;
import com.elvishew.xlog.printer.file.backup.BackupStrategy2;
import com.elvishew.xlog.printer.file.clean.CleanStrategy;
import com.elvishew.xlog.printer.file.naming.FileNameGenerator;
import com.elvishew.xlog.printer.file.writer.Writer;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyFilePrinter implements Printer {

	/**
	 * The folder path of log file.
	 */
	private final String folderPath;

	/**
	 * The file name generator for log file.
	 */
	private final FileNameGenerator fileNameGenerator;

	/**
	 * The backup strategy for log file.
	 */
	private final BackupStrategy2 backupStrategy;

	/**
	 * The clean strategy for log file.
	 */
	private final CleanStrategy cleanStrategy;

	/**
	 * Every time before printing a log, try to clean the log if necessary,
	 * not just check when creating new log file.
	 */
	private final boolean cleanInRealTime;

	/**
	 * The flattener when print a log.
	 */
	private final Flattener2 flattener;

	/**
	 * Log writer.
	 */
	private final Writer writer;

	private final Worker worker;

	/*package*/ MyFilePrinter(Builder builder) {
		folderPath = builder.folderPath;
		fileNameGenerator = builder.fileNameGenerator;
		backupStrategy = builder.backupStrategy;
		cleanStrategy = builder.cleanStrategy;
		cleanInRealTime = builder.cleanInRealTime;
		flattener = builder.flattener;
		writer = builder.writer;

		worker = new Worker();

		checkLogFolder();
	}

	/**
	 * Make sure the folder of log file exists.
	 */
	private void checkLogFolder() {
		File folder = new File(folderPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	@Override
	public void println(int logLevel, String tag, String msg) {
		long timeMillis = System.currentTimeMillis();
		if (!worker.isStarted()) {
			worker.start();
		}
		worker.enqueue(new LogItem(timeMillis, logLevel, tag, msg));
	}

	/**
	 * Do the real job of writing log to file.
	 */
	private void doPrintln(long timeMillis, int logLevel, String tag, String msg) {
		String lastFileName = writer.getOpenedFileName();
		boolean isWriterClosed = !writer.isOpened();
		if (lastFileName == null || isWriterClosed || fileNameGenerator.isFileNameChangeable()) {
			String newFileName = fileNameGenerator.generateFileName(logLevel, System.currentTimeMillis());
			if (newFileName == null || newFileName.trim().isEmpty()) {
				Platform.get().error("File name should not be empty, ignore log: " + msg);
				return;
			}
			if (!newFileName.equals(lastFileName) || isWriterClosed) {
				writer.close();
				cleanLogFilesIfNecessary(null);
				if (!writer.open(new File(folderPath, newFileName))) {
					return;
				}
				lastFileName = newFileName;
			}
		}

		File lastFile = writer.getOpenedFile();
		if (backupStrategy.shouldBackup(lastFile)) {
			// Backup the log file, and create a new log file.
			writer.close();
			BackupUtil.backup(lastFile, backupStrategy);
			if (!writer.open(new File(folderPath, lastFileName))) {
				return;
			}
		}
		String flattenedLog = flattener.flatten(timeMillis, logLevel, tag, msg).toString();
		writer.appendLog(flattenedLog);
	}

	/**
	 * Clean log files if should clean follow strategy
	 *
	 * @param currentFileName current logging file name
	 */
	private void cleanLogFilesIfNecessary(String currentFileName) {
		File logDir = new File(folderPath);
		File[] files = logDir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if ((currentFileName == null || !currentFileName.equals(file.getName()))
				&& cleanStrategy.shouldClean(file)) {
				file.delete();
			}
		}
	}

	/**
	 * Builder for {@link com.elvishew.xlog.printer.file.FilePrinter}.
	 */
	public static class Builder {

		/**
		 * The folder path of log file.
		 */
		String folderPath;

		/**
		 * The file name generator for log file.
		 */
		FileNameGenerator fileNameGenerator;

		/**
		 * The backup strategy for log file.
		 */
		BackupStrategy2 backupStrategy;

		/**
		 * The clean strategy for log file.
		 */
		CleanStrategy cleanStrategy;


		/**
		 * Every time before printing a log, try to clean the log if necessary,
		 * not just check when creating new log file.
		 */
		boolean cleanInRealTime;

		/**
		 * The flattener when print a log.
		 */
		Flattener2 flattener;

		/**
		 * The writer to write log into log file.
		 */
		Writer writer;

		/**
		 * Construct a builder.
		 *
		 * @param folderPath the folder path of log file
		 */
		public Builder(String folderPath) {
			this.folderPath = folderPath;
		}

		/**
		 * Set the file name generator for log file.
		 *
		 * @param fileNameGenerator the file name generator for log file
		 * @return the builder
		 */
		public Builder fileNameGenerator(FileNameGenerator fileNameGenerator) {
			this.fileNameGenerator = fileNameGenerator;
			return this;
		}

		/**
		 * Set the backup strategy for log file.
		 *
		 * @param backupStrategy the backup strategy for log file
		 * @return the builder
		 */
		public Builder backupStrategy(BackupStrategy backupStrategy) {
			if (!(backupStrategy instanceof BackupStrategy2)) {
				backupStrategy = new BackupStrategyWrapper(backupStrategy);
			}
			this.backupStrategy = (BackupStrategy2) backupStrategy;

			BackupUtil.verifyBackupStrategy(this.backupStrategy);
			return this;
		}

		/**
		 * Set the clean strategy for log file.
		 *
		 * @param cleanStrategy the clean strategy for log file
		 * @return the builder
		 * @since 1.5.0
		 */
		public Builder cleanStrategy(CleanStrategy cleanStrategy) {
			this.cleanStrategy = cleanStrategy;
			return this;
		}

		/**
		 * Set the clean strategy for log file.
		 *
		 * @param cleanStrategy   the clean strategy for log file
		 * @param checkInRealTime true if clean in real-time, false if only clean during the
		 *                        start-up phase, default to be false
		 * @return the builder
		 * @since 1.12.0
		 */
		public Builder cleanStrategy(CleanStrategy cleanStrategy, boolean checkInRealTime) {
			this.cleanStrategy = cleanStrategy;
			this.cleanInRealTime = checkInRealTime;
			return this;
		}

		/**
		 * Set the flattener when print a log.
		 *
		 * @param flattener the flattener when print a log
		 * @return the builder
		 * @deprecated {@link Flattener} is deprecated, use {@link #flattener(Flattener2)} instead,
		 * since 1.6.0
		 */
		@Deprecated
		public Builder logFlattener(final Flattener flattener) {
			return flattener(new Flattener2() {
				@Override
				public CharSequence flatten(long timeMillis, int logLevel, String tag, String message) {
					return flattener.flatten(logLevel, tag, message);
				}
			});
		}

		/**
		 * Set the flattener when print a log.
		 *
		 * @param flattener the flattener when print a log
		 * @return the builder
		 * @since 1.6.0
		 */
		public Builder flattener(Flattener2 flattener) {
			this.flattener = flattener;
			return this;
		}

		/**
		 * Set the writer to write log into log file.
		 *
		 * @param writer the writer to write log into log file
		 * @return the builder
		 * @since 1.11.0
		 */
		public Builder writer(Writer writer) {
			this.writer = writer;
			return this;
		}

		/**
		 * Build configured {@link com.elvishew.xlog.printer.file.FilePrinter} object.
		 *
		 * @return the built configured {@link com.elvishew.xlog.printer.file.FilePrinter} object
		 */
		public MyFilePrinter build() {
			fillEmptyFields();
			return new MyFilePrinter(this);
		}

		private void fillEmptyFields() {
			if (fileNameGenerator == null) {
				fileNameGenerator = DefaultsFactory.createFileNameGenerator();
			}
			if (backupStrategy == null) {
				backupStrategy = DefaultsFactory.createBackupStrategy();
			}
			if (cleanStrategy == null) {
				cleanStrategy = DefaultsFactory.createCleanStrategy();
			}
			if (flattener == null) {
				flattener = DefaultsFactory.createFlattener2();
			}
			if (writer == null) {
				writer = DefaultsFactory.createWriter();
			}
		}
	}

	private static class LogItem {

		long timeMillis;
		int level;
		String tag;
		String msg;

		LogItem(long timeMillis, int level, String tag, String msg) {
			this.timeMillis = timeMillis;
			this.level = level;
			this.tag = tag;
			this.msg = msg;
		}
	}

	/**
	 * Work in background, we can enqueue the logs, and the worker will dispatch them.
	 */
	private class Worker implements Runnable {

		private final BlockingQueue<MyFilePrinter.LogItem> logs = new LinkedBlockingQueue<>();

		private volatile boolean started;

		/**
		 * Enqueue the log.
		 *
		 * @param log the log to be written to file
		 */
		void enqueue(MyFilePrinter.LogItem log) {
			try {
				logs.put(log);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Whether the worker is started.
		 *
		 * @return true if started, false otherwise
		 */
		boolean isStarted() {
			synchronized (this) {
				return started;
			}
		}

		/**
		 * Start the worker.
		 */
		void start() {
			synchronized (this) {
				if (started) {
					return;
				}
				new Thread(this).start();
				started = true;
			}
		}

		@Override
		public void run() {
			MyFilePrinter.LogItem log;
			try {
				while ((log = logs.take()) != null) {
					doPrintln(log.timeMillis, log.level, log.tag, log.msg);
					Log.d("TAG", "run: write log on thread " + Thread.currentThread().getName());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				synchronized (this) {
					started = false;
				}
			}
		}
	}
}
