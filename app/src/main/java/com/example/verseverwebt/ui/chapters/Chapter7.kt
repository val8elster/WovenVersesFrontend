package com.example.verseverwebt.ui.chapters

import androidx.activity.compose.setContent
import android.database.ContentObserver
import android.net.Uri
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.verseverwebt.ui.general.AnimatedTypewriterText2
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.ui.general.PageNumber
import com.example.verseverwebt.R
import com.example.verseverwebt.ui.theme.CustomTypography

class Chapter7 : ComponentActivity() {
    //a variable of the type ContentObserver is created here for future use
    private lateinit var contentObserver: ContentObserver

    // A changeable boolean variable that returns the winner status
    var hasWin by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startTime = System.currentTimeMillis()

        //A handler is created, this is connected to the main thread and can thus perceive constant ui updates
        val handler = Handler(Looper.getMainLooper())

        //The ContentObserver is initialized here and monitors the MediaStore
        contentObserver = object : ContentObserver(handler) {
            //This function is executed as soon as the MediaStore changes
            override fun onChange(newScreenshot: Boolean, uri: Uri?) {
                //Calls the onChange function of the base class
                super.onChange(newScreenshot, uri)
                Log.d("Chapter 7", "Screenshot was taken ")
                //Sets the winner boolean true
                hasWin = true
                levelTime = stopTimer()
            }
        }

        //Registers the created observer with the resolver and thus receives notifications about changes in the media store
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )

        //This function starts the Chapter7Content function and set the Content for this activity
        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Chapter7Content(hasWin)
                }
            }
        }
    }

    //destroys and logs off the observer if the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contentObserver)
    }
}

//This function contains the design for this activity
@Composable
fun Chapter7Content(hasWin: Boolean) {
    //Vertical text elements, a menu button and a picture of the treasure
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
            text = "Seven",
            style = CustomTypography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 66.dp)
        )

        AnimatedTypewriterText2(
            text = "Treasure found, a gleam on the horizon so wide, " +
                    "Cherish the moment before it slips with the tide. " +
                    "Be quick to capture your treasure, " +
                    "Hold it closely as a memory forever, " +
                    "Quickly capture the evidence, " +
                    "So you can keep it in your remembrance. ",
            fontSize = 13,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(id = R.drawable.treasure),
            contentDescription = "treasure",
            modifier = Modifier
                .scale(0.6f)
        )
    }

    PageNumber("-70-")

    //When the puzzle has been solved, the Chapter Win function is triggered
    if(hasWin){
        Chapter7Win()
    }
}

//This function is triggered as soon as the puzzle has been solved
// and then starts a success pop-up
@Composable
fun Chapter7Win() {
    val context = LocalContext.current

    val userId = getUserId(context)
    val time = levelTime.toFloat() / 1000

    val showDialog = remember { mutableStateOf(true) }

    saveTimeIfNotSaved(userId, 7, time)


    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("OK")
                }
            },
            //end message, shows the player that they completed all available levels
            title = { Text("Congratulations!") },
            text = { Text("You completed the chapter in ${levelTime / 1000} seconds. \n \n You found the treasure!") }
        )
    }
}
