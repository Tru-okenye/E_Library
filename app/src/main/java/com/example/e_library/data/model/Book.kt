package com.example.e_library.data.model

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val coverUrl: String = "",
    val pdfUrl: String = "",
    val category: String = "",
    val isFavorite: Boolean = false
)
