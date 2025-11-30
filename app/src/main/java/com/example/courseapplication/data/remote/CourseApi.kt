package com.example.courseapplication.data.remote

import com.example.courseapplication.domain.model.Category
import retrofit2.http.GET

interface CourseApi {
    @GET("categories")
    suspend fun getCategories(): List<Category>
}
