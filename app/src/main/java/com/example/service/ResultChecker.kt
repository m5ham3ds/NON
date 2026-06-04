package com.example.service

import timber.log.Timber

object ResultChecker {
    fun checkResult(html: String, successIndicator: String, failureIndicator: String): Boolean? {
        Timber.d("Checking result on HTML of length ${html.length}")
        val hasSuccess = successIndicator.isNotEmpty() && html.contains(successIndicator, ignoreCase = true)
        val hasFailure = failureIndicator.isNotEmpty() && html.contains(failureIndicator, ignoreCase = true)

        return when {
            hasSuccess -> {
                Timber.d("Success indicator found!")
                true
            }
            hasFailure -> {
                Timber.d("Failure indicator found!")
                false
            }
            else -> {
                Timber.d("Neither indicator found on the current HTML.")
                null
            }
        }
    }
}
