package com.example.domain.repository

import com.example.data.local.entity.RouterProfileEntity
import kotlinx.coroutines.flow.Flow

interface IRouterRepository {
    val allRouters: Flow<List<RouterProfileEntity>>
    suspend fun insert(router: RouterProfileEntity): Long
    suspend fun update(router: RouterProfileEntity)
    suspend fun delete(router: RouterProfileEntity)
    suspend fun getById(id: Long): RouterProfileEntity?
    suspend fun getDefault(): RouterProfileEntity?
    suspend fun setDefault(id: Long)
}
