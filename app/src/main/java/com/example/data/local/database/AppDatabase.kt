package com.example.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.entity.*

@Database(
    entities = [
        CardEntity::class,
        RouterProfileEntity::class,
        TestResultEntity::class,
        TestSessionEntity::class,
        SuccessfulPatternEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun routerProfileDao(): RouterProfileDao
    abstract fun testResultDao(): TestResultDao
    abstract fun sessionDao(): SessionDao
    abstract fun patternDao(): PatternDao

    companion object {
        const val DATABASE_NAME = "wdmaster_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL(
                        "ALTER TABLE router_profiles ADD COLUMN logout_selector TEXT NOT NULL DEFAULT ''"
                    )
                } catch (_: Exception) {}
            }
        }
    }
}
