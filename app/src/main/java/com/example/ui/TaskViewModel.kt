package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FocusSessionEntity
import com.example.data.TaskEntity
import com.example.data.TaskRepository
import com.example.data.qubo.Constraint
import com.example.data.qubo.CounterfactualResult
import com.example.data.qubo.EngineConfig
import com.example.data.qubo.PolicyRule
import com.example.data.qubo.QuboPolicyEngine
import com.example.data.qubo.QuboSolution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class TaskFilterType {
    ALL, ACTIVE, COMPLETED
}

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filterType = MutableStateFlow(TaskFilterType.ALL)
    val filterType: StateFlow<TaskFilterType> = _filterType

    private val _isAddTaskDialogOpen = MutableStateFlow(false)
    val isAddTaskDialogOpen: StateFlow<Boolean> = _isAddTaskDialogOpen

    private val _editingTask = MutableStateFlow<TaskEntity?>(null)
    val editingTask: StateFlow<TaskEntity?> = _editingTask

    // Focus Timer State
    private val _isFocusTimerOpen = MutableStateFlow(false)
    val isFocusTimerOpen: StateFlow<Boolean> = _isFocusTimerOpen

    private val _focusTimerTask = MutableStateFlow<TaskEntity?>(null)
    val focusTimerTask: StateFlow<TaskEntity?> = _focusTimerTask

    private val _timerTotalSeconds = MutableStateFlow(25 * 60)
    val timerTotalSeconds: StateFlow<Int> = _timerTotalSeconds

    private val _timerSecondsRemaining = MutableStateFlow(25 * 60)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private var timerJob: Job? = null

    // QUBO Policy Engine State
    private val _isQuboSheetOpen = MutableStateFlow(false)
    val isQuboSheetOpen: StateFlow<Boolean> = _isQuboSheetOpen

    private val _quboRules = MutableStateFlow<List<PolicyRule>>(QuboPolicyEngine.FINTECH_RULES)
    val quboRules: StateFlow<List<PolicyRule>> = _quboRules

    private val _quboConstraints = MutableStateFlow<List<Constraint>>(QuboPolicyEngine.FINTECH_CONSTRAINTS)
    val quboConstraints: StateFlow<List<Constraint>> = _quboConstraints

    private val _quboSolution = MutableStateFlow<QuboSolution?>(null)
    val quboSolution: StateFlow<QuboSolution?> = _quboSolution

    private val _counterfactualResult = MutableStateFlow<CounterfactualResult?>(null)
    val counterfactualResult: StateFlow<CounterfactualResult?> = _counterfactualResult

    private val _quboBudget = MutableStateFlow(1500.0)
    val quboBudget: StateFlow<Double> = _quboBudget

    private val _isOptimizing = MutableStateFlow(false)
    val isOptimizing: StateFlow<Boolean> = _isOptimizing

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulateDefaultTasks()
            runQuboOptimization()
        }
    }

    val tasks: StateFlow<List<TaskEntity>> = combine(
        repository.allTasks,
        _selectedCategory,
        _searchQuery,
        _filterType
    ) { allTasks, category, query, filter ->
        allTasks.filter { task ->
            val matchesCategory = (category == "All" || task.category.equals(category, ignoreCase = true))
            val matchesQuery = query.isBlank() || task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                TaskFilterType.ALL -> true
                TaskFilterType.ACTIVE -> !task.isCompleted
                TaskFilterType.COMPLETED -> task.isCompleted
            }
            matchesCategory && matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allRawTasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.allFocusSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(filter: TaskFilterType) {
        _filterType.value = filter
    }

    fun openAddTaskDialog(taskToEdit: TaskEntity? = null) {
        _editingTask.value = taskToEdit
        _isAddTaskDialogOpen.value = true
    }

    fun closeAddTaskDialog() {
        _isAddTaskDialogOpen.value = false
        _editingTask.value = null
    }

    fun saveTask(
        title: String,
        description: String,
        category: String,
        priority: String,
        estimatedMinutes: Int
    ) {
        viewModelScope.launch {
            val current = _editingTask.value
            if (current != null) {
                val updated = current.copy(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    estimatedMinutes = estimatedMinutes
                )
                repository.updateTask(updated)
            } else {
                val newTask = TaskEntity(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    estimatedMinutes = estimatedMinutes
                )
                repository.insertTask(newTask)
            }
            closeAddTaskDialog()
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- Focus Timer Actions ---
    fun openFocusTimer(task: TaskEntity? = null) {
        _focusTimerTask.value = task
        val minutes = task?.estimatedMinutes ?: 25
        _timerTotalSeconds.value = minutes * 60
        _timerSecondsRemaining.value = minutes * 60
        _isTimerRunning.value = false
        timerJob?.cancel()
        _isFocusTimerOpen.value = true
    }

    fun closeFocusTimer() {
        _isFocusTimerOpen.value = false
        timerJob?.cancel()
        _isTimerRunning.value = false
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0 && _isTimerRunning.value) {
                delay(1000L)
                _timerSecondsRemaining.value -= 1
            }
            if (_timerSecondsRemaining.value == 0) {
                _isTimerRunning.value = false
                completeFocusSession()
            }
        }
    }

    private fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timerSecondsRemaining.value = _timerTotalSeconds.value
    }

    private fun completeFocusSession() {
        val task = _focusTimerTask.value
        val durationMins = _timerTotalSeconds.value / 60
        viewModelScope.launch {
            repository.insertFocusSession(
                FocusSessionEntity(
                    taskTitle = task?.title ?: "General Focus Session",
                    durationMinutes = durationMins
                )
            )
            if (task != null && !task.isCompleted) {
                repository.updateTask(task.copy(isCompleted = true))
            }
        }
    }

    // --- QUBO Policy Optimizer Actions ---
    fun openQuboSheet() {
        _isQuboSheetOpen.value = true
        runQuboOptimization()
    }

    fun closeQuboSheet() {
        _isQuboSheetOpen.value = false
    }

    fun setQuboBudget(newBudget: Double) {
        _quboBudget.value = newBudget
        val updatedConstraints = _quboConstraints.value.map { c ->
            if (c is Constraint.MaxCost) c.copy(budget = newBudget, description = "Annual budget $$newBudget") else c
        }
        _quboConstraints.value = updatedConstraints
        runQuboOptimization()
    }

    fun runQuboOptimization() {
        viewModelScope.launch {
            _isOptimizing.value = true
            val solution = withContext(Dispatchers.Default) {
                QuboPolicyEngine.solve(
                    rules = _quboRules.value,
                    constraints = _quboConstraints.value,
                    config = EngineConfig(seed = 42L, maxIterations = 5000, coolingRate = 0.995)
                )
            }
            _quboSolution.value = solution
            _isOptimizing.value = false
        }
    }

    fun runCounterfactualAnalysis(alteredBudget: Double) {
        viewModelScope.launch {
            val altConstraints = _quboConstraints.value.map { c ->
                if (c is Constraint.MaxCost) c.copy(budget = alteredBudget, description = "Altered budget $$alteredBudget") else c
            }
            val cfResult = withContext(Dispatchers.Default) {
                QuboPolicyEngine.counterfactual(
                    rules = _quboRules.value,
                    originalConstraints = _quboConstraints.value,
                    alteredConstraints = altConstraints,
                    config = EngineConfig(seed = 42L, maxIterations = 5000)
                )
            }
            _counterfactualResult.value = cfResult
        }
    }

    fun applyQuboSolutionToTasks() {
        val sol = _quboSolution.value ?: return
        viewModelScope.launch {
            sol.activeRules.forEach { rule ->
                val category = when (rule.category) {
                    "IDENTITY", "NETWORK" -> "Work"
                    "DATA_PROTECTION" -> "Finance"
                    "MONITORING", "COMPLIANCE" -> "Study"
                    "HEALTH" -> "Health"
                    else -> "Personal"
                }
                val newTask = TaskEntity(
                    title = "QUBO Policy: ${rule.name}",
                    description = "${rule.description} [Value: ${rule.businessValue.toInt()}, Risk -${rule.riskReduction.toInt()}%]",
                    category = category,
                    priority = if (rule.cost >= 300) "High" else "Medium",
                    isCompleted = false,
                    estimatedMinutes = (rule.cost / 10).toInt().coerceIn(15, 60)
                )
                repository.insertTask(newTask)
            }
            closeQuboSheet()
        }
    }
}
