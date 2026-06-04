package com.example.domain.usecase

import com.example.data.local.entity.RouterProfileEntity
import com.example.domain.repository.IRouterRepository
import kotlinx.coroutines.flow.Flow

class ManageRoutersUseCase(private val routerRepository: IRouterRepository) {
    val allRouters: Flow<List<RouterProfileEntity>> = routerRepository.allRouters

    suspend fun addRouter(router: RouterProfileEntity): Long {
        return routerRepository.insert(router)
    }

    suspend fun updateRouter(router: RouterProfileEntity) {
        routerRepository.update(router)
    }

    suspend fun deleteRouter(router: RouterProfileEntity) {
        routerRepository.delete(router)
    }

    suspend fun setDefault(id: Long) {
        routerRepository.setDefault(id)
    }

    suspend fun getById(id: Long): RouterProfileEntity? {
        return routerRepository.getById(id)
    }
}
