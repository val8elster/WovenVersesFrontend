package com.example.verseverwebt.ui.chapters


import android.hardware.SensorManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.verseverwebt.ui.general.AnimatedTypewriterText2
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.ui.general.PageNumber
import com.example.verseverwebt.ui.general.ToTheNextPage
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme

//The class inherits from the SensorEventListener
class Chapter3 : ComponentActivity(), SensorEventListener {
    //a variable of the type SensorManager is created here for future use
    private lateinit var sensorManager: SensorManager
    //a variable of the type lightSensor is created here for future use
    private lateinit var lightSensor: Sensor

    // A changeable boolean variable that returns the winner status
    private var hasWin by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //the sensor manager is initialized when the activity is created
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        //the light sensor is initialized using a get function of the manger when creating the activity
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!

        startTime = System.currentTimeMillis()

        //This function starts the Chapter3Content function and set the Content for this activity
        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Chapter3Content(hasWin)
                }
            }
        }
    }

    //Sensor listener is registered to receive notifications about light sensor changes
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // this function is called when the activity is paused and the sensor is briefly unregister
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    //this function is given by the SensorEventListener interface but is not used here
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    //This function is called up when the data of the light sensor changes
    override fun onSensorChanged(event: SensorEvent?) {
        // Checks whether the lux sensor light value is below 200
        val lightValue = event?.values?.get(0) ?: 0f
        if (lightValue <= 20){
            hasWin = true
        }
    }
}

//This function contains the design for this activity
@Composable
fun Chapter3Content(hasWin: Boolean) {
    //Vertical text elements and a menu button
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
            text = "Three",
            style = CustomTypography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 66.dp)
        )

        // Display the current text
        AnimatedTypewriterText2(
            text = "The sun shines bright, stealing your sight, " +
                    "You must find a way, to not go astray. " +
                    "Shield your eyes from the blinding light, " +
                    "And continue your quest, with all your might. ",
            fontSize = 13,
            textAlign = TextAlign.Center
        )
    }
    PageNumber("-30-")

    //When the puzzle has been solved, the Chapter Win function is triggered
    //And the next button is available to switch to the next chapter
    if(hasWin){
        ToTheNextPage(nextClass = Chapter4::class.java)
        levelTime = stopTimer()
        Chapter3Win()
    }
}

//This function is triggered as soon as the puzzle has been solved
// and then starts a success pop-up
@Composable
fun Chapter3Win(){
    val context = LocalContext.current

    val userId = getUserId(context)
    val time = levelTime.toFloat() / 1000

    val showDialog = remember { mutableStateOf(true) }

    saveTimeIfNotSaved(userId, 3, time)


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