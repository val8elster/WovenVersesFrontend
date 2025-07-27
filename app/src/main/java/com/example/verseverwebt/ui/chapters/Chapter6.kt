package com.example.verseverwebt.ui.chapters

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.speech.RecognizerIntent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.verseverwebt.ui.general.AnimatedTypewriterText2
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.ui.general.PageNumber
import com.example.verseverwebt.R
import com.example.verseverwebt.ui.general.ToTheNextPage
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme

class Chapter6 : ComponentActivity() {

    // A changeable boolean variable that returns the winner status
    private var hasWin by mutableStateOf(false)

    //An ActivityResultLauncher object is created
    //This uses the API for activity results to provide the result
    private lateinit var speechRecognizerLauncher: ActivityResultLauncher<Intent>

    //The speechRecognizerLauncher is initialized as soon as the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startTime = System.currentTimeMillis()

        //The registerForActivityResult function is used to register an ActivityResultLauncher with the speechRecognizerLauncher
        //This is a kind of agreement on how the activity is started and the result is handled
        speechRecognizerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            //Checks whether the Recognizer activity was successful
            if (result.resultCode == RESULT_OK) {
                val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                spokenText?.let {

                    //here you can now check the translated speech to text results to see if they match the solution code
                    //if yes, the hasWin boolean is set to true and the recognition is terminated
                    if (it.isNotEmpty()) {
                        val recognizedText = it[0]
                        if (recognizedText.contains("five seven six eight")
                            || recognizedText.contains("5768")
                            || recognizedText.contains("5 7 6 8")) {
                            hasWin = true
                            levelTime = stopTimer()
                            stopSpeechRecognition()
                        }
                    }
                }
            }
        }

        //This function starts the Chapter6Content function and set the Content for this activity
        setContent {
            VerseVerwebtTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Chapter6Content(hasWin = hasWin , startSpeechRecognition = { startSpeechRecognition() })
                }
            }
        }
    }

    // This function is responsible for starting speech recognition
    // and an intent is created for this
    private fun startSpeechRecognition() {
        // If the puzzle has not yet been solved, an intent for speech recognition is created
        //This is a predefined intent (pop-up) from Google to convert voice input into text
        if (!hasWin) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            // Language settings for the intent are set here
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            // Creates a message for the Intent / Google Pop-up
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tell us the code for the lock ")

            //Launches the speechRecognizerLauncher with the Intent
            speechRecognizerLauncher.launch(intent)
        }
    }

    //Ends the speech recognition and unregister the speechRecognizerLauncher
    private fun stopSpeechRecognition() {
        speechRecognizerLauncher.unregister()
    }
}

//This function contains the design for this activity
@Composable
fun Chapter6Content(hasWin: Boolean , startSpeechRecognition: () -> Unit) {
    //Vertical text elements, a menu button, a microphone button
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
            text = "Six",
            style = CustomTypography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 36.dp)
        )

        AnimatedTypewriterText2(
            text = "Deep within the cave, your journey's nearly through, " +
                    "A treasure chest awaits, but locked from view. " +
                    "Gleaming gems and gold, a sight so grand, " +
                    "Yet a secret code you must understand. " +
                    "\n \n" +
                    "Among the echoes, listen well, " +
                    "To ancient rhymes that secrets tell:  " +
                    "In the whispers of time the Voices unfold, " +
                    "Begin with five, as legends foretold. " +
                    "Two steps forth, then one step before. " +
                    "Forward again, two steps more, " +
                    "The path unveiled, the treasure's door. ",
            fontSize = 13,
            textAlign = TextAlign.Center
        )

        ElevatedButton(
            onClick = { startSpeechRecognition()},
        ) {
            Icon(Icons.Rounded.Mic, contentDescription = null)
        }

        //As long as the puzzle has not been solved, a closed image is displayed
        // but if it is, the ChapterWin function is triggered with the animation and more
        if(hasWin){
            LockAnimation()
            Chapter6Win()
        }else {
            Image(
                painter = painterResource(id = R.drawable.lock1),
                contentDescription = "Lock1_Closed"
            )
        }
    }

    PageNumber("-60-")

    //The button is available to switch to the next chapter
    if(hasWin) {
        ToTheNextPage(nextClass = Chapter7::class.java)
    }
}

//This function is triggered as soon as the puzzle has been solved
// and then starts an animation and a success pop-up
@Composable
fun Chapter6Win(){

    val context = LocalContext.current

    val userId = getUserId(context)
    val time = levelTime.toFloat() / 1000

    val showDialog = remember { mutableStateOf(true) }

    saveTimeIfNotSaved(userId, 6, time)

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("OK")
                }
            },
            title = { Text("Congratulations!") },
            text = { Text("You completed the chapter in ${levelTime / 1000} seconds.") }
        )
    }
}

//This function is responsible for the animation of unlocking the lock
@Composable
fun LockAnimation (){
    val context = LocalContext.current

    //An AnimationDrawable is created with several frames that are drawn in succession for a certain period of time
    val animationDrawable = AnimationDrawable().apply {
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock1)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock2)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock3)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock4)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock5)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock6)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock7)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock8)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock9)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock10)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock11)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock12)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock13)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock14)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock15)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock16)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock17)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock18)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock19)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock20)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock21)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock22)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock23)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock24)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock25)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock26)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock27)!!, 450)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock28)!!, 300)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock29)!!, 300)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock30)!!, 300)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock31)!!, 300)
        addFrame(ContextCompat.getDrawable(context, R.drawable.lock32)!!, 300)
        isOneShot  = true
    }

    //An Android view is used here to start and display the images
    // from the animationDrawable object in an animation
    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                setImageDrawable(animationDrawable)
                animationDrawable.start()
            }
        },
        modifier = Modifier
            .offset (y = (-30).dp)
            .scale(0.7f)
    )
}