package com.example.courseapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courseapplication.ui.viewmodel.CourseDetailViewModel
import com.example.courseapplication.ui.viewmodel.CourseDetailUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    navigateBack: () -> Unit,
    navigateToEditItem: (Int) -> Unit,
    courseId: Int,
    modifier: Modifier = Modifier,
    viewModel: CourseDetailViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by remember { mutableStateOf(false) }
    
    LaunchedEffect(courseId) {
        viewModel.loadCourse(courseId)
    }
    
    val courseDetailState = viewModel.courseDetailState

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Course Details") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (courseDetailState is CourseDetailUiState.Success) {
                        IconButton(onClick = { navigateToEditItem(courseId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (courseDetailState is CourseDetailUiState.Success) {
                Button(
                    onClick = { deleteConfirmationRequired = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Course")
                }
            }
        }
    ) { innerPadding ->
        when (courseDetailState) {
            is CourseDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CourseDetailUiState.Success -> {
                val course = courseDetailState.course
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CourseDetailRow(label = "Title", value = course.title)
                    CourseDetailRow(label = "Category", value = course.category)
                    CourseDetailRow(label = "Lessons", value = course.lessons.toString())
                    CourseDetailRow(label = "Score", value = course.score.toString())
                    CourseDetailRow(label = "Description", value = course.description)
                    
                    if (deleteConfirmationRequired) {
                        DeleteConfirmationDialog(
                            onDeleteConfirm = {
                                deleteConfirmationRequired = false
                                coroutineScope.launch {
                                    viewModel.deleteCourse(course)
                                    navigateBack()
                                }
                            },
                            onDeleteCancel = { deleteConfirmationRequired = false }
                        )
                    }
                }
            }
            is CourseDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Course not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun CourseDetailRow(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDeleteCancel,
        title = { Text("Delete Course") },
        text = { Text("Are you sure you want to delete this course?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text("No")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text("Yes")
            }
        }
    )
}
