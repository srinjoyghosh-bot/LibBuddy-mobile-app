package com.srinjoy.libbuddy.viewmodels

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.BookRepository
import com.srinjoy.libbuddy.models.Book
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentBooksViewModel(private val repository: BookRepository) : BaseViewModel() {
    val books = MutableLiveData<List<Book.Book>>();

    fun getAllBooks(showLoader: Boolean = true) {
        if (showLoader)
            startLoading()
        addDisposable(
            repository.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<Book.BooksModel>() {
                    override fun onSuccess(result: Book.BooksModel) {
                        if (showLoader)
                            stopLoading()
                        books.value = result.books
                        setSuccess()

                    }

                    override fun onError(e: Throwable) {
                        setError(e)
                        if (showLoader)
                            stopLoading()
                    }

                })
        )

    }
    private var searchJob: Job? = null

    fun searchBooksDebounced(searchText: String) {
        searchJob?.cancel()
        startLoading()
        searchJob = viewModelScope.launch {
            delay(500)
            searchBooks(searchText)
        }
    }

    private fun searchBooks(query:String){
        addDisposable(repository.search(query).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : DisposableSingleObserver<Book.BooksModel>(){
            override fun onSuccess(t: Book.BooksModel) {
                books.value=t.books
                stopLoading()
                setSuccess()
            }

            override fun onError(e: Throwable) {
                stopLoading()
                setError(e)
            }
        }))
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