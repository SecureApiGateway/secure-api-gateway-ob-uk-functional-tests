package com.forgerock.sapi.gateway.framework.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileReader
import java.nio.file.Paths

class FileUtils {

    fun getInputStream(filePath: String): FileInputStream {
        fileChecks(filePath)
        return File(filePath).inputStream()
    }

    fun getFileReader(filePath: String): FileReader {
        fileChecks(filePath)
        return FileReader(Paths.get(filePath).toFile())
    }

    fun getStringContent(filePath: String): String {
        fileChecks(filePath)
        return File(filePath).readText()
    }

    private fun fileChecks(filePath: String) {
        if (!File(filePath).exists() && !File(filePath).canRead()) {
            throw FileNotFoundException("The file expected '$filePath' do not exist or can not be read, please check the configuration.")
        }
    }
}
