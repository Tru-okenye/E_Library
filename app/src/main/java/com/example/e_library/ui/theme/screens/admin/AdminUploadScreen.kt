package com.example.e_library.ui.theme.screens.admin


import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextFieldValue.Companion
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_library.data.firebase.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

@Composable
fun AdminUploadScreen(context: Context, firebaseService: FirebaseService = FirebaseService()) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var coverBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        pdfUri = it
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        it?.let { uri ->
            val stream = context.contentResolver.openInputStream(uri)
            coverBitmap = BitmapFactory.decodeStream(stream)
        }
    }

    var uploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()




    Column(modifier = Modifier.padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Upload a New Book", style = MaterialTheme.typography.headlineMedium)

            TextButton(onClick = {
                auth.signOut()
                (context as? Activity)?.finish() // optional: close current activity
            }) {
                Text("Logout", color = Color.Red)
            }
        }


        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Cover Image")
        }

        coverBitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "Cover", modifier = Modifier.size(120.dp).padding(top = 8.dp))
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { launcher.launch("application/pdf") }) {
            Text("Select PDF File")
        }

        pdfUri?.let {
            Text("PDF selected", color = Color.Green, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && author.isNotBlank() && coverBitmap != null && pdfUri != null) {
                    uploading = true
                    scope.launch {
                        val stream = ByteArrayOutputStream()
                        coverBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                        val success = firebaseService.uploadBook(title, author, stream.toByteArray(), pdfUri!!, context)
                        uploading = false
                        if (success) {
                            title = ""
                            author = ""
                            coverBitmap = null
                            pdfUri = null
                            Toast.makeText(context, "Upload Successful", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Upload Failed", Toast.LENGTH_LONG).show()

//                            e.printStackTrace()
//                            Log.e("FirebaseUpload", "Upload failed: ${e.localizedMessage}")

                        }
                    }
                }
            },
            enabled = !uploading
        ) {
            Text(if (uploading) "Uploading..." else "Upload Book")
        }
    }
}
