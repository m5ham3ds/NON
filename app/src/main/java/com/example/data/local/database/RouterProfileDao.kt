package com.example.data.local.database

import androidx.room.*
import com.example.data.local.entity.RouterProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouterProfileDao {
    @Query("SELECT * FROM router_profiles ORDER BY name ASC")
    fun getAllRouters(): Flow<List<RouterProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(router: RouterProfileEntity): Long

    @Update
    suspend fun update(router: RouterProfileEntity)

    @Delete
    suspend fun delete(router: RouterProfileEntity)

    @Query("SELECT * FROM router_profiles WHERE id = :id")
    suspend fun getById(id: Long): RouterProfileEntity?

    @Query("SELECT * FROM router_profiles WHERE is_default = 1 LIMIT 1")
    suspend fun getDefault(): RouterProfileEntity?

    @Query("UPDATE router_profiles SET is_default = 0")
    suspend fun resetDefaults()

    @Query("UPDATE router_profiles SET is_default = 1 WHERE id = :id")
    suspend fun setAsDefaultInternal(id: Long)

    @Transaction
    suspend fun setDefault(id: Long) {
        resetDefaults()
        setAsDefaultInternal(id)
    }
}
