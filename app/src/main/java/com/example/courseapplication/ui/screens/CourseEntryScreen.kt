package com.example.courseapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.courseapplication.ui.viewmodel.CourseDetails
import com.example.courseapplication.ui.viewmodel.CourseEntryViewModel
import com.example.courseapplication.ui.viewmodel.CourseUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    isEdit: Boolean = false,
    courseId: Int? = null,
    viewModel: CourseEntryViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(courseId) {
        if (courseId != null) {
            viewModel.loadCourse(courseId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isEdit) "Edit Course" else "Add Course") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (viewModel.isLoadingCategories) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            CourseEntryBody(
                courseUiState = viewModel.courseUiState,
                onCourseValueChange = viewModel::updateUiState,
                onSaveClick = {
                    coroutineScope.launch {
                        if (isEdit) viewModel.updateCourse() else viewModel.saveCourse()
                        navigateBack()
                    }
                },
                categories = viewModel.categories.map { it.name },
                categoryLoadError = viewModel.categoryLoadError,
                modifier = modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun CourseEntryBody(
    courseUiState: CourseUiState,
    onCourseValueChange: (CourseDetails) -> Unit,
    onSaveClick: () -> Unit,
    categories: List<String>,
    categoryLoadError: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (categoryLoadError != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = categoryLoadError,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        CourseInputForm(
            courseDetails = courseUiState.courseDetails,
            onValueChange = onCourseValueChange,
            categories = categories
        )
        Button(
            onClick = onSaveClick,
            enabled = courseUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseInputForm(
    courseDetails: CourseDetails,
    onValueChange: (CourseDetails) -> Unit,
    categories: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = courseDetails.title,
            onValueChange = { onValueChange(courseDetails.copy(title = it)) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = courseDetails.description,
            onValueChange = { onValueChange(courseDetails.copy(description = it)) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = courseDetails.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onValueChange(courseDetails.copy(category = category))
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = courseDetails.lessons,
            onValueChange = { onValueChange(courseDetails.copy(lessons = it)) },
            label = { Text("Number of Lessons") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
