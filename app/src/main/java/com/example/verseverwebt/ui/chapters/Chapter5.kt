package com.example.verseverwebt.ui.chapters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.verseverwebt.ui.general.AnimatedTextFlashing
import com.example.verseverwebt.ui.general.AnimatedTypewriterText
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.ui.general.PageNumber
import com.example.verseverwebt.ui.general.ToTheNextPage
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import kotlinx.coroutines.delay
import com.example.verseverwebt.ui.theme.DarkThemeBackground

//Player needs to charge phone
class Chapter5 : ComponentActivity() {
    //a BroadcastReceiver object is created
    //is used to handle broadcast events related to the charging status
    private lateinit var chargingReceiver: BroadcastReceiver

    private lateinit var achieved: MutableState<Boolean>
    private lateinit var isCharging: MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //if battery is already loading the state is set false
        achieved = mutableStateOf(false)
        isCharging = mutableStateOf(false)

        //listener to react to battery status changes
        chargingReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                isCharging.value = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                if (isCharging.value) {
                    achieved.value = true
                }
            }
        }

        //registers charging Receiver
        registerReceiver(chargingReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // Start the timer
        startTime = System.currentTimeMillis()

        //chapter content
        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Chapter5Content(isCharging = isCharging.value, achieved = achieved.value)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("startTime", startTime)
        outState.putLong("endTime", endTime)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        startTime = savedInstanceState.getLong("startTime")
        endTime = savedInstanceState.getLong("endTime")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(chargingReceiver)
    }
}

@Composable
fun Chapter5Content(isCharging: Boolean, achieved: Boolean) {
    val context = LocalContext.current
    var showNextButton by remember { mutableStateOf(false) } // Track the visibility of the button
    var showDialog by remember { mutableStateOf(false) }

    var showInitialText by remember { mutableStateOf(true) }

    //initial and final text
    val initialMessage = "A long journey you have left behind,\nMany nights awake, no rest to find.\nIt's time to lay down and let your mind mend,\nTo regain your energy, my dear friend."
    val finalMessage = "You have done it! Now you regained your energy,\nFor you can continue your path,\nstill a little along the way the treasure to see."

    //background color that changes depending on the status of achievement
    val backgroundColor by animateColorAsState(

        targetValue = when {
            achieved && isSystemInDarkTheme() -> DarkThemeBackground
            achieved && !isSystemInDarkTheme() -> Color.White
            !achieved && isSystemInDarkTheme() -> Color.DarkGray
            else -> Color.DarkGray
        },
        animationSpec = tween(durationMillis = 2000), label = ""
    )
    //text color that changes depending on the status of achievement
    val textColor by animateColorAsState(
        targetValue = when {
            achieved && isSystemInDarkTheme() -> Color.White
            achieved && !isSystemInDarkTheme() -> Color.Black
            !achieved && isSystemInDarkTheme() -> Color.Gray
            else -> Color.Black
        },
        animationSpec = tween(durationMillis = 2000), label = ""
    )

    //Changes boolean if phone is charging
    LaunchedEffect(isCharging) {
        if (isCharging) {
            delay(5000) // delay before changing the text
            showInitialText = false
            //level completed
            levelTime = stopTimer()
            showDialog = true
            // Delay before showing the next button
            delay(1000) // Adjust this value as needed
            showNextButton = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackToMenuButton()

        Spacer(modifier = Modifier.height(32.dp))
        //Title
        Text(
            text = "CHAPTER",
            style = CustomTypography.titleLarge,
            textAlign = TextAlign.Center
        )
        //Subtitle
        Text(
            text = "Five",
            style = CustomTypography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 66.dp)
        )
        //text with flashing animation
        if (showInitialText){
            AnimatedTypewriterText(
                text = initialMessage,
                fontSize = 13,
                textAlign = TextAlign.Center,
                color = textColor,
                modifier = Modifier.padding(all = 16.dp)
            )
        }
        else {
            AnimatedTextFlashing(
                text = finalMessage,
                color = textColor
            )
        }
    }
    PageNumber("-50-")

    //The button is available to switch to the next chapter
    if(showNextButton) {

        ToTheNextPage(nextClass = Chapter6::class.java)
    }
    if (showDialog) {
        val userId = getUserId(context)
        val time = levelTime.toFloat() / 1000

        saveTimeIfNotSaved(userId, 5, time)

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Congratulations!") },
            text = { Text("You completed the chapter in ${levelTime / 1000} seconds.") }
        )
    }
}