package com.example.e_library.ui.theme.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.e_library.data.model.Book
import com.example.e_library.ui.theme.components.BookItem
import com.example.e_library.ui.theme.components.BookItemPlaceholder
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.runtime.LaunchedEffect
import com.example.e_library.data.firebase.FavoritesRepository
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.e_library.navigation.Routes
import com.example.e_library.ui.theme.screens.home.CategorySidebar
import com.example.e_library.ui.theme.screens.home.ThemePreferenceManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, onBack: () -> Unit) {
    var favorites by remember { mutableStateOf<List<Book>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val darkModePref by ThemePreferenceManager.getDarkModePreference(context).collectAsState(initial = false)
    val isDarkTheme = darkModePref

    fun toggleTheme() {
        scope.launch {
            ThemePreferenceManager.saveDarkModePreference(context, !isDarkTheme)
        }
    }

    LaunchedEffect(Unit) {
        FavoritesRepository.getUserFavorites {
            favorites = it
            loading = false
        }
    }

    // 🔧 Apply theme here
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Your Favorite Books") }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                })
            }
        ) { padding ->
            if (loading) {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(5) {
                        BookItemPlaceholder()
                    }
                }
            } else if (favorites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No favorites yet.")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(favorites) { book ->
                        BookItem(book = book, onClick = {
                            navController.navigate("${Routes.DETAILS}/${book.id}")
                        })
                    }
                }
            }

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
