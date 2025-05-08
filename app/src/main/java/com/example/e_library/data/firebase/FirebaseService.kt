package com.example.e_library.data.firebase



import android.content.Context
import android.net.Uri
import com.example.e_library.data.model.Book
import com.example.e_library.data.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class FirebaseService {
    private val db = FirebaseFirestore.getInstance()

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getBooks(): List<Book> {
        return try {
            val snapshot = db.collection("books").get().await()
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: return@mapNotNull null
                val author = doc.getString("author") ?: return@mapNotNull null
                val coverUrl = doc.getString("coverUrl") ?: ""
                val pdfUrl = doc.getString("pdfUrl") ?: ""
                val category = doc.getString("category") ?: "Uncategorized"

                Book(id, title, author, coverUrl, pdfUrl, category)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    suspend fun uploadBook(
        title: String,
        author: String,
        coverImage: ByteArray,
        pdfUri: Uri,
        context: Context
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val coverRef = storage.reference.child("book_covers/${title}_$timestamp.jpg")
            val pdfRef = storage.reference.child("books/${title}_$timestamp.pdf")

            // Upload cover image
            val coverUploadTask = coverRef.putBytes(coverImage).await()
            val coverUrl = coverRef.downloadUrl.await().toString()

            // Upload PDF
            val pdfUploadTask = pdfRef.putFile(pdfUri).await()
            val pdfUrl = pdfRef.downloadUrl.await().toString()

            // Metadata
            val bookData = hashMapOf(
                "title" to title,
                "author" to author,
                "coverUrl" to coverUrl,
                "pdfUrl" to pdfUrl,
                "timestamp" to Timestamp.now(),
                "uploaderId" to auth.currentUser?.uid
            )

            // Save metadata to Firestore
            firestore.collection("books").add(bookData).await()

            return@withContext true

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    suspend fun getCategories(): List<String> {
        return try {
            val snapshot = db.collection("books").get().await()
            snapshot.documents.mapNotNull { it.getString("category") }
                .toSet() // ensure uniqueness
                .sorted() // optional: alphabetical sorting
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }




}

