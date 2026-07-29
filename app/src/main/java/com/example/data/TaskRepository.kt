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
                    title = "Complete Team Sprint Planning",
                    description = "Review roadmap milestones, prioritize backlog tickets, and assign tasks.",
                    category = "Work",
                    priority = "High",
                    isCompleted = false,
                    estimatedMinutes = 45
                ),
                TaskEntity(
                    title = "Morning 30-Min Cardio & Stretching",
                    description = "Hydrate, do 15 mins jog followed by full body flexibility routine.",
                    category = "Health",
                    priority = "Medium",
                    isCompleted = true,
                    estimatedMinutes = 30
                ),
                TaskEntity(
                    title = "Read 2 Chapters of Design Patterns",
                    description = "Focus on Creational and Structural patterns with code examples.",
                    category = "Study",
                    priority = "High",
                    isCompleted = false,
                    estimatedMinutes = 35
                ),
                TaskEntity(
                    title = "Review Monthly Savings & Investments",
                    description = "Check portfolio performance, pay recurring bills, and record expense log.",
                    category = "Finance",
                    priority = "Medium",
                    isCompleted = false,
                    estimatedMinutes = 20
                ),
                TaskEntity(
                    title = "Plan Weekend Family Outing",
                    description = "Research nearby parks, book tickets, and organize packing list.",
                    category = "Personal",
                    priority = "Low",
                    isCompleted = false,
                    estimatedMinutes = 15
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
