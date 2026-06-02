package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.Category
import com.example.data.Task
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val mockCategory = Category(
        id = 1,
        name = "خرید خانه",
        iconName = "ShoppingCart",
        colorHex = "#FF9800"
    )
    val mockTask = Task(
        id = 1,
        title = "خرید اقلام بهداشتی و تغذیه هلال احمر",
        description = "بررسی قفسه شیر خشک و دستمال‌های آنتی باکتریال",
        categoryId = 1,
        status = "TODO",
        priority = 3
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        TaskCardItem(
            task = mockTask,
            category = mockCategory,
            onCompleteChanged = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
