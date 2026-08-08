package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allFocusSessions: Flow<List<FocusSessionEntity>> = taskDao.getAllFocusSessions()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Int) = taskDao.deleteTaskById(id)

    suspend fun insertFocusSession(session: FocusSessionEntity) = taskDao.insertFocusSession(session)

    suspend fun checkAndPrepopulateDefaultTasks() {
        val currentTasks = taskDao.getAllTasks().first()
        if (currentTasks.isEmpty()) {
            val defaultTasks = listOf(
                TaskEntity(
                    title = "Code review completed",
                    description = "Pre-Launch checklist item",
                    category = "Work",
                    priority = "High",
                    isCompleted = true,
                    estimatedMinutes = 30
                ),
                TaskEntity(
                    title = "Unit tests passing",
                    description = "Pre-Launch checklist item",
                    category = "Work",
                    priority = "High",
                    isCompleted = true,
                    estimatedMinutes = 30
                ),
                TaskEntity(
                    title = "Integration tests passing",
                    description = "Pre-Launch checklist item",
                    category = "Work",
                    priority = "High",
                    isCompleted = true,
                    estimatedMinutes = 45
                ),
                TaskEntity(
                    title = "Documentation updated",
                    description = "Pre-Launch checklist item",
                    category = "Work",
                    priority = "Medium",
                    isCompleted = true,
                    estimatedMinutes = 60
                ),
                TaskEntity(
                    title = "Security review done",
                    description = "Pre-Launch checklist item",
                    category = "Work",
                    priority = "High",
                    isCompleted = true,
                    estimatedMinutes = 120
                ),
                TaskEntity(
                    title = "Performance benchmarks met",
                    description = "Pre-Launch checklist item",
                    category = "Work",
                    priority = "Medium",
                    isCompleted = true,
                    estimatedMinutes = 60
                ),
                TaskEntity(
                    title = "Deploy to production",
                    description = "Launch Day checklist item",
                    category = "Work",
                    priority = "High",
                    isCompleted = true,
                    estimatedMinutes = 120
                ),
                TaskEntity(
                    title = "Monitor error rates",
                    description = "Launch Day checklist item",
                    category = "Work",
                    priority = "High",
                    isCompleted = true,
                    estimatedMinutes = 60
                ),
                TaskEntity(
                    title = "Monitor latency metrics",
                    description = "Launch Day checklist item",
                    category = "Work",
                    priority = "Medium",
                    isCompleted = true,
                    estimatedMinutes = 60
                ),
                TaskEntity(
                    title = "Announce to stakeholders",
                    description = "Launch Day checklist item",
                    category = "Work",
                    priority = "Low",
                    isCompleted = true,
                    estimatedMinutes = 15
                ),
                TaskEntity(
                    title = "Update status page",
                    description = "Launch Day checklist item",
                    category = "Work",
                    priority = "Medium",
                    isCompleted = true,
                    estimatedMinutes = 15
                ),
                TaskEntity(
                    title = "Verify rollback plan ready",
                    description = "Launch Day checklist item",
                    category = "Work",
                    priority = "High",
                    isCompleted = true,
                    estimatedMinutes = 30
                ),
                TaskEntity(
                    title = "Schedule retrospective",
                    description = "Post-Launch checklist item",
                    category = "Work",
                    priority = "Low",
                    isCompleted = true,
                    estimatedMinutes = 15
                ),
                TaskEntity(
                    title = "Review success metrics",
                    description = "Post-Launch checklist item",
                    category = "Work",
                    priority = "Medium",
                    isCompleted = true,
                    estimatedMinutes = 60
                ),
                TaskEntity(
                    title = "Close out JIRA tickets",
                    description = "Post-Launch checklist item",
                    category = "Work",
                    priority = "Low",
                    isCompleted = true,
                    estimatedMinutes = 45
                ),
                TaskEntity(
                    title = "Archive feature branch",
                    description = "Post-Launch checklist item",
                    category = "Work",
                    priority = "Low",
                    isCompleted = true,
                    estimatedMinutes = 15
                ),
                TaskEntity(
                    title = "Update runbooks",
                    description = "Post-Launch checklist item",
                    category = "Work",
                    priority = "Medium",
                    isCompleted = true,
                    estimatedMinutes = 60
                ),
                TaskEntity(
                    title = "Celebrate the team 🎉",
                    description = "Post-Launch checklist item",
                    category = "Personal",
                    priority = "High",
                    isCompleted = true,
                    estimatedMinutes = 120
                )
            )
            defaultTasks.forEach { taskDao.insertTask(it) }

            // Add sample focus session
            taskDao.insertFocusSession(
                FocusSessionEntity(
                    taskTitle = "Morning 30-Min Cardio & Stretching",
                    durationMinutes = 30
                )
            )
        }
    }
}
