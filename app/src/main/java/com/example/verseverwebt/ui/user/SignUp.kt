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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.verseverwebt.ui.general.BackToMenuButton
import com.example.verseverwebt.ui.general.MainMenu
import com.example.verseverwebt.valueobjects.permissions.PermissionManager
import com.example.verseverwebt.api.ApiClient
import com.example.verseverwebt.ui.chapters.ChapterIntro
import com.example.verseverwebt.ui.theme.CustomTypography
import com.example.verseverwebt.ui.theme.PurpleBookmark
import com.example.verseverwebt.ui.theme.VerseVerwebtTheme
import com.example.verseverwebt.valueobjects.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VerseVerwebtTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SignUpContent(onSignUpSuccess = { user ->
                        saveLoginState(user)
                    })
                }
            }
        }
    }

    //also a loginstate saving function, since login is embedded
    private fun saveLoginState(user: User) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", true)
            putLong("user_id", user.id)
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
}

@Composable
fun SignUpContent(onSignUpSuccess: (User) -> Unit) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
            text = "Sign Up",
            style = CustomTypography.titleLarge,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Name", style = CustomTypography.bodyMedium) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email", style = CustomTypography.bodyMedium) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", style = CustomTypography.bodyMedium) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(text = "Confirm Password", style = CustomTypography.bodyMedium) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
        PermissionQuery()

        Button(
            onClick = {
                //check for permissions
                if(!PermissionManager.isPermissionAllowed()){
                    dialogMessage = "You have to agree the Permissions"
                    showDialog = true
                }
                //check for passwords
                else if (password != confirmPassword || password.isEmpty()) {
                    dialogMessage = "Passwords do not match"
                    showDialog = true
                } else {
                    val apiService = ApiClient.instance

                    //perform signup: check if unique constraint of name is met
                    apiService.checkIfExistsName(name).enqueue(object : Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            if (response.isSuccessful && response.body() == true) {
                                dialogMessage = "Username already exists"
                                showDialog = true
                            }
                            //check if unique constraint of mail is met
                            else {
                                apiService.checkIfExistsMail(email).enqueue(object : Callback<Boolean> {
                                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                                        if (response.isSuccessful && response.body() == true) {
                                            dialogMessage = "Email already exists"
                                            showDialog = true
                                        }
                                        //actual signup
                                        else {
                                            performSignUp(name, email, password) { user ->
                                                dialogMessage = "Account created successfully!"
                                                onSignUpSuccess(user)
                                                showDialog = true
                                            }
                                        }
                                    }
                                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                        dialogMessage = "Error checking email: ${t.message}"
                                        showDialog = true
                                    }
                                })
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            dialogMessage = "Error checking username: ${t.message}"
                            showDialog = true
                        }
                    })
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(PurpleBookmark)
        ) {
            Text(
                text = "Sign Up",
                style = CustomTypography.bodyMedium
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
                        if (dialogMessage == "Account created successfully!") {
                            startChapterIntro(context)
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

fun startChapterIntro(context: Context) {
    context.startActivity(Intent(context, ChapterIntro::class.java))
}

//signup also has login built in, so the newly signed up user doesn't have to log in.
fun performSignUp(name: String, email: String, password: String, onSignUpSuccess: (User) -> Unit) {
    val newUser = User(1001, name, email, password, 0f, 0f, 0f, 0f, 0f, 0f, 0f, false, 0)
    ApiClient.instance.createUser(newUser).enqueue(object : Callback<Unit> {
        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
            if (response.isSuccessful) {
                //yay
            } else if (response.code() == 500) {
                Log.e("SignUp", "Error")
            } else {
                Log.e("SignUp", "API call failed with response code: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Unit>, t: Throwable) {
            Log.e("SignUp", "Error: ${t.message}")
        }
    })

    ApiClient.instance.getUsers().enqueue(object : Callback<List<User>> {
        override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
            if (response.isSuccessful) {
                val users = response.body()
                if (users != null) {
                    for (user in users){
                        if(user.name == name){
                            onSignUpSuccess(user)
                        }
                    }
                }
            } else if (response.code() == 500) {
                Log.e("SignUp", "Error")
            } else {
                Log.e("SignUp", "API call failed with response code: ${response.code()}")
            }        }

        override fun onFailure(call: Call<List<User>>, t: Throwable) {
            //cry
        }
    })
}

//This function creates a checkbox query and starts the permission query
@Composable
fun PermissionQuery() {
    val context = LocalContext.current

    var checked by remember { mutableStateOf(false) }

    //activates or deactivates the checkbox depending on whether the permission was granted or denied
    LaunchedEffect(PermissionManager.isPermissionAllowed()) {
        checked = PermissionManager.isPermissionAllowed()
    }

    //Text and checkbox are created in one line
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()

    ){
        Text(
            text = "Please grant sensor permission:",
            style = CustomTypography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                checked = isChecked

                //as soon as the checkbox is pressed, the permission query is started
                if (isChecked) {
                    PermissionManager.requestUserPermissions(
                        context as ComponentActivity,
                        onPermissionsGranted = {
                            checked = true
                        },
                        onPermissionsDenied = {
                            checked = false
                        })
                }
            }
        )
    }

}

//Variable for loggedIn User

var logUserId: Long = 0