package com.example.courseapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.courseapplication.data.repository.CourseRepository
import com.example.courseapplication.domain.model.Course
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CourseRepository) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val homeUiState: StateFlow<HomeUiState> = combine(
        repository.allCourses,
        _searchQuery,
        _selectedCategory
    ) { courses, query, category ->
        val filtered = courses.filter { course ->
            (course.title.contains(query, ignoreCase = true) || 
             course.category.contains(query, ignoreCase = true)) &&
            (category == null || course.category == category)
        }
        HomeUiState(courses = filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }
}

data class HomeUiState(val courses: List<Course> = listOf())
