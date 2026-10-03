package com.example.spire_labs.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spire_labs.data.local.dao.CartDao
import com.example.spire_labs.data.local.entity.CartEntity

@Database(
    entities = [CartEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "spire_labs_db"
    }
}
