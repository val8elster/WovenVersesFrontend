package com.example.verseverwebt.ui.chapters

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.verseverwebt.ui.general.AnimatedFadeInText
import com.example.verseverwebt.ui.general.AnimatedTypewriterText
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.api.ApiClient
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChapterIntro : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Turn flashlight off on create
        turnOffFlashlight()
        // Content of the page
        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ChapterIntroContent()
                }
            }
        }
    }

    // Function for turning off the flashlight
    private fun turnOffFlashlight() {
        // Access to Camera manager
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        // Sets flashlight mode to false
        cameraManager.setTorchMode(cameraManager.cameraIdList[0], false)
    }
}

@Composable
fun ChapterIntroContent() {
    // Saves status of the flashlight
    var flashlightOn by remember { mutableStateOf(false) }
    // Current context
    val context = LocalContext.current
    // Camera service from current context
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    // Uses DisposableEffect to free resources if the effect is not used anymore
    DisposableEffect(Unit) {
        // Creates a TorchCallback instance to react to changes from the flashlight mode
        val torchCallback = object : CameraManager.TorchCallback() {
            // Is called if flashlight mode changes
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                flashlightOn = enabled // Updates the status of the flashlight
            }
        }
        // Registers TorchCallback at CameraManager
        cameraManager.registerTorchCallback(torchCallback, null)

        // Frees resources if the effect is not used anymore
        onDispose {
            // Removes the TorchCallback from CameraManager
            cameraManager.unregisterTorchCallback(torchCallback)
        }
    }

    val initialText = "The adventure begins,\n The first mystery solved,\n But more awaits you amidst,\n Your bookmark helps you not get lost,\n To keep on your way where you have crossed."
    val placeholderText = "Enlighten the dark..."

    Column(
        // Column alignment

        modifier = Modifier
            .fillMaxSize()
            .background(if (flashlightOn) Color.White else Color.Black), // Background color changes depending on the flashlight status
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if(flashlightOn) Arrangement.spacedBy(8.dp) else Arrangement.Center,
        ) {
        // Title
        if (flashlightOn) {
            BackToMenuButton()
            Spacer(modifier = Modifier.height(32.dp))

            //intro can only be played once per person, so no check. and the method sets the introCompleted boolean to true anytime, anyways.
            ApiClient.instance.updateIntroCompleted(getUserId(context)).enqueue(object :
                Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        Log.d("ChapterIntro", "Saved boolean successfully")
                    } else {
                        Log.e("ChapterIntro", "Error with saving boolean")
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("ChapterIntro", "Error with saving boolean")
                }
            })
        }

        Text(
            // Color changes when flashlight is on
            text = "CHAPTER",
            style = CustomTypography.titleLarge,
            textAlign = TextAlign.Center,
            color = if (flashlightOn) Color.Black else Color.Gray
        )
        // Subtitle
        Text(
            // Color changes when flashlight is on
            text = "Intro",
            style = CustomTypography.titleMedium,
            textAlign = TextAlign.Center,
            color = if (flashlightOn) Color.Black else Color.Gray,
            modifier = Modifier.padding(bottom = 66.dp)
        )
        if(flashlightOn){
            // Success text with typewriter effect
            AnimatedTypewriterText(
                text = initialText,
                fontSize = 13,
                textAlign = TextAlign.Center,
                color = Color.Black,
                )
        }else{
            // Riddle text with Fade in effect
            AnimatedFadeInText(
                text = placeholderText,
                fontSize = 13,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}


