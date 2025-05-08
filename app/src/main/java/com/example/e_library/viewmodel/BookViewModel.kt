package com.example.e_library.viewmodel

//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.e_library.data.model.Book
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class BookViewModel : ViewModel() {
//    private val _books = MutableStateFlow<List<Book>>(emptyList())
//    val books: StateFlow<List<Book>> = _books
//
//    init {
//        loadBooks()
//    }
//
//    private fun loadBooks() {
//        viewModelScope.launch {
//            // Dummy books
//            _books.value = listOf(
//                Book(
//                    id = "1",
//                    title = "Atomic Habits",
//                    author = "James Clear",
//                    coverUrl = "https://covers.openlibrary.org/b/id/10594779-L.jpg",
//                    pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
//                ),
//                Book(
//                    id = "2",
//                    title = "The Power of Now",
//                    author = "Eckhart Tolle",
//                    coverUrl = "https://covers.openlibrary.org/b/id/11151062-L.jpg",
//                    pdfUrl = ""
//                )
//            )
//        }
//    }
//}



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_library.data.firebase.FirebaseService
import com.example.e_library.data.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    private val firebaseService = FirebaseService()
    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            val fetchedBooks = firebaseService.getBooks()
            println("Books fetched: $fetchedBooks") // 🔍 Debug log
            _books.value = fetchedBooks
        }
    }

  


}
