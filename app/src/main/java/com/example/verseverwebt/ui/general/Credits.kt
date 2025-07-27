package com.example.verseverwebt.ui.general

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme

class Credits : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This function starts the CreditContent function and set the Content for this activity

        setContent {
            VerseVerwebtTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreditsContent()
                }
            }
        }
    }
}

//This function creates a scrollable view with many text elements for the credits
@Composable
fun CreditsContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { BackToMenuButton() }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        item {
            Text(
                text = "THE",
                style = CustomTypography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
        item {
            Text(
                text = "CREDITS",
                style = CustomTypography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 66.dp)
            )
        }
        item {
            Text(
                text = "Developer: ",
                style = CustomTypography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
        item {
            Text(
                text = "Sophie Brand, Shirin Erol, Val Müller",
                style = CustomTypography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp, start = 50.dp, end = 50.dp)
            )
        }
        item {
            Text(
                text = "Project leader: ",
                style = CustomTypography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        item {
            Text(
                text = " Prof. Dr. Thorsten Teschke ",
                style = CustomTypography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp, start = 50.dp, end = 50.dp)
            )
        }
        item {
            Text(
                text = "Used images:  ",
                style = CustomTypography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
        item {
            Text(
                text = "https://www.vecteezy.com/png/8507495-lens-flare-light-special-effect \n" +
                        "https://www.vecteezy.com/png/26758496-treasure-png-with-ai-generated \n",
                style = CustomTypography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp, start = 50.dp, end = 50.dp)
            )
        }
        item {
            Text(
                text = "Used sounds:  ",
                style = CustomTypography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
        item {
            Text(
                text = "https://pixabay.com/de/sound-effects/handle-paper-foley-4-184013/",
                style = CustomTypography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp, start = 50.dp, end = 50.dp)
            )
        }
        item {
            PageNumber("-80-")
        }
    }
}