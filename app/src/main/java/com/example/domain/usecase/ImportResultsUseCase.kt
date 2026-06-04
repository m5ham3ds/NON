package com.example.domain.usecase

import com.example.data.local.entity.TestResultEntity
import com.example.domain.repository.ITestResultRepository
import kotlinx.serialization.json.Json
import java.io.File

class ImportResultsUseCase(private val testResultRepository: ITestResultRepository) {
    suspend operator fun invoke(file: File): Result {
        return try {
            if (!file.exists()) return Result.Error("File does not exist")
            val json = file.readText()
            val results = Json.decodeFromString<List<TestResultEntity>>(json)
            testResultRepository.insertResults(results)
            Result.Success
        } catch (e: Exception) {
            Result.Error(e.message ?: "Import failed")
        }
    }

    sealed class Result {
        data object Success : Result()
        data class Error(val message: String) : Result()
    }
}
