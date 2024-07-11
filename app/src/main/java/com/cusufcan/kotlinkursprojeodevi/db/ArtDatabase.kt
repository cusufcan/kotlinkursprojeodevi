package com.cusufcan.kotlinkursprojeodevi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cusufcan.kotlinkursprojeodevi.model.Art

@Database(entities = [Art::class], version = 1)
abstract class ArtDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao
}