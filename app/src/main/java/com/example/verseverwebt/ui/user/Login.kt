package com.example.verseverwebt.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.ui.general.MainMenu
import com.example.verseverwebt.api.ApiClient
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.PurpleBookmark
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import com.example.verseverwebt.valueobjects.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LoginContent(onLoginSuccess = { user ->
                        saveLoginState(user)
                    })
                }
            }
        }
    }

    //saved variables pertaining to user in current context
    private fun saveLoginState(user: User) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", true)
            putString("user_name", user.name)
            putInt("user_rank", user.rank)
            putLong("user_id", user.id)
            putString("user_times", userTimesToString(user))
            apply()
        }
    }

    private fun userTimesToString(user: User): String {
        return floatArrayOf(user.time1, user.time2, user.time3, user.time4, user.time5, user.time6, user.time7)
            .joinToString(",")
    }

    fun navigateToMainMenu() {
        val intent = Intent(this, MainMenu::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun LoginContent(onLoginSuccess: (User) -> Unit) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

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
            text = "Login",
            style = CustomTypography.titleLarge,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(
                text = "Username",
                style = CustomTypography.bodyMedium
            )
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(
                text = "Password",
                style = CustomTypography.bodyMedium
            )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )


        Button(
            onClick = {
                val apiService = ApiClient.instance

                //checks if the user exists and then performs login. different errors for wrong username or password.
                apiService.checkIfExistsName(username).enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful && response.body() == true) {
                            performLogin(username, password) { user ->
                                dialogMessage = "Login successful!"
                                onLoginSuccess(user)
                                showDialog = true
                            }
                        } else {
                            dialogMessage = "User with username $username does not exist"
                            showDialog = true
                        }
                    }

                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        dialogMessage = "Error checking username: ${t.message}"
                        showDialog = true
                    }
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(PurpleBookmark)
        ) {
            Text(
                text = "Login",
                style = CustomTypography.bodyMedium
            )
        }
        Button(
            onClick = {
                context.startActivity(Intent(context, SignUp::class.java))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Text(
                text = "-Sign up-",
                style = CustomTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        if (dialogMessage == "Login successful!") {
                            (context as? Login)?.navigateToMainMenu()
                        }
                    }
                ) {
                    Text(text = "OK", style = CustomTypography.bodyMedium)
                }
            },
            text = {
                Text(text = dialogMessage, style = CustomTypography.bodyMedium)
            }
        )
    }
}

//login function, compares passwords.
fun performLogin(username: String, password: String, onLoginSuccess: (User) -> Unit) {
    ApiClient.instance.getUsers().enqueue(object : Callback<List<User>> {
        override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
            if (response.isSuccessful) {
                val users = response.body() ?: emptyList()
                val user = users.find { it.name == username && it.password == password }
                if (user != null) {
                    onLoginSuccess(user)
                    logUserId = user.id
                } else {
                    Log.e("Login", "Wrong password")
                }
            } else {
                Log.e("Login", "API call failed with response code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<List<User>>, t: Throwable) {
            Log.e("Login", "Error fetching users: ${t.message}")
        }
    })
}
