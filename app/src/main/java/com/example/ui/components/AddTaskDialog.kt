package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TaskCategory
import com.example.data.TaskEntity
import com.example.data.TaskPriority

@Composable
fun AddTaskDialog(
    editingTask: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, category: String, priority: String, estimatedMinutes: Int) -> Unit
) {
    var title by remember { mutableStateOf(editingTask?.title ?: "") }
    var description by remember { mutableStateOf(editingTask?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(editingTask?.category ?: "Personal") }
    var selectedPriority by remember { mutableStateOf(editingTask?.priority ?: "Medium") }
    var estimatedMinutes by remember { mutableStateOf((editingTask?.estimatedMinutes ?: 25).toString()) }

    var titleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("add_task_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = if (editingTask != null) "Edit Task" else "Create New Task",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Task Title *") },
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text("Title cannot be empty") }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_description_input"),
                    maxLines = 3
                )

                // Category Selector
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Work", "Personal", "Health", "Study", "Finance").forEach { category ->
                        FilterChip(
                            selected = selectedCategory.equals(category, ignoreCase = true),
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            modifier = Modifier.testTag("category_chip_$category")
                        )
                    }
                }

                // Priority Selector
                Text(
                    text = "Priority Level",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority.equals(priority.displayName, ignoreCase = true),
                            onClick = { selectedPriority = priority.displayName },
                            label = { Text(priority.displayName) },
                            modifier = Modifier.testTag("priority_chip_${priority.displayName}")
                        )
                    }
                }

                // Estimated Duration Input
                OutlinedTextField(
                    value = estimatedMinutes,
                    onValueChange = { if (it.all { char -> char.isDigit() }) estimatedMinutes = it },
                    label = { Text("Estimated Minutes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_duration_input"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        val mins = estimatedMinutes.toIntOrNull() ?: 25
                        onSave(title.trim(), description.trim(), selectedCategory, selectedPriority, mins)
                    }
                },
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text(if (editingTask != null) "Update" else "Save Task")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_task_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
