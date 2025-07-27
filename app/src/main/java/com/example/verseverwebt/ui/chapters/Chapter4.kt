package com.example.verseverwebt.ui.chapters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.verseverwebt.ui.general.AnimatedTypewriterText2
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.ui.general.PageNumber
import com.example.verseverwebt.R
import com.example.verseverwebt.ui.general.ToTheNextPage

var oneTime = 0

class Chapter4 : ComponentActivity() {
    //This function starts the Chapter4Content function and the IsDarkModeOn function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startTime = System.currentTimeMillis()

        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Chapter4Content()
                    IsDarkModeOn()
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

//This function contains the design for this activity consisting of
// vertical text elements and a menu button
@Composable
fun Chapter4Content() {
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
            text = "Four",
            style = CustomTypography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 66.dp)
        )

        AnimatedTypewriterText2(
            text = "In the veil of night, where darkness reigns, " +
                    "A gentle touch, and the sky exclaims. " +
                    "What is it, that in the hidden sparks stars alight, " +
                    "Making them shine in the splendor of night? ",
            fontSize = 13,
            textAlign = TextAlign.Center
        )
    }

    PageNumber("-40-")
}

// This function checks whether the light screen state has been changed to the dark screen state
// if this is the case the ChapterWin function is executed
@Composable
fun IsDarkModeOn() {

    //saves the screen status when starting the activity
    var beginningDarkThemeState = false
    if (oneTime == 0) {
        beginningDarkThemeState = isSystemInDarkTheme()
        oneTime++
    }

    //boolean for the winner status and boolean variable for the current screen status
    val hasWin = remember { mutableStateOf(false) }
    val currentDarkThemeState = isSystemInDarkTheme()

    //this function is executed as soon as the currentDarkThemeState has changed
    // and then checks whether it is a different status than initially
    // and also whether it is in the DarkTheme
    //If this is the case, the hasWin boolean is set to true
    LaunchedEffect(currentDarkThemeState) {
        if (beginningDarkThemeState != currentDarkThemeState && currentDarkThemeState) {
            hasWin.value = true
        }
    }

    //If the puzzle is solved and the hasWin boolean is true, the ChapterWin function will start
    //And the button is available to switch to the next chapter
    if (hasWin.value) {
        oneTime = 0
        ToTheNextPage(nextClass = Chapter5::class.java)
        levelTime = stopTimer()
        ChapterWin()
    }
}

//This function is triggered as soon as the puzzle has been solved
// and starts an animation function and also transfers the time values to the database
//and starts the Winner Pop-Up
@Composable
fun ChapterWin () {
    StarAnimation()

    val context = LocalContext.current

    val userId = getUserId(context)
    val time = levelTime.toFloat() / 1000

    val showDialog = remember { mutableStateOf(true) }

    saveTimeIfNotSaved(userId, 4, time)


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

//This function is responsible for the stars twinkling after successfully solving the puzzle
@Composable
fun StarAnimation() {
    //An anim-able variable is set and used to define an animation with keyframes of different lengths
    // to create a star animation where the star slowly changes from visible to invisible
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 10000
                    0f at 3000 with LinearEasing
                    0f at 3000
                    1f at 4000
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }

    //Here the actual star images are created with the help of a for-loop
    // in which they receive an absolute position value from the array
    // and are assigned the previously created animation value
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val xValues = arrayOf(600, 800 , -768, -710, 0, 200, -800, 986)
        val yValues = arrayOf(100, -500, -702, 1020, 1578, 1777, 2300, 2186)

        for (i in xValues.indices) {
            Image(
                painter = painterResource(id = R.drawable.sparkle),
                contentDescription = "Big_Stars",
                modifier = Modifier
                    .scale(0.2f)
                    .absoluteOffset(x = xValues[i].dp, y = yValues[i].dp)
                    .alpha(alpha.value)
            )
        }
    }

    //A second time star images are generated with a different anim-able variable
    // and a different size
    val betta = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        betta.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 8000
                    0f at 4000 with LinearEasing
                    0f at 1000
                    1f at 3000
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val xValues2 = arrayOf(-200, 561, -792, -1346 )
        val yValues2 = arrayOf(0, 1693 , 503, -100)

        for (i in xValues2.indices) {
            Image(
                painter = painterResource(id = R.drawable.sparkle),
                contentDescription = "Medium_Stars",
                modifier = Modifier
                    .scale(0.15f)
                    .absoluteOffset(x = xValues2[i].dp, y = yValues2[i].dp)
                    .alpha(betta.value)
            )
        }
    }

    //A third time star images are generated with a different anim-able variable
    // and a different size
    val gamma = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        gamma.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 6000
                    0f at 3000 with LinearEasing
                    0f at 2000
                    1f at 1000
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val xValues3 = arrayOf(666, 600, -500 , -950, 1700)
        val yValues3 = arrayOf(555, -1600, -1300, 3500, 1800  )

        for (i in xValues3.indices) {
            Image(
                painter = painterResource(id = R.drawable.sparkle),
                contentDescription = "Small_Stars",
                modifier = Modifier
                    .scale(0.1f)
                    .absoluteOffset(x = xValues3[i].dp, y = yValues3[i].dp)
                    .alpha(gamma.value)
            )
        }
    }

    //A fourth time star images are generated with a different anim-able variable
    // and a different size
    val delta = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        delta.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 5000
                    0f at 2000 with LinearEasing
                    0f at 1000
                    1f at 2000
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val xValues4 = arrayOf(0, -1000, -1900, 0, 1498)
        val yValues4 = arrayOf(1400, -2000, 3000, 4500, 3000 )

        for (i in xValues4.indices) {
            Image(
                painter = painterResource(id = R.drawable.sparkle),
                contentDescription = "Small_Fast_Stars",
                modifier = Modifier
                    .scale(0.1f)
                    .absoluteOffset(x = xValues4[i].dp, y = yValues4[i].dp)
                    .alpha(delta.value)
            )
        }
    }
}

