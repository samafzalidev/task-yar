package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Category
import com.example.data.Task
import com.example.ui.dragdrop.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TaskViewModel
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TaskViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val language by viewModel.language.collectAsState()

            MyApplicationTheme(themeMode = themeMode) {
                val layoutDir = if (language == "fa") LayoutDirection.Rtl else LayoutDirection.Ltr
                CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        TodoAppMainScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

fun getDateString(lang: String): String {
    return if (lang == "fa") {
        getPersianDateString()
    } else {
        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val year = cal.get(Calendar.YEAR)
        
        val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        
        val dayStr = if (dayOfWeek in 1..7) days[dayOfWeek - 1] else "Today"
        val monthStr = if (month in 0..11) months[month] else ""
        
        "$dayStr, $monthStr $day $year"
    }
}

fun getPersianDateString(): String {
    val cal = Calendar.getInstance()
    val gYear = cal.get(Calendar.YEAR)
    val gMonth = cal.get(Calendar.MONTH) + 1
    val gDay = cal.get(Calendar.DAY_OF_MONTH)
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

    val weekdays = listOf(
        "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه"
    )
    val weekdayStr = weekdays[if (dayOfWeek >= 1) dayOfWeek - 1 else 0]

    val gy = gYear - 1600
    val gm = gMonth - 1
    val gd = gDay - 1

    var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
    for (i in 0 until gm) {
        gDayNo += when (i) {
            0 -> 31
            1 -> if ((gYear % 4 == 0 && gYear % 100 != 0) || (gYear % 400 == 0)) 29 else 28
            2 -> 31
            3 -> 30
            4 -> 31
            5 -> 30
            6 -> 31
            7 -> 31
            8 -> 30
            9 -> 31
            10 -> 30
            else -> 31
        }
    }
    gDayNo += gd

    var jDayNo = gDayNo - 79
    val jNp = jDayNo / 12053
    jDayNo %= 12053

    var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
    jDayNo %= 1461

    if (jDayNo >= 366) {
        jy += (jDayNo - 1) / 365
        jDayNo = (jDayNo - 1) % 365
    }

    var jm = 0
    var jd = 0
    for (i in 0 until 12) {
        val daysInMonth = if (i < 6) 31 else if (i < 11) 30 else 29
        if (jDayNo < daysInMonth) {
            jm = i + 1
            jd = jDayNo + 1
            break
        }
        jDayNo -= daysInMonth
    }

    val jalaliMonths = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )
    val jMonthStr = jalaliMonths[jm - 1]

    return "$weekdayStr، ${jd.toPersianDigits()} $jMonthStr ${jy.toPersianDigits()}"
}

fun String.toPersianDigits(): String {
    val persianDigits = mapOf(
        '0' to '۰', '1' to '۱', '2' to '۲', '3' to '۳', '4' to '۴',
        '5' to '۵', '6' to '۶', '7' to '۷', '8' to '۸', '9' to '۹'
    )
    return this.map { persianDigits[it] ?: it }.joinToString("")
}

fun Int.toPersianDigits(): String = this.toString().toPersianDigits()

fun String.toLocalizedDigits(lang: String): String {
    return if (lang == "fa") this.toPersianDigits() else this
}

fun Int.toLocalizedDigits(lang: String): String {
    return this.toString().toLocalizedDigits(lang)
}

fun hexToColor(hex: String, fallback: Color = Color.Gray): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        fallback
    }
}

fun getIconByName(name: String): ImageVector {
    return when (name) {
        "Person" -> Icons.Default.Person
        "Business" -> Icons.Default.Business
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "Fitness" -> Icons.Default.FitnessCenter
        "Book" -> Icons.Default.MenuBook
        "Star" -> Icons.Default.Star
        "Calendar" -> Icons.Default.CalendarToday
        else -> Icons.Default.MoreHoriz
    }
}

