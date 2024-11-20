package com.leoh.logging

import java.io.File

class CsvFileTree(
	folderPath: File,
) : FileTree(CsvFilePrinter(folderPath = folderPath))
