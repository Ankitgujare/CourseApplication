package com.example.courseapplication.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapplication.data.repository.CourseRepository
import com.example.courseapplication.domain.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {

    var courseDetailState by mutableStateOf<CourseDetailUiState>(CourseDetailUiState.Loading)
        private set

    fun loadCourse(courseId: Int) {
        viewModelScope.launch {
            courseDetailState = CourseDetailUiState.Loading
            val course = repository.getCourseById(courseId)
            courseDetailState = if (course != null) {
                CourseDetailUiState.Success(course)
            } else {
                CourseDetailUiState.Error
            }
        }
    }

    suspend fun deleteCourse(course: Course) {
        repository.deleteCourse(course)
    }
}

sealed class CourseDetailUiState {
    object Loading : CourseDetailUiState()
    data class Success(val course: Course) : CourseDetailUiState()
    object Error : CourseDetailUiState()
}
