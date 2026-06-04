package com.example.util

import android.content.Context
import timber.log.Timber
import java.io.File

object FileUtils {
    private const val EXPORT_DIR = "exports"

    fun ensureDirExists(dir: File): Boolean {
        return if (!dir.exists()) dir.mkdirs() else true
    }

    fun writeText(file: File, content: String) {
        val parent = file.parentFile
            ?: throw IllegalArgumentException("File has no parent directory: ${file.path}")
        ensureDirExists(parent)
        file.writeText(content, Charsets.UTF_8)
    }

    fun readText(file: File): String? {
        return if (file.exists()) file.readText(Charsets.UTF_8) else null
    }

    fun getExportDir(context: Context): File {
        val dir = context.getExternalFilesDir(EXPORT_DIR)
            ?: context.filesDir.resolve(EXPORT_DIR)
        ensureDirExists(dir)
        return dir
    }

    fun getExportFile(context: Context, fileName: String): File {
        val dir = getExportDir(context)
        return File(dir, fileName)
    }
}
