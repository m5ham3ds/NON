package com.example.data.mapper

import com.example.data.local.entity.RouterProfileEntity
import com.example.domain.model.RouterProfile

object RouterMapper {
    fun RouterProfileEntity.toDomain(): RouterProfile = RouterProfile(
        id = id,
        name = name,
        ip = ip,
        protocol = protocol,
        username = username,
        password = password,
        loginPath = loginPath,
        usernameSelector = usernameSelector,
        passwordSelector = passwordSelector,
        submitSelector = submitSelector,
        logoutSelector = logoutSelector,
        successIndicator = successIndicator,
        failureIndicator = failureIndicator,
        customJs = customJs,
        md5Salt = md5Salt,
        authType = authType,
        isActive = isActive,
        isDefault = isDefault,
        createdAt = createdAt
    )

    fun RouterProfile.toEntity(): RouterProfileEntity = RouterProfileEntity(
        id = id,
        name = name,
        ip = ip,
        protocol = protocol,
        username = username,
        password = password,
        loginPath = loginPath,
        usernameSelector = usernameSelector,
        passwordSelector = passwordSelector,
        submitSelector = submitSelector,
        logoutSelector = logoutSelector,
        successIndicator = successIndicator,
        failureIndicator = failureIndicator,
        customJs = customJs,
        md5Salt = md5Salt,
        authType = authType,
        isActive = isActive,
        isDefault = isDefault,
        createdAt = createdAt
    )

    fun List<RouterProfileEntity>.toDomainList(): List<RouterProfile> = map { it.toDomain() }
}
