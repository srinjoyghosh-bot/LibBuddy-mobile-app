package com.srinjoy.libbuddy.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.AdminRepository
import com.srinjoy.libbuddy.data.repository.BookRepository
import com.srinjoy.libbuddy.models.Book
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.SubmissionPublisher

class AddEditBookViewModel(private val repository: AdminRepository) : BaseViewModel() {
    fun addBook(
        title: String,
        author: String,
        publisher: String,
        description: String,
        activity: Activity
    ) {
        startLoading()
        val token = (activity.application as LibraryApplication).prefs.token ?: " "
        val book = Book.Book(
            author = author,
            name = title,
            publisher = publisher,
            description = description
        )
        Log.i("Admin token",token)
        addDisposable(
            repository.addBook(book, "Bearer $token").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Book.AddResponseModel>() {
                    override fun onSuccess(t: Book.AddResponseModel) {
                        setSuccess()
                        stopLoading()
                    }

                    override fun onError(e: Throwable) {
                        setError(e.message)
                        stopLoading()
                    }

                })
        )
    }
}

class AddEditBookViewModelFactory(private val repository: AdminRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditBookViewModel::class.java)) {
            return AddEditBookViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}