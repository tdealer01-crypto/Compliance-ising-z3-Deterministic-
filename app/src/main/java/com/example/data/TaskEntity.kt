package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val category: String = "Personal",
    val priority: String = "Medium",
    val isCompleted: Boolean = false,
    val dueDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedMinutes: Int = 25
)

enum class TaskCategory(val displayName: String, val iconName: String) {
    ALL("All", "ic_all"),
    WORK("Work", "ic_work"),
    PERSONAL("Personal", "ic_personal"),
    HEALTH("Health", "ic_health"),
    STUDY("Study", "ic_study"),
    FINANCE("Finance", "ic_finance")
}

enum class TaskPriority(val displayName: String) {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low")
}
