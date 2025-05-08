package com.example.e_library.ui.theme.screens.reader

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

@Composable
fun ReaderScreen(pdfUrl: String) {
    val context = LocalContext.current
    val bitmaps = remember { mutableStateListOf<Bitmap>() }
    var currentPage by remember { mutableStateOf(1) }
    var scale by remember { mutableStateOf(1f) }
    val bookmarks = remember { mutableStateListOf<Int>() }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(pdfUrl) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(pdfUrl)
                val connection = url.openConnection()
                val input = connection.getInputStream()
                val file = File(context.cacheDir, "downloaded.pdf")
                file.outputStream().use { input.copyTo(it) }

                val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fileDescriptor)

                val tempBitmaps = mutableListOf<Bitmap>()
                for (i in 0 until renderer.pageCount) {
                    val page = renderer.openPage(i)
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    tempBitmaps.add(bitmap)
                    page.close()
                }

                renderer.close()
                fileDescriptor.close()

                withContext(Dispatchers.Main) {
                    bitmaps.addAll(tempBitmaps)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to load PDF", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (bitmaps.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column {
            // Top bar with bookmark and dropdown
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (currentPage > 1) currentPage--
                    },
                    enabled = currentPage > 1
                ) {
                    Text("Previous")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Page $currentPage of ${bitmaps.size}", style = MaterialTheme.typography.bodyLarge)

                    IconButton(onClick = {
                        if (bookmarks.contains(currentPage)) {
                            bookmarks.remove(currentPage)
                        } else {
                            bookmarks.add(currentPage)
                        }
                    }) {
                        Icon(
                            imageVector = if (bookmarks.contains(currentPage)) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark"
                        )
                    }

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Show Bookmarks")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            if (bookmarks.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No bookmarks") },
                                    onClick = { expanded = false },
                                    enabled = false
                                )
                            } else {
                                bookmarks.sorted().forEach { page ->
                                    DropdownMenuItem(
                                        text = { Text("Page $page") },
                                        onClick = {
                                            currentPage = page
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < bitmaps.size) currentPage++
                    },
                    enabled = currentPage < bitmaps.size
                ) {
                    Text("Next")
                }
            }

            // Zoomable Page Viewer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            scale *= zoom
                        }
                    }
                    .padding(8.dp)
            ) {
                Image(
                    bitmap = bitmaps[currentPage - 1].asImageBitmap(),
                    contentDescription = "PDF Page",
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer(
                            scaleX = scale.coerceIn(1f, 5f),
                            scaleY = scale.coerceIn(1f, 5f)
                        )
                )
            }
        }
    }
}
