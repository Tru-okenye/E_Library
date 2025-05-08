package com.example.e_library.ui.theme.screens.details


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.e_library.viewmodel.BookViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_library.navigation.Routes
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun BookDetailsScreen(
    bookId: String,
    navController: NavController,
    viewModel: BookViewModel = viewModel()
) {
    val book = viewModel.books.collectAsState().value.find { it.id == bookId }

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Book not found")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = book.coverUrl,
            contentDescription = null,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(book.title, style = MaterialTheme.typography.headlineMedium)
        Text("By ${book.author}", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Optional: Display PDF open button or summary if available
        Text("This is a placeholder for book description or more info.")

        // Inside Column
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (book.pdfUrl.isNotEmpty()) {
                    val request = DownloadManager.Request(Uri.parse(book.pdfUrl))
                        .setTitle(book.title)
                        .setDescription("Downloading ${book.title}")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${book.title}.pdf")
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

                    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request)

                    Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No PDF URL found", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download PDF")
        }

        Button(onClick = {
            println("PDF URL: ${book.pdfUrl}")
            navController.navigate(Routes.READER + "/${Uri.encode(book.pdfUrl)}")

        }) {
            Text("Read Book")
        }


    }
}
