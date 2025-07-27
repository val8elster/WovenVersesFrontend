package com.example.verseverwebt.ui.general

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.verseverwebt.api.ApiClient
import com.example.verseverwebt.ui.chapters.getUserId
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//table of contest for navigation through the chapters
//chapters get unlocked when the previous chapter is cleared
class TableOfContents : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //content of the page
        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TableOfContentsContent()
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TableOfContentsContent() {
    //current context
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackToMenuButton()
        //Title
        Text(
            text = "Chapters",
            fontFamily = playfair,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 45.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Column {
            //fetches loggedIn user
            val user = getUserId(context)

            (1..7).forEach { chapter ->
                var chapterStatus by remember { mutableIntStateOf(-1) }

                LaunchedEffect(user, chapter) {
                    chapterStatus = withContext(Dispatchers.IO) {
                        getIfChapter(user, chapter)
                    }
                }

                //switches if the chapter was completed or not, only playable if chapter has been completed previously.
                when (chapterStatus) {
                    0 -> {
                        ButtonColumn("Chapter $chapter", 18.sp) {
                            context.startActivity(
                                Intent(
                                    context,
                                    Class.forName("com.example.verseverwebt.ui.chapters.Chapter$chapter")
                                )
                            )
                        }
                    }

                    1 -> {
                        GreyButtonColumn("Chapter $chapter", 18.sp) {
                            // Do nothing
                        }
                    }

                    else -> {
                        Log.e("TableOfContents", "Error fetching Chapter $chapter")
                    }
                }
            }
        }
    }
}

//checks, if the chapter was played before
suspend fun getIfChapter(user: Long, chapter: Int): Int {
    return try {
        val chapterTimeResponse = ApiClient.instance.getChapterTime(user, chapter).execute()
        if (chapterTimeResponse.isSuccessful) {
            val chapterTime = chapterTimeResponse.body()
            if (chapterTime == 0.0F) {
                Log.d("TableOfContents", "Chapter $chapter is locked")
                1
            } else {
                Log.d("TableOfContents", "Chapter $chapter is available")
                0
            }
        } else {
            Log.e("TableOfContents", "Error fetching Chapter $chapter: ${chapterTimeResponse.errorBody()?.string()}")
            2
        }
    } catch (e: Exception) {
        Log.e("TableOfContents", "Exception fetching Chapter $chapter", e)
        2
    }
}