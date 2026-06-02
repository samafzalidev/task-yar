package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TaskRepository(private val database: AppDatabase) {
    val categories: Flow<List<Category>> = database.categoryDao().getAllCategories()
    val tasks: Flow<List<Task>> = database.taskDao().getAllTasks()

    suspend fun getCategoryById(id: Int): Category? {
        return database.categoryDao().getCategoryById(id)
    }

    suspend fun insertCategory(category: Category): Long {
        return database.categoryDao().insertCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        database.categoryDao().deleteCategory(category)
    }

    suspend fun insertTask(task: Task): Long {
        return database.taskDao().insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        database.taskDao().updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        database.taskDao().deleteTask(task)
    }

    suspend fun updateTaskStatus(taskId: Int, status: String) {
        database.taskDao().updateTaskStatus(taskId, status)
    }

    suspend fun updateTaskOrder(taskId: Int, orderIndex: Int) {
        database.taskDao().updateTaskOrder(taskId, orderIndex)
    }

    suspend fun seedDefaultCategoriesIfNeeded() {
        val existing = database.categoryDao().getAllCategories().first()
        if (existing.isEmpty()) {
            val defaults = listOf(
                Category(name = "Personal", iconName = "Person", colorHex = "#4CAF50", isSystem = true),
                Category(name = "Work", iconName = "Business", colorHex = "#2196F3", isSystem = true),
                Category(name = "Shopping", iconName = "ShoppingCart", colorHex = "#FF9800", isSystem = true),
                Category(name = "Health", iconName = "Fitness", colorHex = "#E91E63", isSystem = true),
                Category(name = "Study", iconName = "Book", colorHex = "#9C27B0", isSystem = true)
            )
            for (category in defaults) {
                database.categoryDao().insertCategory(category)
            }
        }
    }
}