fun trans(key: String, lang: String): String {
    val enMap = mapOf(
        "app_title" to "Task Yar",
        "all_tasks" to "All",
        "progress_title" to "Task Progress",
        "progress_desc" to "%s of %s tasks completed successfully.",
        "categories" to "Categories",
        "add_category" to "Create Category",
        "to_do" to "To Do",
        "doing" to "Doing",
        "done" to "Done",
        "no_tasks" to "No tasks in this section",
        "no_tasks_desc" to "You can pull tasks or tap + to record a task.",
        "add_task" to "Add Task",
        "drag_hint" to "Drag the task onto a status target to update it",
        "delete_task" to "Delete Task",
        "task_deleted" to "Task deleted",
        "status_updated" to "Task state updated",
        "category_added" to "New category created",
        "task_added" to "New task recorded successfully",
        "task_name" to "Task Title",
        "description" to "Description (Optional)",
        "category_label" to "Task Category",
        "priority_label" to "Task Priority",
        "save" to "Save",
        "cancel" to "Cancel",
        "low" to "Low",
        "medium" to "Medium",
        "high" to "High",
        "theme_settings" to "Dark Mode Settings",
        "theme_system" to "System Auto",
        "theme_light" to "Light",
        "theme_dark" to "Dark",
        "category_title" to "Category Title",
        "icon_selection" to "Select Icon",
        "color_selection" to "Select Color",
        "dev_by" to "Developer: Sam Afzali",
        "choose_lang" to "Welcome to Task Yar",
        "choose_lang_desc" to "Please select your workspace language.",
        "all_badge" to "All",
        "confirm" to "Get Started",
        "lang_settings" to "Language Settings",
        "lang_en" to "English",
        "lang_fa" to "فارسی",
        "cat_personal" to "Personal",
        "cat_work" to "Work",
        "cat_shopping" to "Shopping",
        "cat_health" to "Health",
        "cat_study" to "Study"
    )
    
    val faMap = mapOf(
        "app_title" to "تسک یار",
        "all_tasks" to "همه",
        "progress_title" to "روند انجام کارها",
        "progress_desc" to "%s از %s تسک با موفقیت انجام شده است.",
        "categories" to "دسته‌بندی‌ها",
        "add_category" to "افزودن دسته‌بندی",
        "to_do" to "برای انجام",
        "doing" to "در حال کار",
        "done" to "انجام شد",
        "no_tasks" to "تسک فعالی وجود ندارد",
        "no_tasks_desc" to "یک تسک بنویسید یا تسکی را با درگ به اینجا بیاورید.",
        "add_task" to "ثبت تسک جدید",
        "drag_hint" to "کشیدن تسک روی اهداف زیر برای تغییر وضعیت سریع",
        "delete_task" to "حذف تسک",
        "task_deleted" to "تسک با موفقیت حذف شد",
        "status_updated" to "وضعیت تسک آپدیت شد",
        "category_added" to "دسته‌بندی ثبت شد",
        "task_added" to "تسک جدید ثبت شد",
        "task_name" to "عنوان تسک",
        "description" to "توضیحات (اختیاری)",
        "category_label" to "دسته‌بندی مربوطه",
        "priority_label" to "اولویت انجام",
        "save" to "ذخیره",
        "cancel" to "انصراف",
        "low" to "کم اهمیت",
        "medium" to "متوسط",
        "high" to "فوری و مهم",
        "theme_settings" to "تنظیم ظاهری پوسته",
        "theme_system" to "هماهنگ با سیستم",
        "theme_light" to "حالت روشن",
        "theme_dark" to "حالت تاریک",
        "category_title" to "عنوان دسته‌بندی",
        "icon_selection" to "آیکون مناسب",
        "color_selection" to "رنگ متمایز کننده",
        "dev_by" to "توسعه دهنده: سام افضلی",
        "choose_lang" to "به تسک یار خوش آمدید",
        "choose_lang_desc" to "لطفاً ابتدا زبان مورد نظر را انتخاب کنید.",
        "all_badge" to "همه",
        "confirm" to "ورود به تسک یار",
        "lang_settings" to "تنظیمات زبان",
        "lang_en" to "English",
        "lang_fa" to "فارسی",
        "cat_personal" to "شخصی",
        "cat_work" to "کاری",
        "cat_shopping" to "خرید",
        "cat_health" to "سلامت و ورزش",
        "cat_study" to "مطالعه و درس"
    )
    
    return if (lang == "fa") (faMap[key] ?: key) else (enMap[key] ?: key)
}

