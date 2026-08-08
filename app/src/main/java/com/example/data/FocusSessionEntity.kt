package com.example.data

data class FocusSessionEntity(
    val id: String = "",
    val taskTitle: String = "",
    val durationMinutes: Int = 0,
    val completedAt: Long = System.currentTimeMillis()
)
