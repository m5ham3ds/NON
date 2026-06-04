package com.example.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.RouterAuthType
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "router_profiles")
data class RouterProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "ip") val ip: String,
    @ColumnInfo(name = "protocol") val protocol: String = "https",
    @ColumnInfo(name = "username") val username: String = "admin",
    @ColumnInfo(name = "password") val password: String = "",
    @ColumnInfo(name = "login_path") val loginPath: String = "/login",
    @ColumnInfo(name = "username_selector") val usernameSelector: String = "input[name=username]",
    @ColumnInfo(name = "password_selector") val passwordSelector: String = "input[name=password]",
    @ColumnInfo(name = "submit_selector") val submitSelector: String = "button[type=submit]",
    @ColumnInfo(name = "logout_selector") val logoutSelector: String = "",
    @ColumnInfo(name = "success_indicator") val successIndicator: String = "status=ok",
    @ColumnInfo(name = "failure_indicator") val failureIndicator: String = "error=",
    @ColumnInfo(name = "custom_js") val customJs: String? = null,
    @ColumnInfo(name = "md5_salt") val md5Salt: String = "",
    @ColumnInfo(name = "auth_type") val authType: RouterAuthType = RouterAuthType.FORM,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "is_default") val isDefault: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) : Parcelable
