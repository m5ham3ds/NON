package com.example.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RouterProfile(
    val id: Long = 0,
    val name: String = "",
    val ip: String = "",
    val protocol: String = "http",
    val username: String = "admin",
    val password: String = "",
    val loginPath: String = "/login",
    val usernameSelector: String = "",
    val passwordSelector: String = "",
    val submitSelector: String = "",
    val logoutSelector: String = "",
    val successIndicator: String = "",
    val failureIndicator: String = "",
    val customJs: String? = null,
    val md5Salt: String = "",
    val authType: RouterAuthType = RouterAuthType.FORM,
    val isActive: Boolean = true,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable
