package com.example.courseapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapplication.data.repository.CourseRepository
import com.example.courseapplication.domain.model.Category
import com.example.courseapplication.domain.model.Course
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseEntryViewModel @Inject constructor(private val repository: CourseRepository) : ViewModel() {
    
    var courseUiState by mutableStateOf(CourseUiState())
        private set
    
    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var isLoadingCategories by mutableStateOf(true)
        private set

    var categoryLoadError by mutableStateOf<String?>(null)
        private set

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                isLoadingCategories = true
                categoryLoadError = null
                categories = repository.getCategories()
            } catch (e: Exception) {
                categoryLoadError = "Failed to load categories: ${e.message}"
                // Provide some default categories as fallback
                categories = emptyList()
            } finally {
                isLoadingCategories = false
            }
        }
    }

    fun updateUiState(courseDetails: CourseDetails) {
        courseUiState = CourseUiState(courseDetails = courseDetails, isEntryValid = validateInput(courseDetails))
    }

    suspend fun saveCourse() {
        if (validateInput(courseUiState.courseDetails)) {
            val details = courseUiState.courseDetails
            val score = details.title.length * (details.lessons.toIntOrNull() ?: 0)
            repository.insertCourse(details.toCourse().copy(score = score))
        }
    }
    
    suspend fun updateCourse() {
         if (validateInput(courseUiState.courseDetails)) {
            val details = courseUiState.courseDetails
            val score = details.title.length * (details.lessons.toIntOrNull() ?: 0)
            repository.updateCourse(details.toCourse().copy(score = score))
        }
    }
    
    suspend fun deleteCourse() {
         repository.deleteCourse(courseUiState.courseDetails.toCourse())
    }
    
    fun loadCourse(courseId: Int) {
        viewModelScope.launch {
            repository.getCourseById(courseId)?.let {
                courseUiState = CourseUiState(courseDetails = it.toCourseDetails(), isEntryValid = true)
            }
        }
    }

    private fun validateInput(uiState: CourseDetails = courseUiState.courseDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && description.isNotBlank() && category.isNotBlank() && lessons.isNotBlank()
        }
    }
}

data class CourseUiState(
    val courseDetails: CourseDetails = CourseDetails(),
    val isEntryValid: Boolean = false
)

data class CourseDetails(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val lessons: String = "",
    val score: Int = 0
)

fun CourseDetails.toCourse(): Course = Course(
    id = id,
    title = title,
    description = description,
    category = category,
    lessons = lessons.toIntOrNull() ?: 0,
    score = score // Use existing score or 0 if new (but logic overrides it on save)
)

fun Course.toCourseDetails(): CourseDetails = CourseDetails(
    id = id,
    title = title,
    description = description,
    category = category,
    lessons = lessons.toString(),
    score = score
)
