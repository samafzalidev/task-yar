package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.Category
import com.example.data.Task
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "taskyar_db"
    )
    .fallbackToDestructiveMigration()
    .build()

    val repository = TaskRepository(database)

    val categories: StateFlow<List<Category>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    private val sharedPrefs = application.getSharedPreferences("taskyar_prefs", Context.MODE_PRIVATE)
    
    private val _themeMode = MutableStateFlow(
        sharedPrefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(
        sharedPrefs.getString("language", "en") ?: "en"
    )
    val language: StateFlow<String> = _language.asStateFlow()

    private val _firstLaunch = MutableStateFlow(
        sharedPrefs.getBoolean("first_launch", true)
    )
    val firstLaunch: StateFlow<Boolean> = _firstLaunch.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDefaultCategoriesIfNeeded()
        }
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        sharedPrefs.edit().putString("theme_mode", mode).apply()
    }

    fun setLanguage(lang: String) {
        _language.value = lang
        sharedPrefs.edit().putString("language", lang).apply()
    }

    fun completeFirstLaunch() {
        _firstLaunch.value = false
        sharedPrefs.edit().putBoolean("first_launch", false).apply()
    }

    fun selectCategory(id: Int?) {
        _selectedCategoryId.value = id
    }

    fun addTask(title: String, description: String, categoryId: Int, priority: Int, dueDate: Long? = null) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                categoryId = categoryId,
                status = "TODO",
                priority = priority,
                dueDate = dueDate,
                orderIndex = 0
            )
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTaskStatus(taskId: Int, status: String) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, status)
        }
    }

    fun updateTaskOrder(taskId: Int, orderIndex: Int) {
        viewModelScope.launch {
            repository.updateTaskOrder(taskId, orderIndex)
        }
    }

    fun addCategory(name: String, iconName: String, colorHex: String) {
        viewModelScope.launch {
            val category = Category(
                name = name,
                iconName = iconName,
                colorHex = colorHex,
                isSystem = false
            )
            repository.insertCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}
