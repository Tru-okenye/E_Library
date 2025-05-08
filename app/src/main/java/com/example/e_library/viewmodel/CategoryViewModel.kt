package com.example.e_library.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Category(val id: String, val name: String)

class CategoryViewModel : ViewModel() {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        FirebaseFirestore.getInstance().collection("categories")
            .get()
            .addOnSuccessListener { result ->
                val catList = result.map { doc ->
                    // Since the category name is stored in the `name` field
                    Category(id = doc.id, name = doc.getString("name") ?: "")
                }
                _categories.value = catList
            }
    }
}

