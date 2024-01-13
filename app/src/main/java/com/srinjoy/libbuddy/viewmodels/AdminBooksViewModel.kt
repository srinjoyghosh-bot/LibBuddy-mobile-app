package com.srinjoy.libbuddy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srinjoy.libbuddy.data.repository.BookRepository
import com.srinjoy.libbuddy.models.Book
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class AdminBooksViewModel(private val repository: BookRepository) : BaseViewModel() {
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
                        setError(e.message)
                        if (showLoader)
                            stopLoading()
                    }

                })
        )

    }

    fun search(query:String){
        startLoading()
        addDisposable(repository.search(query).observeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object :DisposableSingleObserver<Book.BooksModel>(){
            override fun onSuccess(t: Book.BooksModel) {
                books.value=t.books
                stopLoading()
                setSuccess()
            }

            override fun onError(e: Throwable) {
                stopLoading()
                setError(e.message)
            }
        }))
    }
}

class AdminBooksViewModelFactory(private val repository: BookRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminBooksViewModel::class.java)) {
            return AdminBooksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}