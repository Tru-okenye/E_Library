# 📚 E-Library App

E-Library is a sleek Android app built with **Kotlin** and **Jetpack Compose**, designed to help students and learners **access, browse, favorite, and track books** online.
Admins can upload books via a dedicated interface, and users can manage their personal reading journey with "Currently Reading" and "Already Read" features.

## 🎯 Key Features

- 🔐 **User Authentication** using Firebase Auth
- 📁 **Book Uploads** (Admin)
  - Upload PDF and metadata (title, author, category, thumbnail)
- 📖 **Browse Books** by category or search
- 🧡 **Favorite Books**
  - Add/remove from personal list
- 🕒 **Track Reading Progress**
  - Add to **Currently Reading**
  - Mark as **Already Read** (prevents book from being opened again)
- 🔍 **Search Functionality**
- ✨ **Shimmer Loading Effect** while fetching books (Accompanist)

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **Firebase Services**:
  - Authentication
  - Firestore (books, users)
  - Firebase Storage (book files & images)
- **Libraries**:
  - Accompanist (for shimmer effects)
  - Coil (image loading)
  - Coroutines / Flow (for async data)
  - Firebase Kotlin SDK



