package com.example.e_library.ui.theme.screens.details
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.e_library.navigation.Routes
import com.example.e_library.viewmodel.AuthViewModel
import com.google.firebase.Timestamp

@Composable
fun EditProfileScreen(viewModel: AuthViewModel = viewModel(), navController: NavController) {

    // Fetch user profile when screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    val userProfile by viewModel.userProfile.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var favoriteCategories by remember { mutableStateOf(listOf<String>()) }

    // Load and set data once userProfile is available
    LaunchedEffect(userProfile) {
        userProfile?.let {
            fullName = it["fullName"] as? String ?: ""
            username = it["username"] as? String ?: ""
            phone = it["phone"] as? String ?: ""
            bio = it["bio"] as? String ?: ""
            favoriteCategories = it["favoriteCategories"] as? List<String> ?: emptyList()
        }
//        userProfile?.get("dateJoined")?.let {
//            val dateJoined = (it as? Timestamp)?.toDate()?.toString() ?: ""
//            Text("Joined on: $dateJoined", style = MaterialTheme.typography.labelLarge)
//        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") })

        Spacer(modifier = Modifier.height(8.dp))
        Text("Favorite Categories", style = MaterialTheme.typography.labelLarge)

        listOf("Fiction", "Science", "Self-help", "Tech", "History").forEach { category ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = favoriteCategories.contains(category),
                    onCheckedChange = { isChecked ->
                        favoriteCategories = if (isChecked)
                            favoriteCategories + category
                        else
                            favoriteCategories - category
                    }
                )
                Text(category)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val updatedProfile = mapOf(
                    "fullName" to fullName,
                    "username" to username,
                    "phone" to phone,
                    "bio" to bio,
                    "favoriteCategories" to favoriteCategories
                )
                viewModel.saveUserProfile(viewModel.currentUser?.uid ?: "", updatedProfile)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Profile")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancel")
        }
    }
}
