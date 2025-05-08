package com.example.e_library.ui.theme.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.e_library.navigation.Routes
import com.example.e_library.ui.theme.components.BookItem
import com.example.e_library.viewmodel.BookViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_library.ui.theme.components.BookItemPlaceholder
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavController, viewModel: BookViewModel = viewModel()) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val books by viewModel.books.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Persisted Dark Theme Preference
    val darkModePref by ThemePreferenceManager.getDarkModePreference(context).collectAsState(initial = false)
    val isDarkTheme = darkModePref



    // Save to datastore when toggled
    fun toggleTheme() {
        scope.launch {
            ThemePreferenceManager.saveDarkModePreference(context, !isDarkTheme)
        }
    }

    // Redirect if not logged in
    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }
    }

    val filteredBooks = books.filter {
        val matchesSearch = it.title.contains(searchQuery, ignoreCase = true) || it.author.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
        matchesSearch && matchesCategory
    }



    // Apply theme wrapper
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 👉 Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 80.dp, top = 16.dp, end = 16.dp, bottom = 16.dp) // adjust for sidebar
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Available Books", style = MaterialTheme.typography.headlineMedium)

                    TextButton(onClick = {
                        auth.signOut()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }) {
                        Text("Logout", color = Color.Red)
                    }
                }
//                Button(
//                    onClick = {
//                        navController.navigate(Routes.PROFILE)  // Navigate to Profile Screen
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Go to Profile")
//                }


                IconButton(onClick = {
                    navController.navigate(Routes.PROFILE)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                }


                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by title or author") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (books.isEmpty()) {
                    // Show shimmer placeholders
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(5) {
                            BookItemPlaceholder()
                        }
                    }
                } else if (filteredBooks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No results found", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredBooks) { book ->
                            BookItem(book = book, onClick = {
                                navController.navigate("${Routes.DETAILS}/${book.id}")
                            })
                        }
                    }
                }


            }

            // 👉 Overlay Sidebar with Theme Toggle
            CategorySidebar(
                onCategorySelected = { category ->
                    selectedCategory = category
                },
                isDarkTheme = isDarkTheme,
                navController = navController,
                onToggleTheme = { toggleTheme() },
                modifier = Modifier
                    .fillMaxHeight()
                    .zIndex(2f)
            )
        }


    }
}
