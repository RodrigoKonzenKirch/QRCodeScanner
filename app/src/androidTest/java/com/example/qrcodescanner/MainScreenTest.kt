package com.example.qrcodescanner

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainScreenTest{

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()

    }

    @Test
    fun assertQrCodeButton_isDisplayed() {


        composeTestRule.setContent {
            val scannedValue: MutableState<String?> = remember {
                mutableStateOf("")
            }
            MainScreen(scannedValue = scannedValue)
        }

        composeTestRule.onNodeWithText( context.getString(R.string.button_qr_code)).assertIsDisplayed()

    }

    @Test
    fun assertTitle_isDisplayed() {
        composeTestRule.setContent {
            val scannedValue: MutableState<String?> = remember {
                mutableStateOf("")
            }
            MainScreen(scannedValue = scannedValue)
        }

        composeTestRule.onNodeWithText(context.getString(R.string.title)).assertIsDisplayed()
    }

    @Test
    fun assertScannedValue_isDisplayed() {
        composeTestRule.setContent {
            val scannedValue: MutableState<String?> = remember {
                mutableStateOf("test")
            }
            MainScreen(scannedValue = scannedValue)
        }

        composeTestRule.onNodeWithText("test").assertIsDisplayed()

    }


}