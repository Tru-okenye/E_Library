package com.example.e_library.ui.theme.screens.home


import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_library.viewmodel.CategoryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_library.data.firebase.FirebaseService
import com.example.e_library.viewmodel.BookViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector



@Composable
fun CategorySidebar(
    firebaseService: FirebaseService = FirebaseService(),
    onCategorySelected: (String) -> Unit,
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var isExpanded by remember { mutableStateOf(false) } // Now it's collapsed by default

    val scope = rememberCoroutineScope()

    // Category-to-icon map
    val categoryIcons: Map<String, ImageVector> = mapOf(
        "All" to Icons.Default.Menu,
        "Science" to Icons.Default.Star,
        "Fiction" to Icons.Default.Face,
        "obsession" to Icons.Default.Person
    )

    LaunchedEffect(Unit) {
        scope.launch {
            val fetched = firebaseService.getCategories()
            categories = listOf("All") + fetched
        }
    }

    val sidebarWidth by animateDpAsState(
        targetValue = if (isExpanded) 220.dp else 60.dp,
        label = "Sidebar Width"
    )

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxHeight()
            .width(sidebarWidth)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            // Collapse / Expand Button
            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowLeft else Icons.Default.KeyboardArrowRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isExpanded) {
                Text("Categories", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Category List
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                val icon = categoryIcons[category] ?: Icons.Default.Menu
                val animatedColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            selectedCategory = category
                            isExpanded = false
                            onCategorySelected(category)
                        },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            horizontal = if (isExpanded) 16.dp else 8.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = animatedColor
                        )
                        if (isExpanded) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = category, color = animatedColor)
                        }
                    }
                }
            }

            // Favorites Button
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isExpanded) 12.dp else 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favorites",
                    tint = MaterialTheme.colorScheme.primary
                )
                if (isExpanded) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            isExpanded = false // Collapse the sidebar
                            navController.navigate("favorites") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Favorites", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Pushes theme toggle to bottom

            // Theme Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = if (isExpanded) 12.dp else 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Theme",
                    tint = MaterialTheme.colorScheme.primary
                )
                if (isExpanded) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dark Mode")
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {
                        onToggleTheme()
                        isExpanded = false // Collapse the sidebar
                    })
            }
        }
    }
}