fun getLocalizedCategoryName(name: String, lang: String): String {
    return when (name) {
        "شخصی", "Personal" -> trans("cat_personal", lang)
        "کاری", "Work" -> trans("cat_work", lang)
        "خرید", "Shopping" -> trans("cat_shopping", lang)
        "سلامت و ورزش", "Health & Fitness", "Health" -> trans("cat_health", lang)
        "مطالعه و درس", "Study & Learning", "Study" -> trans("cat_study", lang)
        else -> name
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoAppMainScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val selectedCatId by viewModel.selectedCategoryId.collectAsState()
    val currentThemeMode by viewModel.themeMode.collectAsState()
    val lang by viewModel.language.collectAsState()
    val firstLaunch by viewModel.firstLaunch.collectAsState()

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var showAddTaskSheet by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showThemeMenu by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }

    var selectedStatusTab by remember { mutableStateOf("TODO") }
    val dragAndDropState = remember { DragAndDropState() }

    val filteredTasks = tasks.filter { task ->
        selectedCatId == null || task.categoryId == selectedCatId
    }

    val todoCount = filteredTasks.count { it.status == "TODO" }
    val doingCount = filteredTasks.count { it.status == "DOING" }
    val doneCount = filteredTasks.count { it.status == "DONE" }
    val totalCount = filteredTasks.size
    val completionRatio = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f

    val isDark = when (currentThemeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }

    val scaffoldBg = if (isDark) Color(0xFF0F1115) else Color(0xFFF1F5F9)
    val cardContainerBg = if (isDark) Color(0x0CFFFFFF) else Color(0xB3FFFFFF)
    val cardBorderColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f)
    val headerTextColor = if (isDark) Color.White else Color(0xFF0F1115)
    val textMutedColor = if (isDark) Color(0xFF919194) else Color(0xFF64748B)

    CompositionLocalProvider(LocalDragAndDropState provides dragAndDropState) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(scaffoldBg)
                .drawBehind {
                    val blueCenter = Offset(x = -size.width * 0.1f, y = size.height * 0.1f)
                    val alphaBlue = if (isDark) 0x293B82F6 else 0x143B82F6
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(alphaBlue), Color.Transparent),
                            center = blueCenter,
                            radius = size.width * 0.85f
                        ),
                        center = blueCenter,
                        radius = size.width * 0.85f
                    )

                    val purpleCenter = Offset(x = size.width * 1.1f, y = size.height * 0.8f)
                    val alphaPurple = if (isDark) 0x248B5CF6 else 0x148B5CF6
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(alphaPurple), Color.Transparent),
                            center = purpleCenter,
                            radius = size.width * 0.85f
                        ),
                        center = purpleCenter,
                        radius = size.width * 0.85f
                    )
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("header_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardContainerBg),
                    border = BorderStroke(1.dp, cardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = trans("app_title", lang),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = headerTextColor,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = textMutedColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = getDateString(lang),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textMutedColor
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { showLanguageMenu = true },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                                        .border(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = headerTextColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { showThemeMenu = true },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f))
                                        .border(1.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f), CircleShape)
                                        .testTag("theme_btn")
                                ) {
                                    val currentThemeIcon = when (currentThemeMode) {
                                        "LIGHT" -> Icons.Default.LightMode
                                        "DARK" -> Icons.Default.DarkMode
                                        else -> Icons.Default.Settings
                                    }
                                    Icon(
                                        imageVector = currentThemeIcon,
                                        contentDescription = null,
                                        tint = headerTextColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.02f))
                                .border(1.dp, cardBorderColor, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(54.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val progressAnimated by animateFloatAsState(
                                    targetValue = completionRatio,
                                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                                    label = "progress"
                                )
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = if (isDark) Color.White.copy(alpha = 0.07f) else Color.Black.copy(alpha = 0.07f),
                                        style = Stroke(width = 6.dp.toPx())
                                    )
                                    drawArc(
                                        color = Color(0xFF3B82F6),
                                        startAngle = -90f,
                                        sweepAngle = 360f * progressAnimated,
                                        useCenter = false,
                                        style = Stroke(width = 6.dp.toPx())
                                    )
                                }
                                Text(
                                    text = "${(completionRatio * 100).toInt()}%".toLocalizedDigits(lang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = headerTextColor
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                val totalLocalized = totalCount.toLocalizedDigits(lang)
                                val doneLocalized = doneCount.toLocalizedDigits(lang)
                                Text(
                                    text = trans("progress_title", lang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = headerTextColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = trans("progress_desc", lang).replace("%s", doneLocalized).replace("%s", totalLocalized),
                                    fontSize = 12.sp,
                                    color = textMutedColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = trans("categories", lang),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = headerTextColor
                        )
                        IconButton(
                            onClick = { showAddCategoryDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        item {
                            val isSelected = selectedCatId == null
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(null) },
                                label = {
                                    Text(
                                        text = "${trans("all_badge", lang)} (${tasks.size})".toLocalizedDigits(lang),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = cardBorderColor,
                                    selectedBorderColor = Color(0x4D3B82F6),
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.dp
                                ),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.02f),
                                    labelColor = textMutedColor,
                                    iconColor = textMutedColor,
                                    selectedContainerColor = Color(0x263B82F6),
                                    selectedLabelColor = Color(0xFF3B82F6),
                                    selectedLeadingIconColor = Color(0xFF3B82F6)
                                )
                            )
                        }

                        items(categories) { category ->
                            val isSelected = selectedCatId == category.id
                            val catColor = hexToColor(category.colorHex, MaterialTheme.colorScheme.secondary)
                            val catPendingCount = tasks.count { it.categoryId == category.id && it.status != "DONE" }

                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(category.id) },
                                label = {
                                    Text(
                                        text = "${getLocalizedCategoryName(category.name, lang)} (${catPendingCount})".toLocalizedDigits(lang),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getIconByName(category.iconName),
                                        contentDescription = null,
                                        tint = if (isSelected) catColor else catColor.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = cardBorderColor,
                                    selectedBorderColor = catColor.copy(alpha = 0.35f),
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.dp
                                ),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.02f),
                                    labelColor = textMutedColor,
                                    iconColor = catColor.copy(alpha = 0.7f),
                                    selectedContainerColor = catColor.copy(alpha = 0.2f),
                                    selectedLabelColor = catColor,
                                    selectedLeadingIconColor = catColor
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp)
                ) {
                    TabRow(
                        selectedTabIndex = when (selectedStatusTab) {
                            "TODO" -> 0
                            "DOING" -> 1
                            else -> 2
                        },
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(
                                    tabPositions[when (selectedStatusTab) {
                                        "TODO" -> 0
                                        "DOING" -> 1
                                        else -> 2
                                    }]
                                ),
                                color = Color(0xFF3B82F6)
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedStatusTab == "TODO",
                            onClick = { selectedStatusTab = "TODO" },
                            selectedContentColor = headerTextColor,
                            unselectedContentColor = textMutedColor,
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = trans("to_do", lang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Badge(
                                        containerColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                                        contentColor = headerTextColor
                                    ) {
                                        Text(text = todoCount.toLocalizedDigits(lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        )
                        Tab(
                            selected = selectedStatusTab == "DOING",
                            onClick = { selectedStatusTab = "DOING" },
                            selectedContentColor = headerTextColor,
                            unselectedContentColor = textMutedColor,
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = trans("doing", lang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Badge(
                                        containerColor = Color(0x26F59E0B),
                                        contentColor = Color(0xFFF59E0B)
                                    ) {
                                        Text(text = doingCount.toLocalizedDigits(lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        )
                        Tab(
                            selected = selectedStatusTab == "DONE",
                            onClick = { selectedStatusTab = "DONE" },
                            selectedContentColor = headerTextColor,
                            unselectedContentColor = textMutedColor,
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = trans("done", lang),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Badge(
                                        containerColor = Color(0x2610B981),
                                        contentColor = Color(0xFF10B981)
                                    ) {
                                        Text(
                                            text = doneCount.toLocalizedDigits(lang),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val tabFilteredTasks = filteredTasks.filter { it.status == selectedStatusTab }

                    if (tabFilteredTasks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Inventory,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(72.dp)
                                        .alpha(0.18f),
                                    tint = headerTextColor
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = trans("no_tasks", lang),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textMutedColor,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = trans("no_tasks_desc", lang),
                                    fontSize = 12.sp,
                                    color = textMutedColor.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 120.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(tabFilteredTasks, key = { it.id }) { task ->
                                val taskCategory = categories.find { it.id == task.categoryId }
                                val opacity = if (dragAndDropState.isDragging && dragAndDropState.draggedTask?.id == task.id) 0.3f else 1f

                                TaskCardItem(
                                    task = task,
                                    category = taskCategory,
                                    lang = lang,
                                    isDark = isDark,
                                    modifier = Modifier
                                        .alpha(opacity)
                                        .dragSourceItem(task, dragAndDropState) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                    onCompleteChanged = { isChecked ->
                                        val nextStatus = if (isChecked) "DONE" else "TODO"
                                        viewModel.updateTaskStatus(task.id, nextStatus)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = trans("dev_by", lang),
                    color = textMutedColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(
                            color = scaffoldBg.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            cardBorderColor.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { uriHandler.openUri("https://samafzali.ir") }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            FloatingActionButton(
                onClick = { showAddTaskSheet = true },
                containerColor = Color(0xFF3B82F6),
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .offset(y = (-30).dp)
                    .testTag("add_task_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }

            AnimatedVisibility(
                visible = dragAndDropState.isDragging,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .offset(y = (-30).dp)
            ) {
                Card(
                    modifier = Modifier.shadow(0.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFA151821) else Color(0xFAECEFF1)
                    ),
                    border = BorderStroke(1.dp, Color(0x4D3B82F6))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = trans("drag_hint", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textMutedColor,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val activeTask = dragAndDropState.draggedTask

                            if (activeTask != null && activeTask.status != "TODO") {
                                DragDropTargetBadge(
                                    zoneId = "TODO",
                                    label = trans("to_do", lang),
                                    glowingColor = Color(0xFF3B82F6),
                                    icon = Icons.Default.Pending,
                                    dragAndDropState = dragAndDropState,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (activeTask != null && activeTask.status != "DOING") {
                                DragDropTargetBadge(
                                    zoneId = "DOING",
                                    label = trans("doing", lang),
                                    glowingColor = Color(0xFFF59E0B),
                                    icon = Icons.Default.Autorenew,
                                    dragAndDropState = dragAndDropState,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (activeTask != null && activeTask.status != "DONE") {
                                DragDropTargetBadge(
                                    zoneId = "DONE",
                                    label = trans("done", lang),
                                    glowingColor = Color(0xFF10B981),
                                    icon = Icons.Default.CheckCircle,
                                    dragAndDropState = dragAndDropState,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            DragDropTargetBadge(
                                zoneId = "DELETE",
                                label = trans("delete_task", lang),
                                glowingColor = Color(0xFFEF4444),
                                icon = Icons.Default.Delete,
                                dragAndDropState = dragAndDropState,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (dragAndDropState.isDragging && dragAndDropState.draggedTask != null) {
                val liftedTask = dragAndDropState.draggedTask!!
                val taskCategory = categories.find { it.id == liftedTask.categoryId }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (dragAndDropState.dragOffset.x).toInt() + 40,
                                    (dragAndDropState.dragOffset.y).toInt() + 180
                                )
                            }
                            .width(280.dp)
                            .shadow(24.dp, RoundedCornerShape(16.dp))
                    ) {
                        TaskCardItem(
                            task = liftedTask,
                            category = taskCategory,
                            lang = lang,
                            isDark = isDark,
                            modifier = Modifier.alpha(0.95f),
                            onCompleteChanged = {}
                        )
                    }
                }
            }

            if (showAddCategoryDialog) {
                AddCategoryDialog(
                    lang = lang,
                    isDark = isDark,
                    onDismiss = { showAddCategoryDialog = false },
                    onSave = { name, icon, color ->
                        viewModel.addCategory(name, icon, color)
                        showAddCategoryDialog = false
                        Toast.makeText(context, trans("category_added", lang), Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showAddTaskSheet) {
                AddTaskDialog(
                    categories = categories,
                    lang = lang,
                    isDark = isDark,
                    onDismiss = { showAddTaskSheet = false },
                    onSave = { title, desc, catId, priority ->
                        viewModel.addTask(title, desc, catId, priority)
                        showAddTaskSheet = false
                        Toast.makeText(context, trans("task_added", lang), Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showThemeMenu) {
                ThemeSelectionDialog(
                    currentMode = currentThemeMode,
                    lang = lang,
                    isDark = isDark,
                    onDismiss = { showThemeMenu = false },
                    onSelect = { selectedMode ->
                        viewModel.setThemeMode(selectedMode)
                        showThemeMenu = false
                    }
                )
            }

            if (showLanguageMenu) {
                LanguageSelectionDialog(
                    currentLang = lang,
                    isDark = isDark,
                    onDismiss = { showLanguageMenu = false },
                    onSelect = { selectedLang ->
                        viewModel.setLanguage(selectedLang)
                        showLanguageMenu = false
                    }
                )
            }

            if (firstLaunch) {
                WelcomeLanguageDialog(
                    onSelect = { selectedLang ->
                        viewModel.setLanguage(selectedLang)
                        viewModel.completeFirstLaunch()
                    }
                )
            }
        }
    }

    LaunchedEffect(dragAndDropState.isDragging) {
        if (!dragAndDropState.isDragging && dragAndDropState.draggedTask != null) {
            val task = dragAndDropState.draggedTask!!
            val zone = dragAndDropState.currentHoveredZoneId
            if (zone != null) {
                if (zone == "DELETE") {
                    viewModel.deleteTask(task)
                    Toast.makeText(context, trans("task_deleted", lang), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.updateTaskStatus(task.id, zone)
                    Toast.makeText(context, trans("status_updated", lang), Toast.LENGTH_SHORT).show()
                }
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }
}

@Composable
fun TaskCardItem(
    task: Task,
    category: Category?,
    lang: String,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onCompleteChanged: (Boolean) -> Unit
) {
    val catColor = if (category != null) {
        hexToColor(category.colorHex, MaterialTheme.colorScheme.secondary)
    } else {
        MaterialTheme.colorScheme.secondary
    }

    val isDone = task.status == "DONE"
    val containerBg = if (isDark) Color(0x0CFFFFFF) else Color(0xF2FFFFFF)
    val taskTitleColor = if (isDone) {
        if (isDark) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.35f)
    } else {
        if (isDark) Color.White else Color(0xFF0F1115)
    }
    val descriptionColor = if (isDark) Color(0xFF919194) else Color(0xFF5F6E80)

    val borderStrokeColor = if (isDone) {
        if (isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.04f)
    } else {
        catColor.copy(alpha = 0.22f)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(0.dp, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(1.dp, borderStrokeColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        when (task.priority) {
                            1 -> Color(0xFF10B981)
                            2 -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        }
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textDecoration = if (isDone) TextDecoration.LineThrough else null,
                        color = taskTitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (category != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(catColor.copy(alpha = 0.08f))
                                .border(1.dp, catColor.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = getLocalizedCategoryName(category.name, lang),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = catColor
                            )
                        }
                    }
                }

                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = descriptionColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            val priorityText = when (task.priority) {
                1 -> trans("low", lang)
                2 -> trans("medium", lang)
                else -> trans("high", lang)
            }
            val priorityColor = when (task.priority) {
                1 -> Color(0xFF10B981)
                2 -> Color(0xFFF59E0B)
                else -> Color(0xFFEF4444)
            }
            val priorityBg = when (task.priority) {
                1 -> Color(0x1F10B981)
                2 -> Color(0x1FF59E0B)
                else -> Color(0x1FEF4444)
            }
            Text(
                text = priorityText,
                color = priorityColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(priorityBg)
                    .border(1.dp, priorityColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = isDone,
                onCheckedChange = onCompleteChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF3B82F6),
                    uncheckedColor = if (isDark) Color.White.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.2f),
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.DragIndicator,
                contentDescription = null,
                tint = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DragDropTargetBadge(
    zoneId: String,
    label: String,
    glowingColor: Color,
    icon: ImageVector,
    dragAndDropState: DragAndDropState,
    modifier: Modifier = Modifier
) {
    val isHovered = dragAndDropState.currentHoveredZoneId == zoneId
    val animatedScale by animateFloatAsState(targetValue = if (isHovered) 1.05f else 1f, label = "drop_scale")
    val animatedBgAlpha by animateFloatAsState(targetValue = if (isHovered) 0.16f else 0.05f, label = "drop_alpha")

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .dropZone(zoneId, dragAndDropState)
            .clip(RoundedCornerShape(16.dp))
            .background(glowingColor.copy(alpha = animatedBgAlpha))
            .drawBehind {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                drawRoundRect(
                    color = if (isHovered) glowingColor else glowingColor.copy(alpha = 0.4f),
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )
            }
            .padding(vertical = 14.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = glowingColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = glowingColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AddCategoryDialog(
    lang: String,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("Person") }
    var selectedColorHex by remember { mutableStateOf("#6366F1") }

    val iconsList = listOf("Person", "Business", "ShoppingCart", "Fitness", "Book", "Star", "Calendar")
    val colorsList = listOf("#6366F1", "#06B6D4", "#10B981", "#FF9800", "#E91E63", "#9C27B0")

    val cardColor = if (isDark) Color(0xFA151821) else Color(0xFAF8FAFC)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
    val labelColor = if (isDark) Color.White else Color(0xFF0F1115)
    val supportColor = if (isDark) Color(0xFF919194) else Color(0xFF475569)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = trans("add_category", lang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = labelColor,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(trans("category_title", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = labelColor,
                        unfocusedTextColor = labelColor,
                        focusedLabelColor = Color(0xFF3B82F6),
                        unfocusedLabelColor = supportColor,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = cardBorder,
                        focusedContainerColor = Color(0x0AFFFFFF),
                        unfocusedContainerColor = Color(0x0AFFFFFF)
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = trans("icon_selection", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = supportColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(iconsList) { iconName ->
                        val isSelected = selectedIconName == iconName
                        IconButton(
                            onClick = { selectedIconName = iconName },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color(0x263B82F6)
                                    else if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.04f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF3B82F6) else Color.Transparent,
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = getIconByName(iconName),
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF3B82F6) else supportColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = trans("color_selection", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = supportColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (colorHex in colorsList) {
                        val isSelected = selectedColorHex == colorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(hexToColor(colorHex))
                                .border(
                                    2.dp,
                                    if (isSelected) (if (isDark) Color.White else Color.Black) else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { selectedColorHex = colorHex }
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = supportColor)
                    ) {
                        Text(trans("cancel", lang))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotEmpty()) {
                                onSave(name, selectedIconName, selectedColorHex)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(trans("save", lang))
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    categories: List<Category>,
    lang: String,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf<Int?>(categories.firstOrNull()?.id) }
    var priority by remember { mutableStateOf(1) }

    val cardColor = if (isDark) Color(0xFA151821) else Color(0xFAF8FAFC)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
    val labelColor = if (isDark) Color.White else Color(0xFF0F1115)
    val supportColor = if (isDark) Color(0xFF919194) else Color(0xFF475569)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = trans("add_task", lang),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = labelColor,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(trans("task_name", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = labelColor,
                        unfocusedTextColor = labelColor,
                        focusedLabelColor = Color(0xFF3B82F6),
                        unfocusedLabelColor = supportColor,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = cardBorder,
                        focusedContainerColor = Color(0x0AFFFFFF),
                        unfocusedContainerColor = Color(0x0AFFFFFF)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(trans("description", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = labelColor,
                        unfocusedTextColor = labelColor,
                        focusedLabelColor = Color(0xFF3B82F6),
                        unfocusedLabelColor = supportColor,
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = cardBorder,
                        focusedContainerColor = Color(0x0AFFFFFF),
                        unfocusedContainerColor = Color(0x0AFFFFFF)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = trans("category_label", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = supportColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCatId == category.id
                        val catColor = hexToColor(category.colorHex, MaterialTheme.colorScheme.secondary)

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCatId = category.id },
                            label = { Text(getLocalizedCategoryName(category.name, lang), fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = getIconByName(category.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) catColor else catColor.copy(alpha = 0.8f)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = cardBorder,
                                selectedBorderColor = catColor.copy(alpha = 0.40f),
                                borderWidth = 1.dp,
                                selectedBorderWidth = 1.dp
                            ),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (isDark) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.02f),
                                labelColor = supportColor,
                                iconColor = catColor.copy(alpha = 0.7f),
                                selectedContainerColor = catColor.copy(alpha = 0.2f),
                                selectedLabelColor = catColor,
                                selectedLeadingIconColor = catColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = trans("priority_label", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = supportColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val priorities = listOf(
                        Triple(1, "low", trans("low", lang)),
                        Triple(2, "medium", trans("medium", lang)),
                        Triple(3, "high", trans("high", lang))
                    )

                    for ((pVal, keyName, label) in priorities) {
                        val isSelected = priority == pVal
                        val itemColor = when (pVal) {
                            1 -> Color(0xFF10B981)
                            2 -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) itemColor.copy(alpha = 0.18f)
                                    else if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.04f)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) itemColor else cardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { priority = pVal }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) itemColor else supportColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = supportColor)
                    ) {
                        Text(trans("cancel", lang))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotEmpty() && selectedCatId != null) {
                                onSave(title, desc, selectedCatId!!, priority)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(trans("save", lang))
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentMode: String,
    lang: String,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val cardColor = if (isDark) Color(0xFA151821) else Color(0xFAF8FAFC)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
    val labelColor = if (isDark) Color.White else Color(0xFF0F1115)
    val supportColor = if (isDark) Color(0xFF919194) else Color(0xFF475569)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.shadow(0.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = trans("theme_settings", lang),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = labelColor
                )

                val modes = listOf(
                    Triple("SYSTEM", trans("theme_system", lang), Icons.Default.Settings),
                    Triple("LIGHT", trans("theme_light", lang), Icons.Default.LightMode),
                    Triple("DARK", trans("theme_dark", lang), Icons.Default.DarkMode)
                )

                for ((mode, label, icon) in modes) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(mode) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (currentMode == mode) Color(0xFF3B82F6) else supportColor
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = label,
                                color = if (currentMode == mode) labelColor else supportColor,
                                fontWeight = if (currentMode == mode) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        if (currentMode == mode) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = supportColor)
                    ) {
                        Text(trans("cancel", lang))
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLang: String,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val cardColor = if (isDark) Color(0xFA151821) else Color(0xFAF8FAFC)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
    val labelColor = if (isDark) Color.White else Color(0xFF0F1115)
    val supportColor = if (isDark) Color(0xFF919194) else Color(0xFF475569)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.shadow(0.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            border = BorderStroke(1.dp, cardBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = trans("lang_settings", lang = currentLang),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = labelColor
                )

                val languages = listOf(
                    Triple("en", trans("lang_en", currentLang), Icons.Default.Language),
                    Triple("fa", trans("lang_fa", currentLang), Icons.Default.Language)
                )

                for ((lCode, label, icon) in languages) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelect(lCode) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (currentLang == lCode) Color(0xFF3B82F6) else supportColor
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = label,
                                color = if (currentLang == lCode) labelColor else supportColor,
                                fontWeight = if (currentLang == lCode) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        if (currentLang == lCode) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF3B82F6)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = supportColor)
                    ) {
                        Text(trans("cancel", currentLang))
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeLanguageDialog(
    onSelect: (String) -> Unit
) {
    var selectedLang by remember { mutableStateOf("en") }

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1115)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = trans("choose_lang", selectedLang),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = trans("choose_lang_desc", selectedLang),
                    fontSize = 13.sp,
                    color = Color(0xFF919194),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selectedLang == "en") Color(0xFF3B82F6).copy(alpha = 0.15f)
                                else Color.White.copy(alpha = 0.04f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedLang == "en") Color(0xFF3B82F6) else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedLang = "en" }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "English",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Default Language",
                                color = Color(0xFF919194),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selectedLang == "fa") Color(0xFF3B82F6).copy(alpha = 0.15f)
                                else Color.White.copy(alpha = 0.04f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedLang == "fa") Color(0xFF3B82F6) else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedLang = "fa" }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "فارسی",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "زبان بومی",
                                color = Color(0xFF919194),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = { onSelect(selectedLang) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = trans("confirm", selectedLang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
