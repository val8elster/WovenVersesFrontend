package com.example.verseverwebt.ui.general

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.verseverwebt.R
import com.example.verseverwebt.ui.theme.CustomTypography
import kotlinx.coroutines.delay

//Creates buttons with the same style
@Composable
fun ButtonColumn(primaryText: String, fontSize: TextUnit, onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.padding(5.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = primaryText,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = playfair,
                fontSize = fontSize,
                textAlign = TextAlign.Center
            )
        }
    }
}

//specifically for greyed out text in table of contents
@Composable
fun GreyButtonColumn(primaryText: String, fontSize: TextUnit, onClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.padding(5.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = primaryText,
                fontSize = fontSize,
                fontFamily = playfair,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

//Function that animates the text flashing
@Composable
fun AnimatedTextFlashing(text: String, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Text(
        text = text,
        style = CustomTypography.bodyMedium.copy(fontSize = 13.sp),
        textAlign = TextAlign.Center,
        color = color.copy(alpha = alpha),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

//Button with an icon that is reused in every Chapter
//Leads to main menu
@Composable
fun BackToMenuButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { context.startActivity(Intent(context, MainMenu::class.java)) }
        ) {
            //Button icon
            Icon(
                painter = painterResource(id = R.drawable.bookmark_icon), //path to icon
                contentDescription = "Back to Main Menu",
                tint = Color.Unspecified //ensures that icon does not change color
            )
        }
    }
}

//A function that defines a Button that can be used for each chapter to go to the next page
@Composable
fun ToTheNextPage(nextClass: Class<*>) {
    val context = LocalContext.current

    //Depending on the DarkTheme, a different image is used for the button
    val picture : Painter = if(!isSystemInDarkTheme()){
        painterResource(id = R.drawable.next_white)
    }else {
        painterResource(id = R.drawable.next_black)
    }

    //a clickable image is created
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = picture,
            contentDescription = "nextButton",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .then(
                    Modifier.clickable {
                        playTurnPageSound(context)
                        context.startActivity(Intent(context, nextClass))
                    }
                )
        )
    }
}

//This function plays a sound that imitates the turning of a book
fun playTurnPageSound(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.turnpage).apply {
        isLooping = false
    }
    mediaPlayer.start()
    mediaPlayer.setOnCompletionListener {
        mediaPlayer.release()
    }
}


//This function creates the page number and can be reused for each chapter
@Composable
fun PageNumber(pageNumber: String) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = pageNumber,
            style = CustomTypography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

}

//Text animation function that fades in the text
@Composable
fun AnimatedFadeInText(
    text: String,
    fontSize: Int,
    textAlign: TextAlign,
    color: Color,
    modifier: Modifier = Modifier
) {
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }

    //Effect
    LaunchedEffect(text) {
        displayedText = ""
        currentIndex = 0
        for (char in text) {
            displayedText += char
            currentIndex++
            delay(50) // Adjust the delay to control the typing speed
        }
    }

    Row(modifier = modifier.padding(all = 50.dp)) {
        text.forEachIndexed { index, char ->
            val alpha = animateFloatAsState(
                targetValue = if (index < currentIndex) 1f else 0f,
                animationSpec = tween(
                    durationMillis = 1000, // Duration of the fade-in effect
                    easing = LinearEasing
                )
            )
            Text(
                text = char.toString(),
                fontFamily = playfair,
                style = MaterialTheme.typography.bodySmall,
                fontSize = fontSize.sp,
                textAlign = textAlign,
                color = color.copy(alpha = alpha.value),
                modifier = modifier
            )
        }
    }
}

//Text animation function with a typewriter effect
@Composable
fun AnimatedTypewriterText(
    text: String,
    fontSize: Int,
    textAlign: TextAlign,
    color: Color,
    modifier: Modifier = Modifier
) {
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }

    //Effect
    LaunchedEffect(text) {
        displayedText = ""
        currentIndex = 0
        for (char in text) {
            displayedText += char
            currentIndex++
            delay(50) // Adjust the delay to control the typing speed
        }
    }

    val visibleChars = displayedText.mapIndexed { index, char ->
        val alpha = animateFloatAsState(if (index < currentIndex) 1f else 0f)
        char to alpha.value
    }

    Text(
        text = buildAnnotatedString {
            visibleChars.forEach { (char, alpha) ->
                withStyle(style = SpanStyle(color = color.copy(alpha = alpha))) {
                    append(char)
                }
            }
        },
        fontFamily = playfair,
        style = MaterialTheme.typography.bodySmall,
        fontSize = fontSize.sp,
        textAlign = textAlign,
        modifier = modifier,
        color = color
    )
}

//Text animation function with a typewriter effect
//but with some other parameter
@Composable
fun AnimatedTypewriterText2(
    text: String,
    fontSize: Int,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }

    //Effect
    LaunchedEffect(text) {
        displayedText = ""
        currentIndex = 0
        for (char in text) {
            displayedText += char
            currentIndex++
            delay(50) // Adjust the delay to control the typing speed
        }
    }

    val visibleChars = displayedText.mapIndexed { index, char ->
        val alpha = animateFloatAsState(if (index < currentIndex) 1f else 0f)
        char to alpha.value
    }

    Text(
        text = buildAnnotatedString {
            visibleChars.forEach { (char, alpha) ->
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha))) {
                    append(char)
                }
            }
        },
        fontFamily = playfair,
        style = MaterialTheme.typography.bodySmall,
        fontSize = fontSize.sp,
        textAlign = textAlign,
        modifier = modifier
            .padding(start = 50.dp, end = 50.dp)
    )
}




