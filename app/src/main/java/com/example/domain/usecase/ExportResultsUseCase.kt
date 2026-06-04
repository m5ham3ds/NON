package com.example.domain.usecase

import com.example.domain.repository.ITestResultRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ExportResultsUseCase(private val testResultRepository: ITestResultRepository) {
    suspend operator fun invoke(outputDir: File, fileName: String = "results_export.json"): Result {
        return try {
            val results = testResultRepository.allResults.first()
            val json = Json.encodeToString(results)
            if (!outputDir.exists()) outputDir.mkdirs()
            val file = File(outputDir, fileName)
            file.writeText(json)
            Result.Success(file.absolutePath)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Export failed")
        }
    }

    sealed class Result {
        data class Success(val path: String) : Result()
        data class Error(val message: String) : Result()
    }
}
