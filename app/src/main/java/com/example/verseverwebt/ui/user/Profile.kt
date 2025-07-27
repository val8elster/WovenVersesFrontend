package com.example.verseverwebt.ui.user

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.api.ApiClient
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Profile : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("user_name", "Unknown") ?: "Unknown"

        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProfileContent(userName, logUserId)
                }
            }
        }
    }
}

@Composable
fun ProfileContent(userName: String, userId: Long) {
    var times by remember { mutableStateOf(FloatArray(7) { 0f }) }
    var totalTime by remember { mutableStateOf(0f) }

    LaunchedEffect(userId) {
        val fetchedTimes = mutableListOf<Float>()
        for (chapter in 1..7) {
            fetchChapterTime(userId, chapter)?.let { fetchedTimes.add(it) }
        }
        times = fetchedTimes.toFloatArray()
        totalTime = fetchedTimes.sum()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackToMenuButton()

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Profile",
            style = CustomTypography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            style = CustomTypography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        times.forEachIndexed { index, time ->
            //format: 2 digits after . in float
            val textTime = "%.2f".format(time)
            Text(
                text = "Time for Chapter ${index + 1}: $textTime",
                style = CustomTypography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val textTime = "%.2f".format(totalTime)

        Text(
            text = "Total time: $textTime",
            style = CustomTypography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

private suspend fun fetchChapterTime(userId: Long, chapter: Int): Float? {
    return withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.instance.getChapterTime(userId, chapter).execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
