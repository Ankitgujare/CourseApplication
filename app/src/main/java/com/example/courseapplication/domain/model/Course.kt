package com.example.courseapplication.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey




//we are creating a table with name courses
@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val lessons: Int,
    val score: Int
)
