package com.example.courseapplication.data.repository

import com.example.courseapplication.data.local.CategoryDao
import com.example.courseapplication.data.local.CategoryEntity
import com.example.courseapplication.data.local.CourseDao
import com.example.courseapplication.data.remote.CourseApi
import com.example.courseapplication.domain.model.Category
import com.example.courseapplication.domain.model.Course
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val courseDao: CourseDao,
    private val categoryDao: CategoryDao,
    private val courseApi: CourseApi
) {
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    suspend fun insertCourse(course: Course) {
        courseDao.insertCourse(course)
    }

    suspend fun updateCourse(course: Course) {
        courseDao.updateCourse(course)
    }

    suspend fun deleteCourse(course: Course) {
        courseDao.deleteCourse(course)
    }
    
    suspend fun getCourseById(id: Int): Course? {
        return courseDao.getCourseById(id)
    }

    suspend fun getCategories(): List<Category> {
        try {
            val remoteCategories = courseApi.getCategories()
            val entities = remoteCategories.map { CategoryEntity(it.id, it.name) }
            categoryDao.insertCategories(entities)
            return remoteCategories
        } catch (e: Exception) {
            return categoryDao.getCategories().map { Category(it.id, it.name) }
        }
    }
}
