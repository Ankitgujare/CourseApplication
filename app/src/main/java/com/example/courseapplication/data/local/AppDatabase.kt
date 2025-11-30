package com.example.courseapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.courseapplication.domain.model.Course

@Database(entities = [Course::class, CategoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun categoryDao(): CategoryDao
}
