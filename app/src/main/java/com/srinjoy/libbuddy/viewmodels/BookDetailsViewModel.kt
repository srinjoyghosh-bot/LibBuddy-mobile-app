package com.srinjoy.libbuddy.viewmodels

import android.app.Activity
import android.widget.MultiAutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.srinjoy.libbuddy.application.LibraryApplication
import com.srinjoy.libbuddy.data.repository.AdminRepository
import com.srinjoy.libbuddy.data.repository.BookRepository
import com.srinjoy.libbuddy.data.repository.StudentRepository
import com.srinjoy.libbuddy.models.Book
import com.srinjoy.libbuddy.models.Student
import com.srinjoy.libbuddy.view.activities.StudentMainActivity
import com.srinjoy.libbuddy.view.fragments.BookDetailsFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class BookDetailsViewModel(
    private val bookRepository: BookRepository,
    private val studentRepository: StudentRepository? = null,
    private val adminRepository: AdminRepository? = null
) : BaseViewModel() {

    val book = MutableLiveData<Book.Book>()
    val bookRequestSuccess = MutableLiveData<Boolean>()
    val deleteSuccess = MutableLiveData<Boolean>()

    fun getBook(id: String, showLoader: Boolean = true) {
        if (showLoader)
            startLoading()
        addDisposable(
            bookRepository.getBookById(id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<Book.Book>() {
                    override fun onSuccess(t: Book.Book) {
                        book.value = t
                        if (showLoader)
                            stopLoading()
                        setSuccess()
                    }

                    override fun onError(e: Throwable) {
                        if (showLoader)
                            stopLoading()
                        setError(e.message)
                    }
                })
        )
    }

    fun requestBorrow(id: String, fragment: BookDetailsFragment) {
        startLoading()
        val token = (fragment.activity?.application as LibraryApplication?)!!.prefs.token
        addDisposable(
            studentRepository!!.borrowRequest(
                Student.BorrowRequestBodyModel(id),
                "Bearer $token"
            ).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribeWith(object :
                    DisposableSingleObserver<Student.BorrowRequestResponseModel>() {
                    override fun onSuccess(t: Student.BorrowRequestResponseModel) {
                        bookRequestSuccess.value = true
                        stopLoading()
                    }

                    override fun onError(e: Throwable) {
                        stopLoading()
                        setError(e.message.toString())
                    }

                })
        )
    }

    fun deleteBook(id: String, fragment: Fragment) {
        startLoading()
        val token = (fragment.activity?.application as LibraryApplication?)!!.prefs.token
        addDisposable(
            adminRepository!!.deleteBook(id, "Bearer $token").subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Book.DeleteResponseModel>() {
                    override fun onSuccess(t: Book.DeleteResponseModel) {
                        stopLoading()
                        deleteSuccess.value=true
                    }

                    override fun onError(e: Throwable) {
                        stopLoading()
                        setError(e.message)
                    }
                })
        )
    }
}

class BookDetailsViewModelFactory(
    private val bookRepository: BookRepository,
    private val studentRepository: StudentRepository? = null,
    private val adminRepository: AdminRepository? = null,
    private val activity: Activity
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookDetailsViewModel::class.java)) {
            if (activity is StudentMainActivity)
                return BookDetailsViewModel(
                    bookRepository,
                    studentRepository = studentRepository
                ) as T
            return BookDetailsViewModel(bookRepository, adminRepository = adminRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}