package com.example.e_library.ui.theme.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.e_library.data.model.Book
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.e_library.data.firebase.FavoritesRepository
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer


@Composable
fun BookItem(book: Book, onClick: () -> Unit) {

    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(book.id) {
        FavoritesRepository.isFavorite(book.id) {
            isFavorite = it
        }
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter(book.coverUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(text = book.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "by ${book.author}", style = MaterialTheme.typography.bodyMedium)
            }

            IconButton(onClick = {
                isFavorite = !isFavorite
                FavoritesRepository.toggleFavorite(book, isFavorite) {}
            }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            }
        }
    }
}

@Composable
fun BookItemPlaceholder() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(end = 16.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Loading title...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer()),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Loading author...",
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                        .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer()),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
