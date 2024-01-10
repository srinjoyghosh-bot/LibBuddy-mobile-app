package com.srinjoy.libbuddy.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srinjoy.libbuddy.data.repository.BookRepository
import com.srinjoy.libbuddy.models.Book
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class StudentBooksViewModel(private val repository: BookRepository) : BaseViewModel() {
    val books = MutableLiveData<List<Book.Book>>();

    fun getAllBooks(showLoader: Boolean = true) {
        if (showLoader)
            startLoading()
        addDisposable(
            repository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<Book.AllBooksModel>() {
                    override fun onSuccess(result: Book.AllBooksModel) {
                        if (showLoader)
                            stopLoading()
                        books.value = result.books
                        setSuccess()

                    }

                    override fun onError(e: Throwable) {
                        setError(e.message)
                        if (showLoader)
                            stopLoading()
                    }

                })
        )

    }


}

class StudentBooksViewModelFactory(private val repository: BookRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentBooksViewModel::class.java)) {
            return StudentBooksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}