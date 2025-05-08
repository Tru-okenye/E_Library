package com.example.e_library.ui.theme.screens.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.e_library.navigation.Routes
import com.example.e_library.viewmodel.AuthState
import com.example.e_library.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.Timestamp

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.authState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var favoriteCategories by remember { mutableStateOf(setOf<String>()) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        profilePictureUri = it
    }

    val scrollState = rememberScrollState()
    val categories = listOf("Fiction", "Science", "Self-help", "Tech", "History")

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            val uid = viewModel.currentUser?.uid
            if (uid != null) {
//                val joinedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val joinedDate = Timestamp.now()
                val profileData = mapOf(
                    "fullName" to fullName,
                    "username" to username,
                    "phone" to phone,
                    "bio" to bio,
                    "email" to email,
                    "favoriteCategories" to favoriteCategories.toList(),
                    "joined" to joinedDate,
                    "profilePicture" to profilePictureUri?.toString().orEmpty()
                )
                viewModel.saveUserProfile(uid, profileData)
            }

            viewModel.resetState()
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.REGISTER) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // <-- add this
            .padding(24.dp)
            .imePadding(), // handles keyboard push
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") })
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

        Text("Favorite Categories", style = MaterialTheme.typography.labelLarge)
        categories.forEach { category ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = category in favoriteCategories,
                    onCheckedChange = {
                        favoriteCategories = if (it) favoriteCategories + category else favoriteCategories - category
                    }
                )
                Text(category)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Select Profile Picture")
        }

        profilePictureUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.register(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Already have an account? Login")
        }

        when (state) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text((state as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
            else -> {}
        }
    }
}
