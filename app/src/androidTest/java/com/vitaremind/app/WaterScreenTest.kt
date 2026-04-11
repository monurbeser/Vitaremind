package com.vitaremind.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.vitaremind.app.ui.water.components.WaterCircularProgress
import com.vitaremind.app.ui.theme.VitaRemindTheme
import org.junit.Rule
import org.junit.Test

class WaterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun circularProgress_displaysConsumedAndGoal() {
        composeTestRule.setContent {
            VitaRemindTheme {
                WaterCircularProgress(consumed = 1200, goal = 2000)
            }
        }
        composeTestRule.onNodeWithText("1200 ml").assertIsDisplayed()
        composeTestRule.onNodeWithText("of 2000 ml goal").assertIsDisplayed()
    }

    @Test
    fun circularProgress_showsZeroWhenEmpty() {
        composeTestRule.setContent {
            VitaRemindTheme {
                WaterCircularProgress(consumed = 0, goal = 2000)
            }
        }
        composeTestRule.onNodeWithText("0 ml").assertIsDisplayed()
    }
}
