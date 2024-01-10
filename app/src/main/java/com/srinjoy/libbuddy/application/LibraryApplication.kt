package com.srinjoy.libbuddy.application

import android.app.Application
import com.srinjoy.libbuddy.data.repository.AdminRepository
import com.srinjoy.libbuddy.data.repository.BookRepository
import com.srinjoy.libbuddy.data.repository.StudentRepository
import com.srinjoy.libbuddy.data.service.LibraryApiService
import com.srinjoy.libbuddy.data.service.SharedPrefs

class LibraryApplication : Application() {
    private val api by lazy {
        LibraryApiService()
    }

    val prefs by lazy {
        SharedPrefs(applicationContext)
    }

    val bookRepository by lazy {
        BookRepository(api)
    }

    val studentRepository by lazy {
        StudentRepository(api)
    }

    val adminRepository by lazy {
        AdminRepository(api)
    }
}