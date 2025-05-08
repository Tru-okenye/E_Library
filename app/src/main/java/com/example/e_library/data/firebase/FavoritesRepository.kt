package com.example.e_library.data.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.e_library.data.model.Book


object FavoritesRepository {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    fun toggleFavorite(book: Book, isFavorite: Boolean, onComplete: (Boolean) -> Unit) {
        val user = auth.currentUser ?: return
        val docRef = firestore.collection("favorites")
            .document(user.uid)
            .collection("books")
            .document(book.id)

        if (isFavorite) {
            docRef.set(book).addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            docRef.delete().addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    fun isFavorite(bookId: String, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser ?: return
        firestore.collection("favorites")
            .document(user.uid)
            .collection("books")
            .document(bookId)
            .get()
            .addOnSuccessListener { doc ->
                onResult(doc.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getUserFavorites(onResult: (List<Book>) -> Unit) {
        val user = auth.currentUser ?: return

        firestore.collection("favorites")
            .document(user.uid)
            .collection("books")
            .get()
            .addOnSuccessListener { snapshot ->
                val books = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
                onResult(books)
            }
            .addOnFailureListener {
                onResult(emptyList()) // Or handle errors as needed
            }
    }

}
