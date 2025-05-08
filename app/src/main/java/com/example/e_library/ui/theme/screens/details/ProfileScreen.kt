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
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ProfileScreen(viewModel: AuthViewModel = viewModel(), navController: NavController) {
    val userProfile by viewModel.userProfile.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)
        .verticalScroll(rememberScrollState())) {

        Text("User Profile", style = MaterialTheme.typography.headlineMedium)

        userProfile?.let { data ->
            Text("Full Name: ${data["fullName"]}")
            Text("Username: ${data["username"]}")
            Text("Email: ${data["email"]}")
            Text("Phone: ${data["phone"]}")
            Text("Bio: ${data["bio"]}")
            val joinedTimestamp = data["joined"] as? Timestamp
            val joinedDate = joinedTimestamp?.toDate()
            val formattedDate = joinedDate?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            } ?: "Unknown"

            Text("Joined: $formattedDate")

            Text("Favorite Categories: ${(data["favoriteCategories"] as? List<*>)?.joinToString() ?: "None"}")

            val imageUrl = data["profilePicture"] as? String
            imageUrl?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit Button to navigate to Edit Profile Screen
            Button(onClick = { navController.navigate(Routes.EDIT_PROFILE) }) {
                Text("Edit Profile")
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}
