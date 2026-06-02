package com.example.ui.dragdrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import com.example.data.Task

class DragAndDropState {
    var isDragging by mutableStateOf(false)
    var draggedTask by mutableStateOf<Task?>(null)
    var dragOffset by mutableStateOf(Offset.Zero)
    var dragStartPosition by mutableStateOf(Offset.Zero)
    var currentHoveredZoneId by mutableStateOf<String?>(null)

    private val dropZones = mutableStateMapOf<String, Rect>()

    fun registerDropZone(id: String, rect: Rect) {
        dropZones[id] = rect
    }

    fun unregisterDropZone(id: String) {
        dropZones.remove(id)
    }

    fun startDragging(task: Task, initialPosInWindow: Offset) {
        draggedTask = task
        dragStartPosition = initialPosInWindow
        dragOffset = Offset.Zero
        isDragging = true
        currentHoveredZoneId = null
    }

    fun updateDrag(dragAmount: Offset) {
        if (!isDragging) return
        dragOffset += dragAmount
        val currentPosition = dragStartPosition + dragOffset
        currentHoveredZoneId = dropZones.entries.find { entry ->
            entry.value.contains(currentPosition)
        }?.key
    }

    fun endDragging(onDropAction: (Task, String) -> Unit) {
        val task = draggedTask
        val zoneId = currentHoveredZoneId
        if (isDragging && task != null && zoneId != null) {
            onDropAction(task, zoneId)
        }
        isDragging = false
        draggedTask = null
        dragOffset = Offset.Zero
        dragStartPosition = Offset.Zero
        currentHoveredZoneId = null
    }
}

val LocalDragAndDropState = staticCompositionLocalOf { DragAndDropState() }

@Composable
fun Modifier.dropZone(
    id: String,
    dragAndDropState: DragAndDropState
): Modifier {
    DisposableEffect(id) {
        onDispose {
            dragAndDropState.unregisterDropZone(id)
        }
    }
    return this.onGloballyPositioned { layoutCoordinates ->
        if (layoutCoordinates.isAttached) {
            val position = layoutCoordinates.positionInWindow()
            val size = layoutCoordinates.size
            val rect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + size.width,
                bottom = position.y + size.height
            )
            dragAndDropState.registerDropZone(id, rect)
        }
    }
}

@Composable
fun Modifier.dragSourceItem(
    task: Task,
    dragAndDropState: DragAndDropState,
    onLongPressHaptic: () -> Unit = {}
): Modifier {
    var positionInWindow by remember { mutableStateOf(Offset.Zero) }
    return this
        .onGloballyPositioned { layoutCoordinates ->
            if (layoutCoordinates.isAttached) {
                positionInWindow = layoutCoordinates.positionInWindow()
            }
        }
        .pointerInput(task.id) {
            detectDragGesturesAfterLongPress(
                onDragStart = { localOffset ->
                    onLongPressHaptic()
                    val absoluteStart = positionInWindow + localOffset
                    dragAndDropState.startDragging(task, absoluteStart)
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    dragAndDropState.updateDrag(dragAmount)
                },
                onDragEnd = {
                    dragAndDropState.endDragging { taskItem, zoneId ->
                        // The UI handles state change
                    }
                },
                onDragCancel = {
                    dragAndDropState.endDragging { _, _ -> }
                }
            )
        }
}
