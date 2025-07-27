package com.example.verseverwebt.ui.chapters

import android.content.Context
import android.content.SharedPreferences
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.ui.general.PageNumber
import com.example.verseverwebt.ui.general.ToTheNextPage
import com.example.verseverwebt.api.ApiClient
import com.example.verseverwebt.ui.general.playfair
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Chapter1 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Access to Audio manager
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // Sets the system volume and media volume to 0 at the beginning
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0) //For real mobile phone
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0) //For emulator

        // Start the timer
        startTime = System.currentTimeMillis()

        // Content of the page
        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Chapter1Content()
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
}

@Composable
fun Chapter1Content() {
    val context = LocalContext.current

    var textSize by remember { mutableStateOf(5.sp) }
    var showDialog by remember { mutableStateOf(false) }

    var achieved by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                // Gets current volume levels
                val systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
                val musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                // Uses the maximum volume to set the text size
                val volume = maxOf(systemVolume, musicVolume)
                textSize = (5 + volume * 2).sp
                if (textSize >= 20.sp) {
                    levelTime = stopTimer()
                    showDialog = true
                    achieved = true
                }
            }
        }
        context.contentResolver.registerContentObserver(
            android.provider.Settings.System.CONTENT_URI,
            true,
            contentObserver
        )
        onDispose {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackToMenuButton()
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "CHAPTER",
            style = CustomTypography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "One",
            style = CustomTypography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 66.dp)
        )
        Text(
            text = "You hear whispering:",
            style = CustomTypography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "In the sea of riddles hidden, lies a sparkling prize,\nWisdom and cleverness are the keys to the skies.\nUnlocking the brilliance with insight so grand,\nTo reveal the treasure within your hand.",
            fontFamily = playfair,
            style = MaterialTheme.typography.bodySmall,
            fontSize = textSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(all = 26.dp)
        )

    }
    PageNumber("-10-")

    //The button is now available to switch to the next chapter
    if(achieved) {
        ToTheNextPage(nextClass = Chapter2::class.java)
    }

    if (showDialog) {
        val userId = getUserId(context)
        val time = levelTime.toFloat() / 1000

        saveTimeIfNotSaved(userId, 1, time)

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


//global variables and functions for time

var startTime: Long = 0
var endTime: Long = 0
var levelTime: Long = 0

fun stopTimer(): Long {
    endTime = System.currentTimeMillis()
    return endTime - startTime
}

//checks, if chapter has been played before and saves the time only if it hasn't
fun saveTimeIfNotSaved(userId: Long, i: Int, time: Float) {
    ApiClient.instance.getChapterTime(userId, i).enqueue(object : Callback<Float> {
        override fun onResponse(call: Call<Float>, response: Response<Float>) {
            if (response.isSuccessful) {
                val existingTime = response.body() ?: 0f
                if (existingTime == 0f) {
                    ApiClient.instance.updateChapterTime(userId, i, time).enqueue(object : Callback<Unit> {
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            if (response.isSuccessful) {
                                Log.d("Chapter $i", "Saved time successfully")
                            } else {
                                Log.e("Chapter $i", "Error with saving")
                            }
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Log.e("Chapter $i", "Error")
                        }
                    })
                }
            } else {
                Log.e("Chapter $i", "Error with getting chapter time")
            }
        }

        override fun onFailure(call: Call<Float>, t: Throwable) {
            Log.e("Chapter $i", "Error with getting chapter time")
        }
    })
}

//global getUserId method to ensure the logged in user is available everywhere
fun getUserId(context: Context): Long {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getLong("user_id", 0L)

}